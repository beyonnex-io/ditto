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

import java.util.Objects;
import java.util.function.Predicate;

import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.base.model.common.HttpStatus;
import org.eclipse.ditto.base.model.headers.DittoHeaders;
import org.eclipse.ditto.base.model.json.JsonParsableCommandResponse;
import org.eclipse.ditto.base.model.json.JsonSchemaVersion;
import org.eclipse.ditto.base.model.signals.commands.WithEntity;
import org.eclipse.ditto.json.JsonField;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonObjectBuilder;
import org.eclipse.ditto.json.JsonPointer;
import org.eclipse.ditto.json.JsonValue;
import org.eclipse.ditto.things.model.devops.WotValidationConfigId;
import org.eclipse.ditto.json.JsonFactory;
import org.eclipse.ditto.things.model.devops.ImmutableWotValidationConfig;

/**
 * Response to a {@link ModifyWotValidationConfig} command.
 */
@Immutable
@JsonParsableCommandResponse(type = ModifyWotValidationConfigResponse.TYPE)
public final class ModifyWotValidationConfigResponse extends AbstractWotValidationConfigCommandResponse<ModifyWotValidationConfigResponse>
        implements WithEntity<ModifyWotValidationConfigResponse> {

    /**
     * Name of the response.
     */
    public static final String NAME = "modifyWotValidationConfigResponse";

    /**
     * Type of this response.
     */
    public static final String TYPE = WotValidationConfigCommandResponse.TYPE_PREFIX + NAME;

    private final JsonValue validationConfig;
    private final WotValidationConfigId configId;

    private ModifyWotValidationConfigResponse(final WotValidationConfigId configId, final JsonValue validationConfig,
            final DittoHeaders dittoHeaders) {
        super(TYPE, HttpStatus.OK, configId, dittoHeaders);
        this.validationConfig = Objects.requireNonNull(validationConfig, "validationConfig");
        this.configId = configId;
    }

    /**
     * Returns a new instance of {@code ModifyWotValidationConfigResponse}.
     *
     * @param configId the ID of the WoT validation config.
     * @param validationConfig the validation config.
     * @param dittoHeaders the headers of the response.
     * @return a new ModifyWotValidationConfigResponse.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static ModifyWotValidationConfigResponse of(final WotValidationConfigId configId, final JsonValue validationConfig,
            final DittoHeaders dittoHeaders) {
        return new ModifyWotValidationConfigResponse(configId, validationConfig, dittoHeaders);
    }

    /**
     * Returns a new instance of {@code ModifyWotValidationConfigResponse} for a created config.
     *
     * @param configId the ID of the WoT validation config.
     * @param dittoHeaders the headers of the response.
     * @return a new ModifyWotValidationConfigResponse.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static ModifyWotValidationConfigResponse created(final WotValidationConfigId configId,
            final DittoHeaders dittoHeaders) {
        return new ModifyWotValidationConfigResponse(configId, JsonFactory.newObject(), dittoHeaders);
    }

    /**
     * Returns a new instance of {@code ModifyWotValidationConfigResponse} for a modified config.
     *
     * @param configId the ID of the WoT validation config.
     * @param validationConfig the validation config.
     * @param dittoHeaders the headers of the response.
     * @return a new ModifyWotValidationConfigResponse.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static ModifyWotValidationConfigResponse modified(final WotValidationConfigId configId,
            final ImmutableWotValidationConfig validationConfig,
            final DittoHeaders dittoHeaders) {
        return new ModifyWotValidationConfigResponse(configId, validationConfig.toJson(), dittoHeaders);
    }

    /**
     * Creates a new {@code ModifyWotValidationConfigResponse} from a JSON string.
     *
     * @param jsonString the JSON string of which the response is to be created.
     * @param dittoHeaders the headers of the response.
     * @return the response.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws IllegalArgumentException if {@code jsonString} is empty.
     * @throws org.eclipse.ditto.json.JsonParseException if the passed in {@code jsonString} was not in the expected
     * format.
     */
    public static ModifyWotValidationConfigResponse fromJson(final String jsonString,
            final DittoHeaders dittoHeaders) {
        return fromJson(JsonObject.of(jsonString), dittoHeaders);
    }

    /**
     * Creates a new {@code ModifyWotValidationConfigResponse} from a JSON object.
     *
     * @param jsonObject the JSON object of which the response is to be created.
     * @param dittoHeaders the headers of the response.
     * @return the response.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws org.eclipse.ditto.json.JsonParseException if the passed in {@code jsonObject} was not in the expected
     * format.
     */
    public static ModifyWotValidationConfigResponse fromJson(final JsonObject jsonObject,
            final DittoHeaders dittoHeaders) {
        final WotValidationConfigId configId = WotValidationConfigId.of(jsonObject.getValueOrThrow(WotValidationConfigCommand.JsonFields.CONFIG_ID));
        final JsonObject validationConfigJson = jsonObject.getValueOrThrow(WotValidationConfigCommand.JsonFields.VALIDATION_CONFIG).asObject();
        final ImmutableWotValidationConfig validationConfig = ImmutableWotValidationConfig.fromJson(validationConfigJson);
        return modified(configId, validationConfig, dittoHeaders);
    }

    /**
     * Returns the validation config.
     *
     * @return the validation config.
     */
    public JsonValue getValidationConfig() {
        return validationConfig;
    }

    @Override
    public JsonValue getEntity(final JsonSchemaVersion schemaVersion) {
        return validationConfig;
    }

    @Override
    public ModifyWotValidationConfigResponse setEntity(final JsonValue entity) {
        return of(configId, entity, getDittoHeaders());
    }

    @Override
    public JsonPointer getResourcePath() {
        return JsonPointer.empty();
    }

    @Override
    public String getResourceType() {
        return WotValidationConfigCommandResponse.RESOURCE_TYPE;
    }

    @Override
    protected void appendPayload(final JsonObjectBuilder jsonObjectBuilder, final JsonSchemaVersion schemaVersion,
            final Predicate<JsonField> predicate) {
        super.appendPayload(jsonObjectBuilder, schemaVersion, predicate);
        jsonObjectBuilder.set(WotValidationConfigCommand.JsonFields.VALIDATION_CONFIG, validationConfig, predicate);
    }

    @Override
    public ModifyWotValidationConfigResponse setDittoHeaders(final DittoHeaders dittoHeaders) {
        return of(configId, validationConfig, dittoHeaders);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), validationConfig, configId);
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
        final ModifyWotValidationConfigResponse that = (ModifyWotValidationConfigResponse) o;
        return Objects.equals(validationConfig, that.validationConfig) &&
                Objects.equals(configId, that.configId);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" +
                super.toString() +
                ", validationConfig=" + validationConfig +
                ", configId=" + configId +
                "]";
    }
} 