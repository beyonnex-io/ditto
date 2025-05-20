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
package org.eclipse.ditto.things.service.persistence.serializer;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.persistence.SnapshotMetadata;
import org.apache.pekko.persistence.SnapshotOffer;
import org.apache.pekko.testkit.TestProbe;
import org.apache.pekko.testkit.javadsl.TestKit;
import org.bson.BsonDocument;
import org.eclipse.ditto.base.api.persistence.PersistenceLifecycle;
import org.eclipse.ditto.base.api.persistence.SnapshotTaken;
import org.eclipse.ditto.base.model.headers.DittoHeaders;
import org.eclipse.ditto.internal.utils.cluster.DistPubSubAccess;
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
import org.eclipse.ditto.things.api.WotValidationConfigSnapshotTaken;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.pekko.cluster.pubsub.DistributedPubSubMediator;

public final class WotValidationConfigMongoSnapshotAdapterTest {

    private static final String PERSISTENCE_ID = "wot-validation-config:ditto:global";
    private static final SnapshotMetadata SNAPSHOT_METADATA = new SnapshotMetadata(PERSISTENCE_ID, 0, 0);
    private static final Instant FIXED_INSTANT = Instant.parse("2024-01-01T00:00:00Z");

    @Rule
    public final TestName testName = new TestName();

    private ActorSystem system;
    private TestProbe pubSubProbe;
    private WotValidationConfigMongoSnapshotAdapter underTest;
    private WotValidationConfigId configId;
    private ImmutableWotValidationConfig config;

    @Before
    public void setUp() {
        system = ActorSystem.create(testName.getMethodName());
        pubSubProbe = TestProbe.apply(system);
        underTest = new WotValidationConfigMongoSnapshotAdapter(pubSubProbe.ref(), ConfigFactory.parseMap(
                Map.of(WotValidationConfigMongoSnapshotAdapter.WOT_VALIDATION_CONFIG_SNAPSHOT_TAKEN_EVENT_PUBLISHING_ENABLED, true)
        ));

        configId = WotValidationConfigId.of("ditto:global");
        config = ImmutableWotValidationConfig.of(
                configId,
                true,
                false,
                ImmutableThingValidationConfig.of(
                        ImmutableThingValidationEnforceConfig.of(true, true, true, true, true),
                        ImmutableThingValidationForbidConfig.of(false, true, false, true)
                ),
                ImmutableFeatureValidationConfig.of(
                        ImmutableFeatureValidationEnforceConfig.of(true, false, true, false, true, false, true),
                        ImmutableFeatureValidationForbidConfig.of(false, true, false, true, false, true)
                ),
                Collections.emptyList(),
                WotValidationConfigRevision.of(1L),
                FIXED_INSTANT,
                FIXED_INSTANT,
                false,
                null
        );
    }

    @After
    public void cleanUp() {
        if (system != null) {
            TestKit.shutdownActorSystem(system);
        }
    }

    @Test
    public void toSnapshotStoreFromSnapshotStoreRoundTripReturnsExpected() {
        // Given
        final Object rawSnapshotEntity = underTest.toSnapshotStore(config);

        // Then
        assertThat(rawSnapshotEntity).as("snapshot entity is BSON document").isInstanceOf(BsonDocument.class);

        // When
        final BsonDocument dbObject = (BsonDocument) rawSnapshotEntity;
        final ImmutableWotValidationConfig restoredConfig = underTest.fromSnapshotStore(new SnapshotOffer(SNAPSHOT_METADATA, dbObject));

        // Then
        assertThat(restoredConfig).as("restored config").isEqualTo(config);
        expectSnapshotPublished(config);
    }

    @Test
    public void toSnapshotStoreFromSnapshotStoreRoundTripWithDeletedConfigReturnsExpected() {
        // Given
        final ImmutableWotValidationConfig deletedConfig = ImmutableWotValidationConfig.of(
                configId,
                true,
                true, // deleted
                ImmutableThingValidationConfig.of(
                        ImmutableThingValidationEnforceConfig.of(true, true, true, true, true),
                        ImmutableThingValidationForbidConfig.of(false, true, false, true)
                ),
                ImmutableFeatureValidationConfig.of(
                        ImmutableFeatureValidationEnforceConfig.of(true, false, true, false, true, false, true),
                        ImmutableFeatureValidationForbidConfig.of(false, true, false, true, false, true)
                ),
                Collections.emptyList(),
                WotValidationConfigRevision.of(1L),
                FIXED_INSTANT,
                FIXED_INSTANT,
                false,
                null
        );

        // When
        final Object rawSnapshotEntity = underTest.toSnapshotStore(deletedConfig);

        // Then
        assertThat(rawSnapshotEntity).as("snapshot entity is BSON document").isInstanceOf(BsonDocument.class);

        // When
        final BsonDocument dbObject = (BsonDocument) rawSnapshotEntity;
        final ImmutableWotValidationConfig restoredConfig = underTest.fromSnapshotStore(new SnapshotOffer(SNAPSHOT_METADATA, dbObject));

        // Then
        assertThat(restoredConfig).as("restored deleted config").isEqualTo(deletedConfig);
        expectSnapshotPublished(deletedConfig);
    }

    @Test
    public void snapshotEventPublishingCanBeDisabled() {
        // Given
        final WotValidationConfigMongoSnapshotAdapter adapterWithDisabledPublishing = 
            new WotValidationConfigMongoSnapshotAdapter(pubSubProbe.ref(), ConfigFactory.parseMap(
                Map.of(WotValidationConfigMongoSnapshotAdapter.WOT_VALIDATION_CONFIG_SNAPSHOT_TAKEN_EVENT_PUBLISHING_ENABLED, false)
            ));

        // When
        adapterWithDisabledPublishing.toSnapshotStore(config);

        // Then
        pubSubProbe.expectNoMessage();
    }

    private void expectSnapshotPublished(final ImmutableWotValidationConfig expectedConfig) {
        final var publish = pubSubProbe.expectMsgClass(DistributedPubSubMediator.Publish.class);
        assertThat(publish.topic()).isEqualTo(DistPubSubAccess.getGroupTopic(WotValidationConfigSnapshotTaken.PUB_SUB_TOPIC));
        
        final var snapshotTaken = (WotValidationConfigSnapshotTaken) publish.message();
        assertThat((Object) snapshotTaken.getEntityId()).isEqualTo(configId);
        assertThat(snapshotTaken.getRevision()).isEqualTo(expectedConfig.getRevision().orElseThrow().toLong());
        assertThat(snapshotTaken.getLifecycle()).isEqualTo(
            expectedConfig.isDeleted() ? PersistenceLifecycle.DELETED : PersistenceLifecycle.ACTIVE
        );
        
        final var entityJson = snapshotTaken.getEntity().orElseThrow();
        assertThat(entityJson).isInstanceOf(JsonObject.class);
        final var snapshotJson = (JsonObject) entityJson;
        assertThat(snapshotJson.getValue("config")).isPresent();
    }
} 