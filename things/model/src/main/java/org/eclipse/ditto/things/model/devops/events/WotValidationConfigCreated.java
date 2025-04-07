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
package org.eclipse.ditto.things.model.devops.events;

import static org.eclipse.ditto.base.model.common.ConditionChecker.checkNotNull;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.base.model.entity.id.EntityId;
import org.eclipse.ditto.base.model.entity.id.WithEntityId;
import org.eclipse.ditto.base.model.entity.type.EntityType;
import org.eclipse.ditto.base.model.entity.metadata.Metadata;
import org.eclipse.ditto.base.model.headers.DittoHeaders;
import org.eclipse.ditto.base.model.json.JsonSchemaVersion;
import org.eclipse.ditto.base.model.signals.events.AbstractEvent;
import org.eclipse.ditto.base.model.signals.events.Event;
import org.eclipse.ditto.json.JsonFactory;
import org.eclipse.ditto.json.JsonField;
import org.eclipse.ditto.json.JsonFieldDefinition;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonObjectBuilder;
import org.eclipse.ditto.json.JsonPointer;
import org.eclipse.ditto.json.JsonValue;
import org.eclipse.ditto.things.model.devops.ImmutableWoTValidationConfig;

/**
 * Event that is emitted when a WoT validation config is created.
 */
@Immutable
public final class WotValidationConfigCreated extends AbstractEvent<WotValidationConfigCreated> implements WithEntityId {

    private static final String NAME = "wot-validation-config-created";
    private static final String TYPE = Event.TYPE_QUALIFIER + ":" + NAME;

    private final EntityId entityId;
    private final ImmutableWoTValidationConfig config;

    private WotValidationConfigCreated(final EntityId entityId,
            final ImmutableWoTValidationConfig config,
            @Nullable final Instant timestamp,
            final DittoHeaders dittoHeaders,
            @Nullable final Metadata metadata) {
        super(TYPE, timestamp, dittoHeaders, metadata);
        this.entityId = checkNotNull(entityId, "entityId");
        this.config = checkNotNull(config, "config");
    }

    /**
     * Creates a new {@code WotValidationConfigCreated} event.
     *
     * @param entityId the ID of the entity.
     * @param config the WoT validation config.
     * @param timestamp the timestamp of the event.
     * @param dittoHeaders the headers of the event.
     * @param metadata the metadata of the event.
     * @return the created event.
     * @throws NullPointerException if {@code entityId} or {@code config} is {@code null}.
     */
    public static WotValidationConfigCreated of(final EntityId entityId,
            final ImmutableWoTValidationConfig config,
            @Nullable final Instant timestamp,
            final DittoHeaders dittoHeaders,
            @Nullable final Metadata metadata) {
        return new WotValidationConfigCreated(entityId, config, timestamp, dittoHeaders, metadata);
    }

    /**
     * Creates a new {@code WotValidationConfigCreated} from a JSON object.
     *
     * @param jsonObject the JSON object of which the event is to be created.
     * @param dittoHeaders the headers of the event.
     * @return the created event.
     * @throws NullPointerException if {@code jsonObject} is {@code null}.
     */
    public static WotValidationConfigCreated fromJson(final JsonObject jsonObject,
            final DittoHeaders dittoHeaders) {
        final EntityId entityId = EntityId.of(EntityType.of("wot-validation-config"), 
                jsonObject.getValueOrThrow(JsonFields.ENTITY_ID));
        final ImmutableWoTValidationConfig config = ImmutableWoTValidationConfig.fromJson(
                jsonObject.getValueOrThrow(JsonFields.CONFIG));
        final Instant timestamp = jsonObject.getValue(JsonFields.TIMESTAMP)
                .map(Instant::parse)
                .orElse(null);
        final Metadata metadata = jsonObject.getValue(JsonFields.METADATA)
                .map(Metadata::newMetadata)
                .orElse(null);
        return of(entityId, config, timestamp, dittoHeaders, metadata);
    }

    @Override
    public EntityId getEntityId() {
        return entityId;
    }

    public ImmutableWoTValidationConfig getConfig() {
        return config;
    }

    @Override
    public JsonPointer getResourcePath() {
        return JsonPointer.empty();
    }

    @Override
    public String getResourceType() {
        return "wot-validation-config";
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected void appendPayload(final JsonObjectBuilder jsonObjectBuilder,
            final JsonSchemaVersion schemaVersion,
            final Predicate<JsonField> thePredicate) {
        final Predicate<JsonField> predicate = schemaVersion.and(thePredicate);
        jsonObjectBuilder.set(JsonFields.ENTITY_ID, entityId.toString());
        jsonObjectBuilder.set(JsonFields.CONFIG, config.toJson());
        if (getTimestamp().isPresent()) {
            jsonObjectBuilder.set(JsonFields.TIMESTAMP, getTimestamp().get().toString());
        }
        getMetadata().ifPresent(metadata -> jsonObjectBuilder.set(JsonFields.METADATA, metadata.toJson()));
    }

    @Override
    public WotValidationConfigCreated setDittoHeaders(final DittoHeaders dittoHeaders) {
        return of(entityId, config, getTimestamp().orElse(null), dittoHeaders, getMetadata().orElse(null));
    }

    @Override
    public WotValidationConfigCreated setEntity(final JsonValue entity) {
        return this;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final WotValidationConfigCreated that = (WotValidationConfigCreated) o;
        return that.canEqual(this) &&
                Objects.equals(entityId, that.entityId) &&
                Objects.equals(config, that.config) &&
                Objects.equals(getTimestamp(), that.getTimestamp()) &&
                Objects.equals(getDittoHeaders(), that.getDittoHeaders()) &&
                Objects.equals(getMetadata(), that.getMetadata());
    }

    @Override
    protected boolean canEqual(@Nullable final Object other) {
        return other instanceof WotValidationConfigCreated;
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityId, config, getTimestamp(), getDittoHeaders(), getMetadata());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" +
                "entityId=" + entityId +
                ", config=" + config +
                ", timestamp=" + getTimestamp() +
                ", dittoHeaders=" + getDittoHeaders() +
                ", metadata=" + getMetadata() +
                "]";
    }

    private static final class JsonFields {
        private static final JsonFieldDefinition<String> ENTITY_ID =
                JsonFieldDefinition.ofString("entityId");
        static final JsonFieldDefinition<JsonObject> CONFIG =
                JsonFieldDefinition.ofJsonObject("config");
        static final JsonFieldDefinition<String> TIMESTAMP =
                JsonFieldDefinition.ofString("timestamp");
        static final JsonFieldDefinition<JsonObject> METADATA =
                JsonFieldDefinition.ofJsonObject("metadata");
    }
} 