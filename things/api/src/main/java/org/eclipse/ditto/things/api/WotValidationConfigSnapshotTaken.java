/*
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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
package org.eclipse.ditto.things.api;

import java.time.Instant;
import java.util.Objects;
import java.util.function.Predicate;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;

import org.eclipse.ditto.base.api.persistence.PersistenceLifecycle;
import org.eclipse.ditto.base.api.persistence.SnapshotTaken;
import org.eclipse.ditto.base.model.common.ConditionChecker;
import org.eclipse.ditto.base.model.entity.metadata.Metadata;
import org.eclipse.ditto.base.model.entity.id.NamespacedEntityId;
import org.eclipse.ditto.base.model.headers.DittoHeaders;
import org.eclipse.ditto.base.model.json.FieldType;
import org.eclipse.ditto.base.model.json.JsonParsableEvent;
import org.eclipse.ditto.base.model.json.JsonSchemaVersion;
import org.eclipse.ditto.json.JsonFactory;
import org.eclipse.ditto.json.JsonField;
import org.eclipse.ditto.json.JsonFieldDefinition;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonValue;
import org.eclipse.ditto.things.model.devops.WotValidationConfigId;

/**
 * Event published when a snapshot of a WoT validation config is taken.
 */
@Immutable
@JsonParsableEvent(name = WotValidationConfigSnapshotTaken.NAME, typePrefix = WotValidationConfigSnapshotTaken.TYPE_PREFIX)
public final class WotValidationConfigSnapshotTaken extends SnapshotTaken<WotValidationConfigSnapshotTaken> {

    /**
     * Pub-sub topic of this event.
     */
    public static final String PUB_SUB_TOPIC = "wot-validation-config:snapshottaken";

    /**
     * The resource type of this event.
     */
    static final String RESOURCE_TYPE = "wot-validation-config";

    /**
     * The prefix of this event's type.
     */
    static final String TYPE_PREFIX = RESOURCE_TYPE + ":";

    static final String NAME = "wotValidationConfigSnapshotTaken";

    /**
     * The type of this event.
     */
    static final String TYPE = TYPE_PREFIX + NAME;

    private final WotValidationConfigId configId;

    private WotValidationConfigSnapshotTaken(final Builder builder) {
        super(TYPE,
                builder.revisionNumber,
                builder.timestamp,
                builder.metadata,
                builder.configJson,
                builder.lifecycle,
                builder.dittoHeaders);
        configId = builder.configId;
    }

    /**
     * Returns a new builder with a fluent API for creating a {@code WotValidationConfigSnapshotTaken}.
     *
     * @param configId the ID of the config.
     * @param revisionNumber the revision number of the config.
     * @param lifecycle the lifecycle of the config.
     * @param configJson the JSON representation of the config.
     * @return the builder.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static Builder newBuilder(final WotValidationConfigId configId,
            final long revisionNumber,
            final PersistenceLifecycle lifecycle,
            final JsonObject configJson) {

        return new Builder(ConditionChecker.checkNotNull(configId, "configId"),
                revisionNumber,
                ConditionChecker.checkNotNull(lifecycle, "lifecycle"),
                ConditionChecker.checkNotNull(configJson, "configJson"));
    }

    /**
     * Deserializes a {@code WotValidationConfigSnapshotTaken} instance from the specified JSON object.
     *
     * @param jsonObject the JSON object that should be deserialized.
     * @param dittoHeaders the headers of the deserialized event.
     * @return the deserialized WotValidationConfigSnapshotTaken instance.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws org.eclipse.ditto.json.JsonMissingFieldException – if {@code jsonObject} did not contain all required
     * fields.
     * @throws org.eclipse.ditto.json.JsonParseException if {@code jsonObject} does not represent a valid
     * {@code WotValidationConfigSnapshotTaken}.
     */
    public static WotValidationConfigSnapshotTaken fromJson(final JsonObject jsonObject, final DittoHeaders dittoHeaders) {
        ConditionChecker.checkNotNull(jsonObject, "jsonObject");
        ConditionChecker.checkNotNull(dittoHeaders, "dittoHeaders");

        final var deserializer = JsonDeserializer.of(jsonObject, TYPE);
        return newBuilder(WotValidationConfigId.of(jsonObject.getValueOrThrow(JsonFields.CONFIG_ID)),
                deserializer.deserializeRevision(),
                deserializer.deserializePersistenceLifecycle(),
                deserializer.deserializeEntity())
                .timestamp(deserializer.deserializeTimestamp())
                .metadata(deserializer.deserializeMetadata())
                .dittoHeaders(dittoHeaders)
                .build();
    }

