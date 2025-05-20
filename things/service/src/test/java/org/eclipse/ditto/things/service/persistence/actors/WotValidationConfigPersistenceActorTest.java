/*
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
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
package org.eclipse.ditto.things.service.persistence.actors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.actor.PoisonPill;
import org.apache.pekko.actor.Props;
import org.apache.pekko.cluster.Cluster;
import org.apache.pekko.cluster.MemberStatus;
import org.apache.pekko.cluster.pubsub.DistributedPubSubMediator;
import org.apache.pekko.persistence.RecoveryCompleted;
import org.apache.pekko.stream.javadsl.Source;
import org.apache.pekko.testkit.javadsl.TestKit;
import org.apache.pekko.testkit.TestProbe;
import org.apache.pekko.NotUsed;
import org.apache.pekko.testkit.SocketUtil;
import org.eclipse.ditto.base.model.headers.DittoHeaders;
import org.eclipse.ditto.internal.utils.persistence.mongo.streaming.MongoReadJournal;
import org.eclipse.ditto.internal.utils.tracing.DittoTracingInitResource;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.things.model.devops.ImmutableFeatureValidationConfig;
import org.eclipse.ditto.things.model.devops.ImmutableFeatureValidationEnforceConfig;
import org.eclipse.ditto.things.model.devops.ImmutableFeatureValidationForbidConfig;
import org.eclipse.ditto.things.model.devops.ImmutableThingValidationConfig;
import org.eclipse.ditto.things.model.devops.ImmutableThingValidationEnforceConfig;
import org.eclipse.ditto.things.model.devops.ImmutableThingValidationForbidConfig;
import org.eclipse.ditto.things.model.devops.ImmutableWotValidationConfig;
import org.eclipse.ditto.things.model.devops.WotValidationConfigId;
import org.eclipse.ditto.things.model.devops.WotValidationConfigRevision;
import org.eclipse.ditto.things.model.devops.commands.CreateWotValidationConfig;
import org.eclipse.ditto.things.model.devops.commands.DeleteWotValidationConfig;
import org.eclipse.ditto.things.model.devops.commands.DeleteWotValidationConfigResponse;
import org.eclipse.ditto.things.model.devops.commands.ModifyWotValidationConfig;
import org.eclipse.ditto.things.model.devops.commands.RetrieveWotValidationConfig;
import org.eclipse.ditto.things.model.devops.commands.RetrieveWotValidationConfigResponse;
import org.eclipse.ditto.things.model.devops.events.WotValidationConfigCreated;
import org.eclipse.ditto.things.model.devops.events.WotValidationConfigDeleted;
import org.eclipse.ditto.things.model.devops.events.WotValidationConfigModified;
import org.eclipse.ditto.things.model.devops.responses.WotValidationConfigResponse;
import org.eclipse.ditto.things.model.devops.commands.ModifyWotValidationConfigResponse;
import org.eclipse.ditto.things.service.persistence.actors.strategies.commands.WotValidationConfigDData;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;


public class WotValidationConfigPersistenceActorTest extends PersistenceActorTestBase {

    @ClassRule
    public static final DittoTracingInitResource DITTO_TRACING_INIT_RESOURCE =
            DittoTracingInitResource.disableDittoTracing();

    private static final Logger LOGGER = LoggerFactory.getLogger(WotValidationConfigPersistenceActorTest.class);

    @Rule
    public final TestWatcher watchman = new TestedMethodLoggingWatcher(LOGGER);

    private WotValidationConfigId configId;
    private ImmutableWotValidationConfig config;
    private ActorRef supervisorActor;
    private TestProbe pubSubMediatorProbe;
    private Cluster cluster;

    @Mock
    private MongoReadJournal mongoReadJournal;

    private static final DittoHeaders TEST_HEADERS = DittoHeaders.newBuilder()
            .putHeader("ditto-auth-type", "devops")
            .build();

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Use a fixed port for testing
        final int port = 25520; // Standard Pekko port
        final String hostname = "127.0.0.1";

        final Config customConfig = ConfigFactory.empty()
                .withValue("pekko.actor.provider",
                        ConfigValueFactory.fromAnyRef("org.apache.pekko.cluster.ClusterActorRefProvider"))
                .withValue("pekko.cluster.roles",
                        ConfigValueFactory.fromIterable(Arrays.asList(
                                "wot-validation-config-aware",
                                "blocked-namespaces-aware"
                        )))
                .withValue("pekko.remote.artery.canonical.port",
                        ConfigValueFactory.fromAnyRef(port))
                .withValue("pekko.remote.artery.canonical.hostname",
                        ConfigValueFactory.fromAnyRef(hostname))
                .withValue("pekko.cluster.seed-nodes",
                        ConfigValueFactory.fromIterable(Collections.singletonList(
                                "pekko://PekkoTestSystem@" + hostname + ":" + port)))
                .withValue("pekko.cluster.min-nr-of-members",
                        ConfigValueFactory.fromAnyRef(1))
                .withValue("pekko.cluster.auto-down-unreachable-after",
                        ConfigValueFactory.fromAnyRef("0s"))
                .withValue("pekko.persistence.journal.plugin",
                        ConfigValueFactory.fromAnyRef("pekko-contrib-mongodb-persistence-wot-validation-config-journal"))
                .withValue("pekko.persistence.snapshot-store.plugin",
                        ConfigValueFactory.fromAnyRef("pekko-contrib-mongodb-persistence-wot-validation-config-snapshots"))
                .withValue("pekko.persistence.journal.auto-start-journals",
                        ConfigValueFactory.fromIterable(Collections.singletonList("pekko-contrib-mongodb-persistence-wot-validation-config-journal")))
                .withValue("pekko.persistence.snapshot-store.auto-start-snapshot-stores",
                        ConfigValueFactory.fromIterable(Collections.singletonList("pekko-contrib-mongodb-persistence-wot-validation-config-snapshots")))
                .withValue("ditto.persistence.things.activity-check.interval",
                        ConfigValueFactory.fromAnyRef("1s"))
                .withValue("ditto.persistence.things.activity-check.inactive-timeout",
                        ConfigValueFactory.fromAnyRef("2s"))
                .withValue("ditto.tracing.enabled",
                        ConfigValueFactory.fromAnyRef(false))
                .withValue("pekko.test.single-expect-default",
                        ConfigValueFactory.fromAnyRef("10s"))
                .withValue("pekko.test.default-timeout",
                        ConfigValueFactory.fromAnyRef("10s"))
                .withValue("pekko.remote.artery.bind.timeout",
                        ConfigValueFactory.fromAnyRef("5s"))
                .withValue("pekko.remote.artery.bind.hostname",
                        ConfigValueFactory.fromAnyRef(hostname))
                .withValue("pekko.remote.artery.bind.port",
                        ConfigValueFactory.fromAnyRef(port));

        setup(customConfig);

        // Get the cluster and wait for it to be up
        cluster = Cluster.get(actorSystem);
        cluster.join(cluster.selfAddress());

        // Wait for cluster to be up using a simple loop
        final long startTime = System.currentTimeMillis();
        final long timeout = 10000; // 10 seconds
        while (System.currentTimeMillis() - startTime < timeout) {
            if (cluster.selfMember().status().equals(MemberStatus.up())) {
                break;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        assertThat(cluster.selfMember().status()).isEqualTo(MemberStatus.up());

        // Create test probes
        pubSubMediatorProbe = TestProbe.apply("pubSubMediator", actorSystem);
        pubSubMediator = pubSubMediatorProbe.ref();

        configId = WotValidationConfigId.of("ns:test-id");
        final ImmutableThingValidationEnforceConfig thingEnforce = ImmutableThingValidationEnforceConfig.of(true, true, true, true, true);
        final ImmutableThingValidationForbidConfig thingForbid = ImmutableThingValidationForbidConfig.of(false, true, false, true);
        final ImmutableThingValidationConfig thingConfig = ImmutableThingValidationConfig.of(thingEnforce, thingForbid);

        final ImmutableFeatureValidationEnforceConfig featureEnforce = ImmutableFeatureValidationEnforceConfig.of(true, false, true, false, true, false, true);
        final ImmutableFeatureValidationForbidConfig featureForbid = ImmutableFeatureValidationForbidConfig.of(false, true, false, true, false, true);
        final ImmutableFeatureValidationConfig featureConfig = ImmutableFeatureValidationConfig.of(featureEnforce, featureForbid);

        config = ImmutableWotValidationConfig.of(
                configId,
                true,  // enabled
                false, // logWarningInsteadOfFailingApiCalls
                thingConfig,
                featureConfig,
                Collections.emptyList(),
                WotValidationConfigRevision.of(1L),
                Instant.now(),
                Instant.now(),
                false,
                null
        );

        // Mock the mongo read journal to return empty for initial recovery
        when(mongoReadJournal.getLatestEventSeqNo(any())).thenReturn(Source.single(Optional.empty()));
        when(mongoReadJournal.getSmallestEventSeqNo(any())).thenReturn(Source.single(Optional.empty()));

        // Create supervisor actor with required dependencies
        supervisorActor = actorSystem.actorOf(
                WotValidationConfigSupervisorActor.props(pubSubMediator, mongoReadJournal),
                URLEncoder.encode(configId.toString(), StandardCharsets.UTF_8)
        );
    }

    @After
    public void tearDown() {
        if (cluster != null) {
            cluster.leave(cluster.selfAddress());
        }
        if (actorSystem != null) {
            TestKit.shutdownActorSystem(actorSystem);
        }
    }

    private ActorRef createPersistenceActorFor(final WotValidationConfigId configId) {
        return actorSystem.actorOf(
                WotValidationConfigSupervisorActor.props(pubSubMediator, mongoReadJournal),
                URLEncoder.encode(configId.toString(), StandardCharsets.UTF_8)
        );
    }

    @Test
    public void testCreateConfig() {
        new TestKit(actorSystem) {{
            // Given
            final ModifyWotValidationConfig command = ModifyWotValidationConfig.of(configId, config, TEST_HEADERS);

            // When
            supervisorActor.tell(command, getRef());

            // Then
            final ModifyWotValidationConfigResponse response = expectMsgClass(ModifyWotValidationConfigResponse.class);
            assertThat(response.getConfigId().toString()).isEqualTo(configId.toString());

            // Verify the event was published
            pubSubMediatorProbe.expectMsgClass(DistributedPubSubMediator.Publish.class);
        }};
    }

    @Test
    public void testModifyConfig() {
        new TestKit(actorSystem) {{
            // Given
            // First create a config
            final ModifyWotValidationConfig createCommand = ModifyWotValidationConfig.of(configId, config, TEST_HEADERS);
            supervisorActor.tell(createCommand, getRef());
            expectMsgClass(ModifyWotValidationConfigResponse.class);
            pubSubMediatorProbe.expectMsgClass(DistributedPubSubMediator.Publish.class);

            // Then modify it
            final ImmutableThingValidationEnforceConfig newThingEnforce = ImmutableThingValidationEnforceConfig.of(false, false, false, false, false);
            final ImmutableThingValidationForbidConfig newThingForbid = ImmutableThingValidationForbidConfig.of(true, false, true, false);
            final ImmutableThingValidationConfig newThingConfig = ImmutableThingValidationConfig.of(newThingEnforce, newThingForbid);

            final ImmutableFeatureValidationEnforceConfig newFeatureEnforce = ImmutableFeatureValidationEnforceConfig.of(false, true, false, true, false, true, false);
            final ImmutableFeatureValidationForbidConfig newFeatureForbid = ImmutableFeatureValidationForbidConfig.of(true, false, true, false, true, false);
            final ImmutableFeatureValidationConfig newFeatureConfig = ImmutableFeatureValidationConfig.of(newFeatureEnforce, newFeatureForbid);

            final ImmutableWotValidationConfig newConfig = ImmutableWotValidationConfig.of(
                    configId,
                    false, // enabled changed from true to false
                    true,  // logWarningInsteadOfFailingApiCalls changed from false to true
                    newThingConfig,
                    newFeatureConfig,
                    config.getDynamicConfig(),
                    WotValidationConfigRevision.of(2L),
                    config.getCreated().orElse(null),
                    Instant.now(),
                    false,
                    null
            );
            final ModifyWotValidationConfig command = ModifyWotValidationConfig.of(configId, newConfig, TEST_HEADERS);

            // When
            supervisorActor.tell(command, getRef());

            // Then
            final ModifyWotValidationConfigResponse response = expectMsgClass(ModifyWotValidationConfigResponse.class);
            assertThat(response.getConfigId().toString()).isEqualTo(configId.toString());

            // Verify the event was published
            pubSubMediatorProbe.expectMsgClass(DistributedPubSubMediator.Publish.class);
        }};
    }

    @Test
    public void testDeleteConfig() {
        new TestKit(actorSystem) {{
            // Given
            // First create a config
            final ModifyWotValidationConfig createCommand = ModifyWotValidationConfig.of(configId, config, TEST_HEADERS);
            supervisorActor.tell(createCommand, getRef());
            expectMsgClass(ModifyWotValidationConfigResponse.class);
            pubSubMediatorProbe.expectMsgClass(DistributedPubSubMediator.Publish.class);

            // When
            final DeleteWotValidationConfig command = DeleteWotValidationConfig.of(configId, TEST_HEADERS);
            supervisorActor.tell(command, getRef());

            // Then
            final DeleteWotValidationConfigResponse response = expectMsgClass(DeleteWotValidationConfigResponse.class);
            assertThat(response.getConfigId().toString()).isEqualTo(configId.toString());

            // Verify the event was published
            pubSubMediatorProbe.expectMsgClass(DistributedPubSubMediator.Publish.class);

            // Verify config is gone
            final RetrieveWotValidationConfig retrieveCommand = RetrieveWotValidationConfig.of(configId, TEST_HEADERS);
            supervisorActor.tell(retrieveCommand, getRef());
            // Expect NotAccessibleException instead of a response
            expectMsgClass(org.eclipse.ditto.things.model.devops.exceptions.WotValidationConfigNotAccessibleException.class);
        }};
    }

    @Test
    public void testRetrieveConfig() {
        new TestKit(actorSystem) {{
            // Given
            // First create a config
            final ModifyWotValidationConfig createCommand = ModifyWotValidationConfig.of(configId, config, TEST_HEADERS);
            supervisorActor.tell(createCommand, getRef());
            expectMsgClass(ModifyWotValidationConfigResponse.class);
            pubSubMediatorProbe.expectMsgClass(DistributedPubSubMediator.Publish.class);

            // When
            final RetrieveWotValidationConfig command = RetrieveWotValidationConfig.of(configId, TEST_HEADERS);
            supervisorActor.tell(command, getRef());

            // Then
            final RetrieveWotValidationConfigResponse response = expectMsgClass(RetrieveWotValidationConfigResponse.class);
            assertThat(response.getConfigId().toString()).isEqualTo(configId.toString());

            // Verify config values
            final JsonObject retrievedConfig = response.getValidationConfig().asObject();
            final JsonObject thingConfig = retrievedConfig.getValue("thing").get().asObject();
            final JsonObject thingEnforce = thingConfig.getValue("enforce").get().asObject();
            final JsonObject thingForbid = thingConfig.getValue("forbid").get().asObject();
            assertThat(thingEnforce.getValue("thingDescriptionModification").get().asBoolean()).isTrue();
            assertThat(thingEnforce.getValue("properties").get().asBoolean()).isTrue();
            assertThat(thingForbid.getValue("thingDescriptionDeletion").get().asBoolean()).isFalse();
            assertThat(thingForbid.getValue("nonModeledProperties").get().asBoolean()).isTrue();

            final JsonObject featureConfig = retrievedConfig.getValue("feature").get().asObject();
            final JsonObject featureEnforce = featureConfig.getValue("enforce").get().asObject();
            final JsonObject featureForbid = featureConfig.getValue("forbid").get().asObject();
            assertThat(featureEnforce.getValue("featureDescriptionModification").get().asBoolean()).isTrue();
            assertThat(featureEnforce.getValue("presenceOfModeledFeatures").get().asBoolean()).isFalse();
            assertThat(featureForbid.getValue("featureDescriptionDeletion").get().asBoolean()).isFalse();
            assertThat(featureForbid.getValue("nonModeledFeatures").get().asBoolean()).isTrue();
        }};
    }

    @Test
    public void testRecovery() {
        new TestKit(actorSystem) {{
            // Given
            // First create a config
            final ModifyWotValidationConfig createCommand = ModifyWotValidationConfig.of(configId, config, TEST_HEADERS);
            supervisorActor.tell(createCommand, getRef());
            expectMsgClass(ModifyWotValidationConfigResponse.class);
            pubSubMediatorProbe.expectMsgClass(DistributedPubSubMediator.Publish.class);

            // When
            // Stop and restart the supervisor actor
            TestProbe terminationProbe = new TestProbe(actorSystem);
            terminationProbe.watch(supervisorActor);
            actorSystem.stop(supervisorActor);
            terminationProbe.expectTerminated(supervisorActor, scala.concurrent.duration.Duration.create(5, "seconds"));
            supervisorActor = actorSystem.actorOf(
                    WotValidationConfigSupervisorActor.props(pubSubMediator, mongoReadJournal),
                    URLEncoder.encode(configId.toString(), StandardCharsets.UTF_8)
            );

            // Then
            // Wait for recovery to complete
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // Verify config is still there
            final RetrieveWotValidationConfig command = RetrieveWotValidationConfig.of(configId, TEST_HEADERS);
            supervisorActor.tell(command, getRef());
            final RetrieveWotValidationConfigResponse response = expectMsgClass(RetrieveWotValidationConfigResponse.class);
            assertThat(response.getConfigId().toString()).isEqualTo(configId.toString());
            // Optionally, avoid comparing timestamps in the following assertion
            assertThat(response.getValidationConfig()).isEqualTo(config.toJson());
        }};
    }

    @Test
    public void testActivityCheck() {
        new TestKit(actorSystem) {{
            // Given
            // First create a config
            final ModifyWotValidationConfig createCommand = ModifyWotValidationConfig.of(configId, config, TEST_HEADERS);
            supervisorActor.tell(createCommand, getRef());
            expectMsgClass(ModifyWotValidationConfigResponse.class);
            pubSubMediatorProbe.expectMsgClass(DistributedPubSubMediator.Publish.class);

            // When
            // Wait for activity check to run
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // Then
            // Verify config is still there
            final RetrieveWotValidationConfig command = RetrieveWotValidationConfig.of(configId, TEST_HEADERS);
            supervisorActor.tell(command, getRef());
            final RetrieveWotValidationConfigResponse response = expectMsgClass(RetrieveWotValidationConfigResponse.class);
            assertThat(response.getConfigId().toString()).isEqualTo(configId.toString());
        }};
    }
}