/*
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.ditto.services.utils.persistence.mongo.ops.eventsource;

import java.math.BigInteger;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.eclipse.ditto.model.base.headers.DittoHeaders;
import org.eclipse.ditto.services.utils.config.ConfigUtil;
import org.eclipse.ditto.services.utils.persistence.mongo.suffixes.NamespaceSuffixCollectionNames;
import org.eclipse.ditto.services.utils.persistence.mongo.suffixes.SuffixBuilderConfig;
import org.eclipse.ditto.services.utils.test.mongo.MongoDbResource;
import org.eclipse.ditto.signals.commands.common.purge.PurgeEntities;
import org.eclipse.ditto.signals.commands.common.purge.PurgeEntitiesResponse;
import org.eclipse.ditto.signals.commands.namespaces.PurgeNamespace;
import org.eclipse.ditto.signals.commands.namespaces.PurgeNamespaceResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.cluster.pubsub.DistributedPubSub;
import akka.testkit.javadsl.TestKit;

/**
 * Tests subclasses of {@link AbstractPersistenceOperationsActor} which provide purging by namespace on a eventsource persistence.
 */
public abstract class MongoEventSourceITAssertions {

    private static final Duration EXPECT_MESSAGE_TIMEOUT = Duration.ofSeconds(10);
    private static final Random RANDOM = new Random();

    /**
     * Embedded MongoDB resource.
     */
    private MongoDbResource mongoDbResource;
    private ActorSystem actorSystem;

    @Rule public TestName name = new TestName();

    @Before
    public void startMongoDb() {
        mongoDbResource = new MongoDbResource("localhost");
        mongoDbResource.start();
    }

    @After
    public void tearDown() {
        if (actorSystem != null) {
            TestKit.shutdownActorSystem(actorSystem);
        }
        if (mongoDbResource != null) {
            mongoDbResource.stop();
            mongoDbResource = null;
        }
    }

    protected void assertPurgeNamespaceWithoutSuffix() {
        purgeNamespace(getConfigWithoutSuffixBuilder());
    }

    protected void assertPurgeNamespaceWithSuffix() {
        purgeNamespace(getConfigWithSuffixBuilder());
    }

    protected void assertPurgeEntitiesWithoutNamespace() {
        purgeEntities(getConfigWithoutSuffixBuilder(), false);
    }

    protected void assertPurgeEntitiesWithNamespaceWithoutSuffix() {
        purgeEntities(getConfigWithoutSuffixBuilder(), true);
    }

    protected void assertPurgeEntitiesWithNamespaceWithSuffix() {
        purgeEntities(getConfigWithSuffixBuilder(), true);
    }

    /**
     * @return name of the configured service.
     */
    protected abstract String getServiceName();

    /**
     * @return list of supported persistence ID prefixes - usually a singleton of the actor's resource type.
     */
    protected final List<String> getSupportedPrefixes() {
        return Collections.singletonList(getResourceType());
    }

    /**
     * Start an entity's persistence actor.
     *
     * @param system the actor system.
     * @param pubSubMediator Akka pub-sub mediator.
     * @param id ID of the entity.
     * @return reference to the entity actor.
     */
    protected abstract ActorRef startEntityActor(final ActorSystem system, final ActorRef pubSubMediator,
            final String id);

    /**
     * Starts the NamespaceOps actor.
     *
     * @param actorSystem the actor system.
     * @param pubSubMediator Akka pub-sub mediator.
     * @param config configuration with info about event journal, snapshot store, metadata and database.
     * @return reference of the NamespaceOps actor.
     */
    protected abstract ActorRef startActorUnderTest(final ActorSystem actorSystem, final ActorRef pubSubMediator,
            final Config config);

    /**
     * Get the command to create an entity belonging to the given resource type.
     *
     * @param id ID of the entity.
     * @return Command to create it.
     */
    protected abstract Object getCreateEntityCommand(final String id);

    /**
     * @return type of responses for successful entity creation.
     */
    protected abstract Class<?> getCreateEntityResponseClass();

    /**
     * @return type of responses for retrieval of nonexistent entities.
     */
    protected abstract Class<?> getEntityNotAccessibleClass();

    /**
     * @return type of responses for successful entity retrieval.
     */
    protected abstract Class<?> getRetrieveEntityResponseClass();

