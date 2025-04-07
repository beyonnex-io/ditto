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
package org.eclipse.ditto.things.model.devops.responses;

import static org.eclipse.ditto.base.model.common.ConditionChecker.checkNotNull;

import java.util.Objects;
import java.util.function.Predicate;

import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.base.model.common.HttpStatus;
import org.eclipse.ditto.base.model.headers.DittoHeaders;
import org.eclipse.ditto.base.model.json.FieldType;
import org.eclipse.ditto.base.model.json.JsonParsableCommandResponse;
import org.eclipse.ditto.base.model.json.JsonSchemaVersion;
import org.eclipse.ditto.base.model.signals.commands.AbstractCommandResponse;
import org.eclipse.ditto.base.model.signals.commands.CommandResponseJsonDeserializer;
import org.eclipse.ditto.json.JsonField;
import org.eclipse.ditto.json.JsonFieldDefinition;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonObjectBuilder;
import org.eclipse.ditto.json.JsonPointer;
import org.eclipse.ditto.things.model.devops.ImmutableWoTValidationConfig;

/**
 * Response to a WoT validation config command.
 *
 * @since 3.0.0
 */
@Immutable
@JsonParsableCommandResponse(type = WotValidationConfigResponse.TYPE)
public final class WotValidationConfigResponse extends AbstractCommandResponse<WotValidationConfigResponse> {

    /**
     * Type prefix of DevOps command responses.
     */
    public static final String TYPE_PREFIX = "devops:";

    /**
     * Name of the response.
     */
    public static final String NAME = "wotValidationConfigResponse";

    /**
     * Type of this response.
     */
    public static final String TYPE = TYPE_PREFIX + NAME;

    private static final JsonFieldDefinition<JsonObject> JSON_CONFIG =
            JsonFieldDefinition.ofJsonObject("config", FieldType.REGULAR, JsonSchemaVersion.V_2);

    private static final CommandResponseJsonDeserializer<WotValidationConfigResponse> JSON_DESERIALIZER =
            CommandResponseJsonDeserializer.newInstance(TYPE,
                    context -> {
                        final JsonObject jsonObject = context.getJsonObject();
                        return new WotValidationConfigResponse(
                                ImmutableWoTValidationConfig.fromJson(jsonObject.getValueOrThrow(JSON_CONFIG)),
                                context.getDeserializedHttpStatus(),
                                context.getDittoHeaders()
                        );
                    });

    private final ImmutableWoTValidationConfig config;

    private WotValidationConfigResponse(final ImmutableWoTValidationConfig config,
            final HttpStatus httpStatus,
            final DittoHeaders dittoHeaders) {

        super(TYPE, httpStatus, dittoHeaders);
        this.config = checkNotNull(config, "config");
    }

    /**
     * Creates a new {@code WotValidationConfigResponse} object.
     *
     * @param config the WoT validation config.
     * @param dittoHeaders the headers of the response.
     * @return the response.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static WotValidationConfigResponse of(final ImmutableWoTValidationConfig config,
            final DittoHeaders dittoHeaders) {
        return new WotValidationConfigResponse(config, HttpStatus.OK, dittoHeaders);
    }

    /**
     * Creates a new {@code WotValidationConfigResponse} from a JSON object.
     *
     * @param jsonObject the JSON object of which the response is to be created.
     * @param dittoHeaders the headers of the response.
     * @return the response.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws org.eclipse.ditto.json.JsonParseException if the passed in {@code jsonObject} was not in the expected format.
     */
    public static WotValidationConfigResponse fromJson(final JsonObject jsonObject, final DittoHeaders dittoHeaders) {
        return JSON_DESERIALIZER.deserialize(jsonObject, dittoHeaders);
    }

    /**
     * Returns the WoT validation config.
     *
     * @return the config.
     */
    public ImmutableWoTValidationConfig getConfig() {
        return config;
    }

    @Override
    public JsonPointer getResourcePath() {
        return JsonPointer.empty();
    }

    @Override
    public String getResourceType() {
        return "devops";
    }

    @Override
    protected void appendPayload(final JsonObjectBuilder jsonObjectBuilder,
            final JsonSchemaVersion schemaVersion,
            final Predicate<JsonField> predicate) {
        final Predicate<JsonField> extendedPredicate = schemaVersion.and(predicate);
        jsonObjectBuilder.set(JSON_CONFIG, config.toJson(), extendedPredicate);
    }

    @Override
    public WotValidationConfigResponse setDittoHeaders(final DittoHeaders dittoHeaders) {
        return new WotValidationConfigResponse(config, getHttpStatus(), dittoHeaders);
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
        final WotValidationConfigResponse that = (WotValidationConfigResponse) o;
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
} 