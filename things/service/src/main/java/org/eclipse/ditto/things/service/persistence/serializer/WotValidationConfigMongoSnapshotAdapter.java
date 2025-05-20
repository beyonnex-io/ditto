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

import static org.eclipse.ditto.base.model.common.ConditionChecker.checkNotNull;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.time.Instant;
import java.util.Optional;

import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.cluster.pubsub.DistributedPubSub;
import org.eclipse.ditto.base.api.persistence.PersistenceLifecycle;
import org.eclipse.ditto.base.api.persistence.SnapshotTaken;
import org.eclipse.ditto.base.model.json.FieldType;
import org.eclipse.ditto.base.model.json.JsonSchemaVersion;
import org.eclipse.ditto.internal.utils.cluster.DistPubSubAccess;
import org.eclipse.ditto.internal.utils.persistence.mongo.AbstractMongoSnapshotAdapter;
import org.eclipse.ditto.json.JsonFactory;
import org.eclipse.ditto.json.JsonField;
import org.eclipse.ditto.json.JsonFieldDefinition;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonValue;
import org.eclipse.ditto.things.model.devops.ImmutableWotValidationConfig;
import org.eclipse.ditto.things.model.devops.WotValidationConfigId;
import org.eclipse.ditto.things.api.WotValidationConfigSnapshotTaken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;

/**
 * SnapshotAdapter for {@link ImmutableWotValidationConfig} persisted into pekko-persistence snapshot-store.
 * Converts WoT validation config to MongoDB BSON objects and vice versa.
 * <p>
 * This adapter handles the serialization and deserialization of WoT validation config snapshots for persistence.
 * It uses the same base configuration as other snapshot adapters but is specialized for WoT validation configs.
 * </p>
 * <p>
 * The adapter supports publishing snapshot events via the distributed pub/sub system when enabled in the configuration.
 * This allows other parts of the system to react to snapshot events (e.g., for caching or monitoring purposes).
 * </p>
 * <p>
 * Note: The adapter handles both active and deleted WoT validation configs, with proper lifecycle management
 * and revision tracking.
 * </p>
 */
@ThreadSafe
public final class WotValidationConfigMongoSnapshotAdapter extends AbstractMongoSnapshotAdapter<ImmutableWotValidationConfig> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WotValidationConfigMongoSnapshotAdapter.class);

    /**
     * Configuration key for enabling/disabling snapshot event publishing.
     */
    public static final String WOT_VALIDATION_CONFIG_SNAPSHOT_TAKEN_EVENT_PUBLISHING_ENABLED =
            "wot-validation-config-snapshot-taken-event-publishing-enabled";

    private static final JsonFieldDefinition<JsonValue> JSON_CONFIG = JsonFactory.newJsonValueFieldDefinition("config",
            FieldType.REGULAR,
            JsonSchemaVersion.V_2);

    private final ActorRef pubSubMediator;
    private final boolean snapshotTakenEventPublishingEnabled;

    /**
     * Constructs a new {@code WotValidationConfigMongoSnapshotAdapter} using the actor system.
     *
     * @param actorSystem the actor system in which to load the extension
     * @param config the config of the extension containing snapshot event publishing settings
     * @throws NullPointerException if any argument is {@code null}
     */
    @SuppressWarnings("unused")
    public WotValidationConfigMongoSnapshotAdapter(final ActorSystem actorSystem, final Config config) {
        this(DistributedPubSub.get(checkNotNull(actorSystem, "actorSystem")).mediator(), 
             checkNotNull(config, "config"));
    }

    /**
     * Constructs a new {@code WotValidationConfigMongoSnapshotAdapter}.
     *
     * @param pubSubMediator Pekko pubsub mediator with which to publish snapshot events
     * @param config the config containing snapshot event publishing settings
     * @throws NullPointerException if any argument is {@code null}
     */
    public WotValidationConfigMongoSnapshotAdapter(final ActorRef pubSubMediator, final Config config) {
        super(LOGGER);
        this.pubSubMediator = checkNotNull(pubSubMediator, "pubSubMediator");
        this.snapshotTakenEventPublishingEnabled = checkNotNull(config, "config")
                .getBoolean(WOT_VALIDATION_CONFIG_SNAPSHOT_TAKEN_EVENT_PUBLISHING_ENABLED);
    }

    @Override
    protected boolean isDeleted(@Nullable final ImmutableWotValidationConfig snapshotEntity) {
        return null == snapshotEntity;
    }

    @Override
    protected JsonField getDeletedLifecycleJsonField() {
        return JsonField.newInstance(JSON_CONFIG.getPointer().getRoot().orElseThrow(),
                JsonValue.nullLiteral(), JSON_CONFIG);
    }

    @Override
    protected Optional<JsonField> getRevisionJsonField(final ImmutableWotValidationConfig entity) {
        return Optional.of(JsonField.newInstance("revision", JsonValue.of(entity.getRevision())));
    }

    @Override
    protected ImmutableWotValidationConfig createJsonifiableFrom(final JsonObject jsonObject) {
        try {
            return ImmutableWotValidationConfig.fromJson(jsonObject);
        } catch (final Exception e) {
            LOGGER.error("Failed to create WoT validation config from JSON: {}", jsonObject, e);
            throw e;
        }
    }

    @Override
    protected JsonObject convertToJson(final ImmutableWotValidationConfig snapshotEntity) {
        checkNotNull(snapshotEntity, "snapshotEntity");
        try {
            return JsonObject.newBuilder()
                    .set(JSON_CONFIG, snapshotEntity.toJson(JsonSchemaVersion.V_2, FieldType.notHidden()))
                    .build();
        } catch (final Exception e) {
            LOGGER.error("Failed to convert WoT validation config to JSON: {}", snapshotEntity, e);
            throw e;
        }
    }

    @Override
    protected void onSnapshotStoreConversion(final ImmutableWotValidationConfig config, final JsonObject configJson) {
        if (snapshotTakenEventPublishingEnabled) {
            try {
                final WotValidationConfigId configId = WotValidationConfigId.of(config.getConfigId());
                final long revision = config.getRevision().orElseThrow().toLong();
                final var configSnapshotTaken = WotValidationConfigSnapshotTaken.newBuilder(
                                configId,
                                revision,
                                (config.isDeleted() ? PersistenceLifecycle.DELETED : PersistenceLifecycle.ACTIVE),
                                configJson)
                        .timestamp(Instant.now())
                        .build();
                publishConfigSnapshotTaken(configSnapshotTaken);
            } catch (final Exception e) {
                LOGGER.error("Failed to publish snapshot taken event for config: {}", config, e);
                // Don't rethrow - snapshot conversion should not fail due to event publishing
            }
        }
    }

    private void publishConfigSnapshotTaken(final SnapshotTaken<WotValidationConfigSnapshotTaken> snapshotTakenEvent) {
        try {
            final var publish = DistPubSubAccess.publishViaGroup(snapshotTakenEvent.getPubSubTopic(), snapshotTakenEvent);
            pubSubMediator.tell(publish, ActorRef.noSender());
        } catch (final Exception e) {
            LOGGER.error("Failed to publish snapshot taken event: {}", snapshotTakenEvent, e);
            // Don't rethrow - event publishing should not fail the snapshot process
        }
    }
} 