    @Override
    public NamespacedEntityId getEntityId() {
        return configId;
    }

    @Override
    public String getPubSubTopic() {
        return PUB_SUB_TOPIC;
    }

    @Override
    public String getResourceType() {
        return RESOURCE_TYPE;
    }

    @Override
    protected WotValidationConfigSnapshotTaken setDittoHeaders(final DittoHeaders dittoHeaders, final JsonObject entityOfSnapshot) {
        return newBuilder(configId, getRevision(), getLifecycle(), entityOfSnapshot)
                .timestamp(getTimestamp().orElse(null))
                .metadata(getMetadata().orElse(null))
                .dittoHeaders(dittoHeaders)
                .build();
    }

    @Override
    public WotValidationConfigSnapshotTaken setEntity(final JsonValue entity) {
        return this;
    }

    @Override
    public JsonObject toJson(final JsonSchemaVersion schemaVersion, final Predicate<JsonField> predicate) {
        var result = super.toJson(schemaVersion, predicate);
        return JsonFactory.newObjectBuilder(result)
                .set(JsonFields.CONFIG_ID, String.valueOf(getEntityId()), schemaVersion.and(predicate))
                .build();
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final var that = (WotValidationConfigSnapshotTaken) o;
        return Objects.equals(configId, that.configId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), configId);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" +
                super.toString() +
                ", configId=" + configId +
                "]";
    }

    /**
     * Builder for {@code WotValidationConfigSnapshotTaken}.
     */
    @NotThreadSafe
    public static final class Builder {

        private final WotValidationConfigId configId;
        private final long revisionNumber;
        private final PersistenceLifecycle lifecycle;
        private final JsonObject configJson;
        @Nullable private Instant timestamp;
        @Nullable private Metadata metadata;
        private DittoHeaders dittoHeaders;

        private Builder(final WotValidationConfigId configId,
                final long revisionNumber,
                final PersistenceLifecycle lifecycle,
                final JsonObject configJson) {

            this.configId = configId;
            this.revisionNumber = revisionNumber;
            this.lifecycle = lifecycle;
            this.configJson = configJson;
            timestamp = Instant.now();
            metadata = null;
            dittoHeaders = DittoHeaders.empty();
        }

        /**
         * Sets the timestamp when the snapshot was taken. By default the timestamp is set to {@link Instant#now()}.
         *
         * @param timestamp the timestamp to be set or {@code null} if the event does not have a timestamp at all.
         * @return this builder instance.
         */
        public Builder timestamp(@Nullable final Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        /**
         * Sets the specified metadata.
         *
         * @param metadata the metadata to be set or {@code null} if the event has none (default).
         * @return this builder instance.
         */
        public Builder metadata(@Nullable final Metadata metadata) {
            this.metadata = metadata;
            return this;
        }

        /**
         * Sets the specified Ditto headers.
         *
         * @param dittoHeaders the Ditto headers to be set.
         * @return this builder instance.
         * @throws NullPointerException if {@code dittoHeaders} is {@code null}.
         */
        public Builder dittoHeaders(final DittoHeaders dittoHeaders) {
            this.dittoHeaders = ConditionChecker.checkNotNull(dittoHeaders, "dittoHeaders");
            return this;
        }

        /**
         * Builds a new {@code WotValidationConfigSnapshotTaken}.
         *
         * @return the new instance.
         */
        public WotValidationConfigSnapshotTaken build() {
            return new WotValidationConfigSnapshotTaken(this);
        }
    }

    /**
     * JSON field definitions.
     */
    public static final class JsonFields {

        private JsonFields() {
            throw new AssertionError();
        }

        /**
         * JSON field containing the config ID.
         */
        public static final JsonFieldDefinition<String> CONFIG_ID =
                JsonFactory.newStringFieldDefinition("configId", FieldType.REGULAR, JsonSchemaVersion.V_2);

        /**
         * JSON field containing the config JSON.
         */
        public static final JsonFieldDefinition<JsonObject> CONFIG =
                JsonFactory.newJsonObjectFieldDefinition("config", FieldType.REGULAR, JsonSchemaVersion.V_2);

        /**
         * JSON field containing the lifecycle.
         */
        public static final JsonFieldDefinition<String> LIFECYCLE =
                JsonFactory.newStringFieldDefinition("lifecycle", FieldType.REGULAR, JsonSchemaVersion.V_2);
    }
} 