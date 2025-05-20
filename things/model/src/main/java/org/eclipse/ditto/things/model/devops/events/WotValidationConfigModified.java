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

import java.time.Instant;
import java.util.function.Predicate;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.base.model.entity.metadata.Metadata;
import org.eclipse.ditto.base.model.headers.DittoHeaders;
import org.eclipse.ditto.base.model.json.JsonParsableEvent;
import org.eclipse.ditto.base.model.json.JsonSchemaVersion;
import org.eclipse.ditto.json.JsonField;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonObjectBuilder;
import org.eclipse.ditto.json.JsonParseException;
import org.eclipse.ditto.json.JsonValue;
import org.eclipse.ditto.things.model.devops.ImmutableWotValidationConfig;
import org.eclipse.ditto.things.model.devops.WotValidationConfigId;

/**
 * Event that is emitted after a WoT (Web of Things) validation configuration has been modified.
 * This event contains the updated configuration and is used to track changes to the WoT validation
 * settings in the event journal.
 *
 * <p>
 * The event is immutable and thread-safe. It is used to notify subscribers about configuration
 * changes and to maintain an audit trail of modifications to the WoT validation settings.
 * </p>
 *
 * @since 3.8.0
 */
@Immutable
@JsonParsableEvent(name = WotValidationConfigModified.NAME, typePrefix = WotValidationConfigEvent.TYPE_PREFIX)
public final class WotValidationConfigModified extends AbstractWotValidationConfigEvent<WotValidationConfigModified>
        implements WotValidationConfigModifiedEvent<WotValidationConfigModified> {

    /**
     * Name of this event.
     * This is used to identify the event type in the event journal and for deserialization.
     */
    public static final String NAME = "wotValidationConfigModified";
    private final ImmutableWotValidationConfig config;

    /**
     * Type of this event.
     * This is the full type identifier including the prefix.
     */
    public static final String TYPE = WotValidationConfigEvent.TYPE_PREFIX + NAME;


    /**
     * Constructs a new {@code WotValidationConfigModified} event.
     *
     * @param configId the ID of the WoT validation configuration that was modified
     * @param config the updated WoT validation configuration
     * @param revision the revision number of the configuration after the modification
     * @param timestamp the timestamp when this event was created
     * @param dittoHeaders the headers of the command which caused this event
     * @param metadata the metadata associated with this event
     * @throws NullPointerException if any non-nullable argument is {@code null}
     */
    private WotValidationConfigModified(final WotValidationConfigId configId,
            final ImmutableWotValidationConfig config,
            final long revision,
            @Nullable final Instant timestamp,
            final DittoHeaders dittoHeaders,
            @Nullable final Metadata metadata) {
        super(TYPE, configId, revision, timestamp, dittoHeaders, metadata);
        this.config = config;
    }

    /**
     * Creates a new {@code WotValidationConfigModified} event.
     *
     * @param configId the ID of the WoT validation configuration that was modified
     * @param config the updated WoT validation configuration
     * @param revision the revision number of the configuration after the modification
     * @param timestamp the timestamp when this event was created
     * @param dittoHeaders the headers of the command which caused this event
     * @param metadata the metadata associated with this event
     * @return the created WotValidationConfigModified event
     * @throws NullPointerException if any non-nullable argument is {@code null}
     */
    public static WotValidationConfigModified of(final WotValidationConfigId configId,
            final ImmutableWotValidationConfig config,
            final long revision,
            @Nullable final Instant timestamp,
            final DittoHeaders dittoHeaders,
            @Nullable final Metadata metadata) {
        return new WotValidationConfigModified(configId, config, revision, timestamp, dittoHeaders, metadata);
    }

    /**
     * Creates a new {@code WotValidationConfigModified} event from a JSON object.
     * This method is used for deserialization of the event from its JSON representation.
     *
     * @param jsonObject the JSON object from which to create the event
     * @param dittoHeaders the headers of the command which caused this event
     * @return the created WotValidationConfigModified event
     * @throws NullPointerException if {@code jsonObject} is {@code null}
     * @throws JsonParseException if the JSON object is not in the expected format
     */
    public static WotValidationConfigModified fromJson(final JsonObject jsonObject, final DittoHeaders dittoHeaders) {
        final String configIdStr = jsonObject.getValueOrThrow(AbstractWotValidationConfigEvent.JsonFields.CONFIG_ID);
        final WotValidationConfigId configId = WotValidationConfigId.of(configIdStr);
        final long revision = jsonObject.getValueOrThrow(org.eclipse.ditto.base.model.signals.events.EventsourcedEvent.JsonFields.REVISION);
        JsonObject configJson = jsonObject.getValueOrThrow(AbstractWotValidationConfigEvent.JsonFields.CONFIG);
        configJson = configJson.toBuilder().set("_revision", revision).build();
        final ImmutableWotValidationConfig config = ImmutableWotValidationConfig.fromJson(configJson);
        final Instant timestamp = jsonObject.getValue(org.eclipse.ditto.base.model.signals.events.Event.JsonFields.TIMESTAMP)
                .map(Instant::parse)
                .orElse(null);
        final Metadata metadata = jsonObject.getValue(org.eclipse.ditto.base.model.signals.events.Event.JsonFields.METADATA)
                .map(JsonValue::asObject)
                .map(Metadata::newMetadata)
                .orElse(null);
        return new WotValidationConfigModified(configId, config, revision, timestamp, dittoHeaders, metadata);
    }

    /**
     * Returns the JSON schema version implemented by this event.
     *
     * @return the JSON schema version
     */
    @Override
    public JsonSchemaVersion getImplementedSchemaVersion() {
        return JsonSchemaVersion.V_2;
    }

    /**
     * Sets the entity (configuration) of this event.
     * This method is used to update the event with a new configuration.
     *
     * @param entity the new configuration as a JSON value
     * @return a new instance of this event with the updated configuration
     * @throws JsonParseException if the entity cannot be parsed as a WoT validation configuration
     */
    @Override
    public WotValidationConfigModified setEntity(final JsonValue entity) {
        final ImmutableWotValidationConfig config = ImmutableWotValidationConfig.fromJson(entity.asObject());
        return of(getEntityId(), config, getRevision(), getTimestamp().orElse(null), getDittoHeaders(),
                getMetadata().orElse(null));
    }

    @Override
    public WotValidationConfigModified setRevision(final long revision) {
        return of(getEntityId(), config, revision, getTimestamp().orElse(null), getDittoHeaders(),
                getMetadata().orElse(null));
    }

    @Override
    public WotValidationConfigModified setDittoHeaders(final DittoHeaders dittoHeaders) {
        return of(getEntityId(), config, getRevision(), getTimestamp().orElse(null), dittoHeaders,
                getMetadata().orElse(null));
    }

    @Override
    protected void appendPayload(final JsonObjectBuilder jsonObjectBuilder,
            final JsonSchemaVersion schemaVersion, final Predicate<JsonField> predicate) {
        super.appendPayload(jsonObjectBuilder, schemaVersion, predicate);
        jsonObjectBuilder.set(AbstractWotValidationConfigEvent.JsonFields.CONFIG, config.toJson(schemaVersion, predicate), predicate);
    }

    @Override
    public ImmutableWotValidationConfig getConfig() {
        return config;
    }

    /**
     * Returns the entity (configuration) of this event as a JSON value.
     *
     * @param schemaVersion the JSON schema version to use
     * @return an optional containing the configuration as a JSON value
     */
    @Override
    public Optional<JsonValue> getEntity(final JsonSchemaVersion schemaVersion) {
        return Optional.of(config.toJson(schemaVersion));
    }
}