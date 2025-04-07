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

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.base.model.common.HttpStatus;
import org.eclipse.ditto.base.model.headers.DittoHeaders;
import org.eclipse.ditto.base.model.json.JsonParsableCommandResponse;
import org.eclipse.ditto.base.model.json.JsonSchemaVersion;
import org.eclipse.ditto.base.model.signals.commands.AbstractCommandResponse;
import org.eclipse.ditto.base.model.signals.commands.CommandResponseJsonDeserializer;
import org.eclipse.ditto.json.JsonCollectors;
import org.eclipse.ditto.json.JsonField;
import org.eclipse.ditto.json.JsonFieldDefinition;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonObjectBuilder;
import org.eclipse.ditto.json.JsonPointer;
import org.eclipse.ditto.json.JsonValue;
import org.eclipse.ditto.things.model.ThingId;
import org.eclipse.ditto.things.model.signals.commands.ThingCommandResponse;

/**
 * Response to a {@link RetrieveWotValidationConfig} command.
 */
@Immutable
@JsonParsableCommandResponse(type = RetrieveWotValidationConfigResponse.TYPE)
public final class RetrieveWotValidationConfigResponse extends AbstractCommandResponse<RetrieveWotValidationConfigResponse>
        implements ThingCommandResponse<RetrieveWotValidationConfigResponse> {

    /**
     * Type prefix of WoT validation config commands.
     */
    public static final String TYPE_PREFIX = "things:";

    /**
     * Name of the command response.
     */
    public static final String NAME = "retrieveWotValidationConfigResponse";

    /**
     * Type of this command response.
     */
    public static final String TYPE = TYPE_PREFIX + NAME;

    private static final JsonFieldDefinition<JsonValue> JSON_CONFIGS =
            JsonFieldDefinition.ofJsonValue("configs", JsonSchemaVersion.V_2);

    private static final CommandResponseJsonDeserializer<RetrieveWotValidationConfigResponse> JSON_DESERIALIZER =
            CommandResponseJsonDeserializer.newInstance(TYPE,
                    context -> {
                        final JsonObject jsonObject = context.getJsonObject();
                        final ThingId thingId = ThingId.of(jsonObject.getValueOrThrow(ThingCommandResponse.JsonFields.JSON_THING_ID));
                        return new RetrieveWotValidationConfigResponse(
                                thingId,
                                jsonObject.getValue(JSON_CONFIGS)
                                        .map(JsonValue::asArray)
                                        .map(array -> array.stream()
                                                .map(JsonValue::asObject)
                                                .map(WotValidationConfig::fromJson)
                                                .collect(Collectors.toSet()))
                                        .orElse(Collections.emptySet()),
                                context.getDittoHeaders()
                        );
                    });

    private final ThingId thingId;
    private final Set<WotValidationConfig> configs;

    private RetrieveWotValidationConfigResponse(final ThingId thingId,
            final Set<WotValidationConfig> configs,
            final DittoHeaders dittoHeaders) {
        super(TYPE, HttpStatus.OK, dittoHeaders);
        this.thingId = Objects.requireNonNull(thingId, "thingId");
        this.configs = Objects.requireNonNull(configs, "configs");
    }

    /**
     * Creates a new {@code RetrieveWotValidationConfigResponse} object.
     *
     * @param thingId the ID of the thing the response is for.
     * @param configs the set of validation configs.
     * @param dittoHeaders the headers of the command response.
     * @return the command response.
     */
    public static RetrieveWotValidationConfigResponse of(final ThingId thingId,
            final Set<WotValidationConfig> configs,
            final DittoHeaders dittoHeaders) {
        return new RetrieveWotValidationConfigResponse(thingId, configs, dittoHeaders);
    }

    /**
     * Creates a new {@code RetrieveWotValidationConfigResponse} from a JSON object.
     *
     * @param jsonObject the JSON object of which the response is to be created.
     * @param dittoHeaders the headers of the command response.
     * @return the response.
     * @throws NullPointerException if {@code jsonObject} is {@code null}.
     */
    public static RetrieveWotValidationConfigResponse fromJson(final JsonObject jsonObject,
            final DittoHeaders dittoHeaders) {
        return JSON_DESERIALIZER.deserialize(jsonObject, dittoHeaders);
    }

    @Override
    public ThingId getEntityId() {
        return thingId;
    }

    /**
     * Returns the set of validation configs.
     *
     * @return the set of validation configs.
     */
    public Set<WotValidationConfig> getConfigs() {
        return configs;
    }

    @Override
    public RetrieveWotValidationConfigResponse setDittoHeaders(final DittoHeaders dittoHeaders) {
        return of(thingId, configs, dittoHeaders);
    }

    @Override
    public JsonPointer getResourcePath() {
        return JsonPointer.of("/wot-validation-config");
    }

    @Override
    protected void appendPayload(final JsonObjectBuilder jsonObjectBuilder,
            final JsonSchemaVersion schemaVersion,
            final Predicate<JsonField> predicate) {
        final Predicate<JsonField> p = schemaVersion.and(predicate);

        jsonObjectBuilder.set(ThingCommandResponse.JsonFields.JSON_THING_ID, thingId.toString(), p);
        jsonObjectBuilder.set(JSON_CONFIGS, configs.stream()
                .map(WotValidationConfig::toJson)
                .collect(JsonCollectors.valuesToArray()), p);
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
                Objects.equals(configs, that.configs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), thingId, configs);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" +
                super.toString() +
                ", thingId=" + thingId +
                ", configs=" + configs +
                "]";
    }
} 