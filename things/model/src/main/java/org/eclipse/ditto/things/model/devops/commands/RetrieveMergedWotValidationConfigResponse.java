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

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.base.model.common.HttpStatus;
import org.eclipse.ditto.base.model.headers.DittoHeaders;
import org.eclipse.ditto.base.model.json.JsonParsableCommandResponse;
import org.eclipse.ditto.base.model.json.JsonSchemaVersion;
import org.eclipse.ditto.base.model.signals.commands.AbstractCommandResponse;
import org.eclipse.ditto.base.model.signals.commands.CommandResponseJsonDeserializer;
import org.eclipse.ditto.json.JsonField;
import org.eclipse.ditto.json.JsonFieldDefinition;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonObjectBuilder;
import org.eclipse.ditto.json.JsonPointer;
import org.eclipse.ditto.json.JsonValue;
import org.eclipse.ditto.things.model.devops.ImmutableWoTValidationConfig;

/**
 * Response to a {@link RetrieveMergedWotValidationConfig} command.
 */
@Immutable
@JsonParsableCommandResponse(type = RetrieveMergedWotValidationConfigResponse.TYPE)
public final class RetrieveMergedWotValidationConfigResponse extends AbstractCommandResponse<RetrieveMergedWotValidationConfigResponse> {

    /**
     * Type prefix of WoT validation config commands.
     */
    public static final String TYPE_PREFIX = "devops:";

    /**
     * Name of the command response.
     */
    public static final String NAME = "retrieveMergedWotValidationConfigResponse";

    /**
     * Type of this command response.
     */
    public static final String TYPE = TYPE_PREFIX + NAME;

    private static final JsonFieldDefinition<JsonValue> JSON_CONFIGS =
            JsonFieldDefinition.ofJsonValue("configs", JsonSchemaVersion.V_2);

    private static final CommandResponseJsonDeserializer<RetrieveMergedWotValidationConfigResponse> JSON_DESERIALIZER =
            CommandResponseJsonDeserializer.newInstance(TYPE,
                    context -> {
                        final JsonObject jsonObject = context.getJsonObject();
                        return new RetrieveMergedWotValidationConfigResponse(
                                jsonObject.getValue(JSON_CONFIGS)
                                        .map(JsonValue::asArray)
                                        .map(array -> array.stream()
                                                .map(JsonValue::asObject)
                                                .map(ImmutableWoTValidationConfig::fromJson)
                                                .collect(java.util.stream.Collectors.toSet()))
                                        .orElse(Collections.emptySet()),
                                context.getDittoHeaders()
                        );
                    });

    private final Set<ImmutableWoTValidationConfig> configs;

    private RetrieveMergedWotValidationConfigResponse(final Set<ImmutableWoTValidationConfig> configs,
            final DittoHeaders dittoHeaders) {
        super(TYPE, HttpStatus.OK, dittoHeaders);
        this.configs = checkNotNull(configs, "configs");
    }

    /**
     * Creates a new {@code RetrieveMergedWotValidationConfigResponse} object.
     *
     * @param configs the set of validation configs.
     * @param dittoHeaders the headers of the command response.
     * @return the command response.
     */
    public static RetrieveMergedWotValidationConfigResponse of(final Set<ImmutableWoTValidationConfig> configs,
            final DittoHeaders dittoHeaders) {
        return new RetrieveMergedWotValidationConfigResponse(configs, dittoHeaders);
    }

    /**
     * Creates a new {@code RetrieveMergedWotValidationConfigResponse} from a JSON object.
     *
     * @param jsonObject the JSON object of which the response is to be created.
     * @param dittoHeaders the headers of the command response.
     * @return the response.
     * @throws NullPointerException if {@code jsonObject} is {@code null}.
     */
    public static RetrieveMergedWotValidationConfigResponse fromJson(final JsonObject jsonObject,
            final DittoHeaders dittoHeaders) {
        return JSON_DESERIALIZER.deserialize(jsonObject, dittoHeaders);
    }

    /**
     * Returns the set of validation configs.
     *
     * @return the set of validation configs.
     */
    public Set<ImmutableWoTValidationConfig> getConfigs() {
        return configs;
    }

    @Override
    public RetrieveMergedWotValidationConfigResponse setDittoHeaders(final DittoHeaders dittoHeaders) {
        return of(configs, dittoHeaders);
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
    protected void appendPayload(final JsonObjectBuilder jsonObjectBuilder, final JsonSchemaVersion schemaVersion,
            final Predicate<JsonField> predicate) {
        jsonObjectBuilder.set(JSON_CONFIGS, JsonValue.of(configs.stream()
                .map(ImmutableWoTValidationConfig::toJson)
                .collect(java.util.stream.Collectors.toList())));
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final RetrieveMergedWotValidationConfigResponse that = (RetrieveMergedWotValidationConfigResponse) o;
        return Objects.equals(configs, that.configs) &&
                super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), configs);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" +
                "configs=" + configs +
                "]";
    }
} 