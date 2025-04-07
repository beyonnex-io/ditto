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
package org.eclipse.ditto.things.model.signals.commands.modify;

import java.util.Objects;
import java.util.function.Predicate;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.base.model.headers.DittoHeaders;
import org.eclipse.ditto.base.model.json.JsonParsableCommand;
import org.eclipse.ditto.base.model.json.JsonSchemaVersion;
import org.eclipse.ditto.base.model.signals.commands.AbstractCommand;
import org.eclipse.ditto.base.model.signals.commands.Command;
import org.eclipse.ditto.json.JsonFactory;
import org.eclipse.ditto.json.JsonField;
import org.eclipse.ditto.json.JsonFieldDefinition;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonObjectBuilder;
import org.eclipse.ditto.json.JsonPointer;
import org.eclipse.ditto.things.model.ThingId;

/**
 * Command which retrieves the WoT validation configuration.
 */
@Immutable
@JsonParsableCommand(typePrefix = WotValidationConfigCommand.TYPE_PREFIX, name = RetrieveWotValidationConfig.NAME)
public final class RetrieveWotValidationConfig extends AbstractCommand<RetrieveWotValidationConfig>
        implements WotValidationConfigCommand<RetrieveWotValidationConfig> {

    /**
     * Name of the command.
     */
    public static final String NAME = "retrieveWotValidationConfig";

    /**
     * Type of this command.
     */
    public static final String TYPE = TYPE_PREFIX + NAME;

    private final ThingId thingId;

    private RetrieveWotValidationConfig(final ThingId thingId, final DittoHeaders dittoHeaders) {
        super(TYPE, dittoHeaders);
        this.thingId = thingId;
    }

    /**
     * Returns a new instance of {@code RetrieveWotValidationConfig}.
     *
     * @param thingId the ID of the thing.
     * @param dittoHeaders the headers of the command.
     * @return a new command for retrieving the WoT validation configuration.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static RetrieveWotValidationConfig of(final ThingId thingId, final DittoHeaders dittoHeaders) {
        Objects.requireNonNull(thingId, "Thing ID must not be null!");
        Objects.requireNonNull(dittoHeaders, "DittoHeaders must not be null!");
        return new RetrieveWotValidationConfig(thingId, dittoHeaders);
    }

    /**
     * Creates a new {@code RetrieveWotValidationConfig} from a JSON object.
     *
     * @param jsonObject the JSON object of which the command is to be created.
     * @param dittoHeaders the headers of the command.
     * @return the command.
     * @throws NullPointerException if {@code jsonObject} is {@code null}.
     * @throws org.eclipse.ditto.json.JsonParseException if the passed in {@code jsonObject} was not in the expected
     * format.
     */
    public static RetrieveWotValidationConfig fromJson(final JsonObject jsonObject, final DittoHeaders dittoHeaders) {
        final ThingId thingId = ThingId.of(jsonObject.getValueOrThrow(JsonFields.THING_ID));
        return new RetrieveWotValidationConfig(thingId, dittoHeaders);
    }

    @Override
    public ThingId getEntityId() {
        return thingId;
    }

    @Override
    public JsonPointer getResourcePath() {
        return JsonPointer.of("/wotValidationConfig");
    }

    @Override
    public Command.Category getCategory() {
        return Command.Category.QUERY;
    }

    @Override
    public RetrieveWotValidationConfig setDittoHeaders(final DittoHeaders dittoHeaders) {
        return new RetrieveWotValidationConfig(thingId, dittoHeaders);
    }

    @Override
    protected void appendPayload(final JsonObjectBuilder jsonObjectBuilder, final JsonSchemaVersion schemaVersion,
            final Predicate<JsonField> predicate) {
        jsonObjectBuilder.set(JsonFields.THING_ID, thingId.toString(), predicate);
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
        final RetrieveWotValidationConfig that = (RetrieveWotValidationConfig) o;
        return Objects.equals(thingId, that.thingId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), thingId);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" +
                super.toString() +
                ", thingId=" + thingId +
                "]";
    }

    /**
     * Fields of this command's JSON representation.
     */
    private static final class JsonFields {

        private static final JsonFieldDefinition<String> THING_ID =
                JsonFactory.newStringFieldDefinition("thingId");

        private JsonFields() {
            throw new AssertionError();
        }
    }
} 