    /**
     * Get the command to retrieve an entity belonging to the given resource type.
     *
     * @param id ID of the entity.
     * @return Command to retrieve it.
     */
    protected abstract Object getRetrieveEntityCommand(final String id);

    /**
     * @return resource type of the NamespaceOps actor being tested.
     */
    protected abstract String getResourceType();

    private Config getConfigWithSuffixBuilder() {
        // suffix builder is active by default
        return getEventSourcingConfiguration(ConfigFactory.empty());
    }

    private Config getConfigWithoutSuffixBuilder() {
        final Config configOverride =
                ConfigFactory.parseString("akka.contrib.persistence.mongodb.mongo.suffix-builder.class=\"\"");
        return getEventSourcingConfiguration(configOverride);
    }

    /**
     * Set up configuration required for event-sourcing to work.
     *
     * @param configOverride overriding config options.
     * @return config to feed the actor system and its actors.
     */
    private Config getEventSourcingConfiguration(final Config configOverride) {
        final String databaseName =
                name.getMethodName() + "-" + BigInteger.valueOf(System.currentTimeMillis()).toString(16);
        final String mongoUriValue = String.format("\"mongodb://%s:%s/%s\"\n",
                mongoDbResource.getBindIp(), mongoDbResource.getPort(), databaseName);

        // - do not log dead letters (i. e., events for which there is no subscriber)
        // - bind to random available port
        // - do not attempt to join an Akka cluster
        // - do not shutdown jvm on exit (breaks unit tests)
        // - make Mongo URI known to the persistence plugin and to the NamespaceOps actor
        final String testConfig = "akka.log-dead-letters=0\n" +
                "akka.remote.artery.bind.port=0\n" +
                "akka.cluster.seed-nodes=[]\n" +
                "akka.coordinated-shutdown.exit-jvm=off\n" +
                "ditto.things.log-incoming-messages=true\n" +
                "akka.contrib.persistence.mongodb.mongo.mongouri=" + mongoUriValue +
                "ditto.services-utils-config.mongodb.uri=" + mongoUriValue;

        // load the service config for info about event journal, snapshot store and metadata
        final Config configWithSuffixBuilder = ConfigFactory.parseString(testConfig)
                .withFallback(ConfigUtil.determineConfig(getServiceName()));

        // set namespace suffix config before persisting any event - NullPointerException otherwise
        NamespaceSuffixCollectionNames.setConfig(new SuffixBuilderConfig(getSupportedPrefixes()));

        return configOverride.withFallback(configWithSuffixBuilder);
    }

    private void purgeNamespace(final Config config) {
        actorSystem = startActorSystem(config);
        final DittoHeaders dittoHeaders = DittoHeaders.newBuilder()
                .correlationId(String.valueOf(UUID.randomUUID()))
                .build();

        new TestKit(actorSystem) {{
            final String purgedNamespace = "purgedNamespace.x" + RANDOM.nextInt(1000000);
            final String survivingNamespace = "survivingNamespace.x" + RANDOM.nextInt(1000000);

            final String purgedId = purgedNamespace + ":name";
            final String survivingId = survivingNamespace + ":name";

            final ActorRef pubSubMediator = DistributedPubSub.get(actorSystem).mediator();
            final ActorRef actorToPurge = watch(startEntityActor(actorSystem, pubSubMediator, purgedId));
            final ActorRef survivingActor = watch(startEntityActor(actorSystem, pubSubMediator, survivingId));

            final ActorRef underTest = startActorUnderTest(actorSystem, pubSubMediator, config);

            // create 2 entities in 2 namespaces, 1 of which will be purged
            actorToPurge.tell(getCreateEntityCommand(purgedId), getRef());
            expectCreateEntityResponse(this);

            survivingActor.tell(getCreateEntityCommand(survivingId), getRef());
            expectCreateEntityResponse(this);

            // kill the actor in the namespace to be purged to avoid write conflict
            actorToPurge.tell(PoisonPill.getInstance(), getRef());
            expectTerminated(EXPECT_MESSAGE_TIMEOUT, actorToPurge);

            // purge the namespace
            underTest.tell(PurgeNamespace.of(purgedNamespace, dittoHeaders), getRef());
            expectMsg(EXPECT_MESSAGE_TIMEOUT,
                    PurgeNamespaceResponse.successful(purgedNamespace, getResourceType(), dittoHeaders));

            // restart the actor in the purged namespace - it should work as if its entity never existed
            final ActorRef purgedActor = watch(startEntityActor(actorSystem, pubSubMediator, purgedId));
            purgedActor.tell(getRetrieveEntityCommand(purgedId), getRef());
            expectMsgClass(EXPECT_MESSAGE_TIMEOUT, getEntityNotAccessibleClass());

            // the actor outside the purged namespace should not be affected
            survivingActor.tell(getRetrieveEntityCommand(survivingId), getRef());
            expectMsgClass(getRetrieveEntityResponseClass());
        }};
    }

