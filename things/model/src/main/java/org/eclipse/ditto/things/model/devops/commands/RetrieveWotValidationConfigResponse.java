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
package org.eclipse.ditto.things.model.devops.commands;

import static org.eclipse.ditto.base.model.common.ConditionChecker.checkNotNull;

import java.util.Objects;
import java.util.function.Predicate;

import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.base.model.common.HttpStatus;
import org.eclipse.ditto.base.model.headers.DittoHeaders;
import org.eclipse.ditto.base.model.json.JsonParsableCommandResponse;
import org.eclipse.ditto.base.model.json.JsonSchemaVersion;
import org.eclipse.ditto.base.model.signals.commands.AbstractCommandResponse;
import org.eclipse.ditto.base.model.signals.commands.CommandResponse;
import org.eclipse.ditto.json.JsonField;
import org.eclipse.ditto.json.JsonFieldDefinition;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonObjectBuilder;
import org.eclipse.ditto.json.JsonPointer;
import org.eclipse.ditto.things.model.ThingId;
import org.eclipse.ditto.things.model.WithThingId;
import org.eclipse.ditto.things.model.signals.commands.ThingCommand;

/**
 * Response to a {@link RetrieveWotValidationConfig} command.
 */
@Immutable
@JsonParsableCommandResponse(type = RetrieveWotValidationConfigResponse.TYPE)
public final class RetrieveWotValidationConfigResponse extends AbstractCommandResponse<RetrieveWotValidationConfigResponse>
        implements CommandResponse<RetrieveWotValidationConfigResponse>, WithThingId {

    /**
     * Type of this response.
     */
    public static final String TYPE = WotValidationConfigCommand.TYPE_PREFIX + "retrieveResponse";

    private final ThingId thingId;
    private final JsonObject config;

    private RetrieveWotValidationConfigResponse(final ThingId thingId,
            final JsonObject config,
            final DittoHeaders dittoHeaders) {
        super(TYPE, HttpStatus.OK, dittoHeaders);
        this.thingId = checkNotNull(thingId, "thingId");
        this.config = checkNotNull(config, "config");
    }

    /**
     * Creates a new {@code RetrieveWotValidationConfigResponse} response.
     *
     * @param thingId the ID of the thing.
     * @param config the configuration.
     * @param dittoHeaders the headers of the response.
     * @return the response.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static RetrieveWotValidationConfigResponse of(final ThingId thingId,
            final JsonObject config,
            final DittoHeaders dittoHeaders) {
        return new RetrieveWotValidationConfigResponse(thingId, config, dittoHeaders);
    }

    /**
     * Creates a new {@code RetrieveWotValidationConfigResponse} from a JSON object.
     *
     * @param jsonObject the JSON object of which the response is to be created.
     * @param dittoHeaders the headers of the response.
     * @return the response.
     * @throws NullPointerException if {@code jsonObject} is {@code null}.
     */
    public static RetrieveWotValidationConfigResponse fromJson(final JsonObject jsonObject,
            final DittoHeaders dittoHeaders) {
        final ThingId thingId = ThingId.of(jsonObject.getValueOrThrow(ThingCommand.JsonFields.JSON_THING_ID));
        final JsonObject config = jsonObject.getValueOrThrow(JsonFields.CONFIG);
        return of(thingId, config, dittoHeaders);
    }

    @Override
    public ThingId getEntityId() {
        return thingId;
    }

    /**
     * Returns the configuration.
     *
     * @return the configuration.
     */
    public JsonObject getConfig() {
        return config;
    }

    @Override
    public JsonPointer getResourcePath() {
        return JsonPointer.empty();
    }

    @Override
    public String getResourceType() {
        return WotValidationConfigCommand.RESOURCE_TYPE;
    }

    @Override
    protected void appendPayload(final JsonObjectBuilder jsonObjectBuilder,
            final JsonSchemaVersion schemaVersion,
            final Predicate<JsonField> predicate) {
        jsonObjectBuilder.set(ThingCommand.JsonFields.JSON_THING_ID, thingId.toString());
        jsonObjectBuilder.set(JsonFields.CONFIG, config);
    }

    @Override
    public RetrieveWotValidationConfigResponse setDittoHeaders(final DittoHeaders dittoHeaders) {
        return of(thingId, config, dittoHeaders);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), thingId, config);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final RetrieveWotValidationConfigResponse that = (RetrieveWotValidationConfigResponse) o;
        return Objects.equals(thingId, that.thingId) &&
                Objects.equals(config, that.config);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" +
                super.toString() +
                ", thingId=" + thingId +
                ", config=" + config +
                "]";
    }

    /**
     * JSON field definitions.
     */
    static final class JsonFields {
        static final JsonFieldDefinition<JsonObject> CONFIG =
                JsonFieldDefinition.ofJsonObject("config");
    }
} 