/*
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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
package org.eclipse.ditto.things.model.signals.events;

import java.time.Instant;
import java.util.Objects;
import java.util.function.Predicate;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.base.model.entity.metadata.Metadata;
import org.eclipse.ditto.base.model.headers.DittoHeaders;
import org.eclipse.ditto.base.model.json.FieldType;
import org.eclipse.ditto.base.model.json.JsonParsableEvent;
import org.eclipse.ditto.base.model.json.JsonSchemaVersion;
import org.eclipse.ditto.base.model.signals.commands.Command;
import org.eclipse.ditto.json.JsonFactory;
import org.eclipse.ditto.json.JsonField;
import org.eclipse.ditto.json.JsonFieldDefinition;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonObjectBuilder;
import org.eclipse.ditto.json.JsonPointer;
import org.eclipse.ditto.json.JsonValue;
import org.eclipse.ditto.things.model.Thing;
import org.eclipse.ditto.things.model.ThingId;
import org.eclipse.ditto.things.model.devops.ImmutableWoTValidationConfig;
import org.eclipse.ditto.base.model.signals.events.Event;

/**
 * This event is emitted after a WoT validation configuration was created.
 *
 * @since 3.0.0
 */
@Immutable
@JsonParsableEvent(name = WotValidationConfigCreated.NAME, typePrefix = ThingEvent.TYPE_PREFIX)
public final class WotValidationConfigCreated extends AbstractThingEvent<WotValidationConfigCreated>
        implements ThingModifiedEvent<WotValidationConfigCreated> {

    /**
     * Name of this event.
     */
    public static final String NAME = "wotValidationConfigCreated";

    /**
     * Type of this event.
     */
    public static final String TYPE = ThingEvent.TYPE_PREFIX + NAME;

    static final JsonFieldDefinition<JsonObject> JSON_CONFIG =
            JsonFactory.newJsonObjectFieldDefinition("config", FieldType.REGULAR,
                    JsonSchemaVersion.V_2);

    private final ImmutableWoTValidationConfig config;

    private WotValidationConfigCreated(final ThingId thingId,
            final ImmutableWoTValidationConfig config,
            final long revision,
            @Nullable final Instant timestamp,
            final DittoHeaders dittoHeaders,
            @Nullable final Metadata metadata) {

        super(TYPE, thingId, revision, timestamp, dittoHeaders, metadata);
        this.config = Objects.requireNonNull(config, "The config must not be null!");
    }

    /**
     * Creates a new {@code WotValidationConfigCreated} event.
     *
     * @param thingId the identifier of the Thing to which the created configuration belongs
     * @param config the created configuration
     * @param revision the revision of the Thing.
     * @param timestamp the timestamp of this event.
     * @param dittoHeaders the headers of the command which was the cause of this event.
     * @param metadata the metadata to apply for the event.
     * @return the created WotValidationConfigCreated.
     * @throws NullPointerException if any argument but {@code timestamp} and {@code metadata} is {@code null}.
     */
    public static WotValidationConfigCreated of(final ThingId thingId,
            final ImmutableWoTValidationConfig config,
            final long revision,
            @Nullable final Instant timestamp,
            final DittoHeaders dittoHeaders,
            @Nullable final Metadata metadata) {

        return new WotValidationConfigCreated(thingId, config, revision, timestamp, dittoHeaders, metadata);
    }

    /**
     * Creates a new {@code WotValidationConfigCreated} from a JSON object.
     *
     * @param jsonObject the JSON object from which a new WotValidationConfigCreated instance is to be created.
     * @param dittoHeaders the headers of the command which was the cause of this event.
     * @return the {@code WotValidationConfigCreated} which was created from the given JSON object.
     * @throws NullPointerException if {@code jsonObject} is {@code null}.
     * @throws org.eclipse.ditto.json.JsonParseException if the passed in {@code jsonObject} was not in the expected
     * format.
     */
    public static WotValidationConfigCreated fromJson(final JsonObject jsonObject, final DittoHeaders dittoHeaders) {
        return new WotValidationConfigCreated(
                ThingId.of(jsonObject.getValueOrThrow(ThingEvent.JsonFields.THING_ID)),
                ImmutableWoTValidationConfig.fromJson(jsonObject.getValueOrThrow(JSON_CONFIG)),
                jsonObject.getValueOrThrow(Thing.JsonFields.REVISION),
                jsonObject.getValue(Event.JsonFields.TIMESTAMP).map(Instant::parse).orElse(null),
                dittoHeaders,
                jsonObject.getValue(Event.JsonFields.METADATA).map(Metadata::newMetadata).orElse(null));
    }

    /**
     * Returns the created configuration.
     *
     * @return the created configuration.
     */
    public ImmutableWoTValidationConfig getConfig() {
        return config;
    }

    @Override
    public JsonPointer getResourcePath() {
        return JsonPointer.of("/wotValidationConfig");
    }

    @Override
    public WotValidationConfigCreated setRevision(final long revision) {
        return of(getEntityId(), config, revision, getTimestamp().orElse(null), getDittoHeaders(),
                getMetadata().orElse(null));
    }

    @Override
    public WotValidationConfigCreated setDittoHeaders(final DittoHeaders dittoHeaders) {
        return of(getEntityId(), config, getRevision(), getTimestamp().orElse(null), dittoHeaders,
                getMetadata().orElse(null));
    }

    @Override
    public Command.Category getCommandCategory() {
        return null;
    }

    @Override
    protected void appendPayload(final JsonObjectBuilder jsonObjectBuilder,
            final JsonSchemaVersion schemaVersion,
            final Predicate<JsonField> predicate) {

        jsonObjectBuilder.set(JSON_CONFIG, config.toJson(), predicate);
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
        final WotValidationConfigCreated that = (WotValidationConfigCreated) o;
        return Objects.equals(config, that.config);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), config);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" +
                super.toString() +
                ", config=" + config +
                "]";
    }

    @Override
    public WotValidationConfigCreated setEntity(final JsonValue entity) {
        return null;
    }
}