    private void purgeEntities(final Config config, final boolean prependNamespace) {
        actorSystem = startActorSystem(config);
        final DittoHeaders dittoHeaders = DittoHeaders.newBuilder()
                .correlationId(String.valueOf(UUID.randomUUID()))
                .build();

        new TestKit(actorSystem) {{
            final Random random = new Random();

            final String namespace = "purgedNamespace.x" + random.nextInt(1000000);

            final String purgedId1 = prependNamespace("purgedId1", namespace, prependNamespace);
            final String purgedId2 = prependNamespace("purgedId2", namespace, prependNamespace);
            final String survivingId = prependNamespace("survingId", namespace, prependNamespace);

            final ActorRef pubSubMediator = DistributedPubSub.get(actorSystem).mediator();
            final ActorRef actorToPurge1 = watch(startEntityActor(actorSystem, pubSubMediator, purgedId1));
            final ActorRef actorToPurge2 = watch(startEntityActor(actorSystem, pubSubMediator, purgedId2));
            final ActorRef survivingActor = watch(startEntityActor(actorSystem, pubSubMediator, survivingId));

            final ActorRef underTest = startActorUnderTest(actorSystem, pubSubMediator, config);

            // create 2 entities which will be purged
            actorToPurge1.tell(getCreateEntityCommand(purgedId1), getRef());
            expectCreateEntityResponse(this);

            actorToPurge2.tell(getCreateEntityCommand(purgedId2), getRef());
            expectCreateEntityResponse(this);

            // create one entity which won't be purged
            survivingActor.tell(getCreateEntityCommand(survivingId), getRef());
            expectCreateEntityResponse(this);

            // kill the actors for the entities to be purged to avoid write conflict
            actorToPurge1.tell(PoisonPill.getInstance(), getRef());
            expectTerminated(actorToPurge1);

            actorToPurge2.tell(PoisonPill.getInstance(), getRef());
            expectTerminated(actorToPurge2);

            // purge the 2 entities
            final String entityType = getResourceType();
            final PurgeEntities purgeEntities =
                    PurgeEntities.of(entityType, Arrays.asList(purgedId1, purgedId2), dittoHeaders);
            underTest.tell(purgeEntities, getRef());
            expectMsg(PurgeEntitiesResponse.successful(entityType, dittoHeaders));

            // restart the actors for the purged entities - they should work as if its entity never existed
            final ActorRef purgedActor1 = watch(startEntityActor(actorSystem, pubSubMediator, purgedId1));
            purgedActor1.tell(getRetrieveEntityCommand(purgedId1), getRef());
            expectMsgClass(getEntityNotAccessibleClass());

            final ActorRef purgedActor2 = watch(startEntityActor(actorSystem, pubSubMediator, purgedId2));
            purgedActor2.tell(getRetrieveEntityCommand(purgedId2), getRef());
            expectMsgClass(getEntityNotAccessibleClass());

            // the actor outside the purged namespace should not be affected
            survivingActor.tell(getRetrieveEntityCommand(survivingId), getRef());
            expectMsgClass(getRetrieveEntityResponseClass());
        }};
    }

    private void expectCreateEntityResponse(final TestKit testKit) {
        testKit.expectMsgClass(EXPECT_MESSAGE_TIMEOUT, getCreateEntityResponseClass());
    }

    private ActorSystem startActorSystem(final Config config) {
        final String name = getClass().getSimpleName() + '-' + UUID.randomUUID().toString();
        return ActorSystem.create(name, config);
    }

    private static String prependNamespace(final String id, final String ns, boolean prepend) {
        if (prepend) {
            return ns + ':' + id;
        } else {
            return id;
        }
    }

}
