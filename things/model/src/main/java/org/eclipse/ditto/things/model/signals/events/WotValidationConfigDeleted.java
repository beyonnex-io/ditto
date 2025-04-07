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
package org.eclipse.ditto.things.model.signals.events;

import static org.eclipse.ditto.base.model.common.ConditionChecker.checkNotNull;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Predicate;

import org.eclipse.ditto.base.model.entity.metadata.Metadata;
import org.eclipse.ditto.base.model.headers.DittoHeaders;
import org.eclipse.ditto.base.model.json.FieldType;
import org.eclipse.ditto.base.model.json.JsonSchemaVersion;
import org.eclipse.ditto.base.model.signals.commands.Command;
import org.eclipse.ditto.base.model.signals.events.EventJsonDeserializer;
import org.eclipse.ditto.base.model.signals.WithOptionalEntity;
import org.eclipse.ditto.json.JsonField;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonObjectBuilder;
import org.eclipse.ditto.json.JsonPointer;
import org.eclipse.ditto.json.JsonValue;
import org.eclipse.ditto.things.model.ThingId;

/**
 * This event is emitted after a WoT validation config was deleted.
 *
 * @since 3.0.0
 */
@Immutable
public final class WotValidationConfigDeleted extends AbstractThingEvent<WotValidationConfigDeleted>
        implements ThingModifiedEvent<WotValidationConfigDeleted> {

    /**
     * Name of this event.
     */
    public static final String NAME = "wotValidationConfigDeleted";

    /**
     * Type of this event.
     */
    public static final String TYPE = TYPE_PREFIX + NAME;

    private WotValidationConfigDeleted(final ThingId thingId,
            final long revision,
            @Nullable final Instant timestamp,
            final DittoHeaders dittoHeaders,
            @Nullable final Metadata metadata) {
        super(TYPE, thingId, revision, timestamp, dittoHeaders, metadata);
    }

    /**
     * Creates a new {@code WotValidationConfigDeleted} event.
     *
     * @param thingId the ID of the Thing with which this event is associated.
     * @param revision the revision of the Thing.
     * @param timestamp the timestamp of this event.
     * @param dittoHeaders the headers of the command which was the cause of this event.
     * @param metadata the metadata to apply for the event.
     * @return the created WotValidationConfigDeleted.
     * @throws NullPointerException if any argument but {@code timestamp} and {@code metadata} is {@code null}.
     */
    public static WotValidationConfigDeleted of(final ThingId thingId,
            final long revision,
            @Nullable final Instant timestamp,
            final DittoHeaders dittoHeaders,
            @Nullable final Metadata metadata) {
        return new WotValidationConfigDeleted(thingId, revision, timestamp, dittoHeaders, metadata);
    }

    /**
     * Creates a new {@code WotValidationConfigDeleted} from a JSON object.
     *
     * @param jsonObject the JSON object from which a new WotValidationConfigDeleted instance is to be created.
     * @param dittoHeaders the headers of the command which was the cause of this event.
     * @return the {@code WotValidationConfigDeleted} which was created from the given JSON object.
     * @throws NullPointerException if {@code jsonObject} is {@code null}.
     * @throws org.eclipse.ditto.json.JsonParseException if the passed in {@code jsonObject} was not in the expected
     * format.
     */
    public static WotValidationConfigDeleted fromJson(final JsonObject jsonObject, final DittoHeaders dittoHeaders) {
        return new EventJsonDeserializer<WotValidationConfigDeleted>(TYPE, jsonObject)
                .deserialize((revision, timestamp, metadata) -> {
                    final String extractedThingId = jsonObject.getValueOrThrow(ThingEvent.JsonFields.THING_ID);
                    final ThingId thingId = ThingId.of(extractedThingId);
                    return new WotValidationConfigDeleted(thingId, revision, timestamp, dittoHeaders, metadata);
                });
    }

    @Override
    public JsonPointer getResourcePath() {
        return JsonPointer.of("/wotValidationConfig");
    }

    @Override
    public WotValidationConfigDeleted setRevision(final long revision) {
        return of(getEntityId(), revision, getTimestamp().orElse(null), getDittoHeaders(),
                getMetadata().orElse(null));
    }

    @Override
    public WotValidationConfigDeleted setDittoHeaders(final DittoHeaders dittoHeaders) {
        return new WotValidationConfigDeleted(getEntityId(), getRevision(), getTimestamp().orElse(null),
                dittoHeaders, getMetadata().orElse(null));
    }

    @Override
    protected void appendPayload(final JsonObjectBuilder jsonObjectBuilder,
            final JsonSchemaVersion schemaVersion,
            final Predicate<JsonField> predicate) {
        // nothing to add for delete events
    }

    @Override
    public Command.Category getCommandCategory() {
        return Command.Category.DELETE;
    }

    @Override
    public Optional<JsonValue> getEntity(final JsonSchemaVersion schemaVersion) {
        return Optional.empty();
    }

    @Override
    public WotValidationConfigDeleted setEntity(final JsonValue entity) {
        return this;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" + super.toString() + "]";
    }
} 