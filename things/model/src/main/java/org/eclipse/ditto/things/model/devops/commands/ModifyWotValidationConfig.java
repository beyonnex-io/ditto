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
import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.base.model.common.HttpStatus;
import org.eclipse.ditto.base.model.entity.type.EntityType;
import org.eclipse.ditto.base.model.headers.DittoHeaders;
import org.eclipse.ditto.base.model.json.FieldType;
import org.eclipse.ditto.base.model.json.JsonParsableCommand;
import org.eclipse.ditto.base.model.json.JsonSchemaVersion;
import org.eclipse.ditto.base.model.signals.commands.Command;
import org.eclipse.ditto.json.JsonFactory;
import org.eclipse.ditto.json.JsonField;
import org.eclipse.ditto.json.JsonFieldDefinition;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonObjectBuilder;
import org.eclipse.ditto.json.JsonPointer;
import org.eclipse.ditto.json.JsonValue;
import org.eclipse.ditto.things.model.ThingConstants;
import org.eclipse.ditto.things.model.devops.WotValidationConfigId;
import org.eclipse.ditto.wot.model.ImmutableWotValidationConfig;

/**
 * Command which modifies the WoT validation config.
 */
@Immutable
@JsonParsableCommand(typePrefix = WotValidationConfigCommand.TYPE_PREFIX, name = ModifyWotValidationConfig.NAME)
public final class ModifyWotValidationConfig extends AbstractWotValidationConfigCommand<ModifyWotValidationConfig>
        implements WotValidationConfigCommand<ModifyWotValidationConfig> {

    /**
     * Name of this command.
     */
    public static final String NAME = "modifyWotValidationConfig";

    /**
     * Type of this command.
     */
    public static final String TYPE = WotValidationConfigCommand.TYPE_PREFIX + NAME;

    private final ImmutableWotValidationConfig validationConfig;

    private ModifyWotValidationConfig(final WotValidationConfigId configId,
            final ImmutableWotValidationConfig validationConfig,
            final DittoHeaders dittoHeaders) {
        super(TYPE, configId, dittoHeaders);
        this.validationConfig = Objects.requireNonNull(validationConfig, "Validation Config");
    }

    /**
     * Creates a new {@code ModifyWotValidationConfig} command.
     *
     * @param configId the ID of the config to modify.
     * @param validationConfig the WoT validation config to modify.
     * @param dittoHeaders the headers of the command.
     * @return a new command for modifying the WoT validation config.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static ModifyWotValidationConfig of(final WotValidationConfigId configId,
            final ImmutableWotValidationConfig validationConfig,
            final DittoHeaders dittoHeaders) {
        return new ModifyWotValidationConfig(configId, validationConfig, dittoHeaders);
    }

    /**
     * Creates a new {@code ModifyWotValidationConfig} from a JSON object.
     *
     * @param jsonObject the JSON object of which the command is to be created.
     * @param dittoHeaders the headers of the command.
     * @return the command.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws org.eclipse.ditto.json.JsonParseException if the passed in {@code jsonObject} was not in the expected
     * format.
     */
    public static ModifyWotValidationConfig fromJson(final JsonObject jsonObject, final DittoHeaders dittoHeaders) {
        final JsonObject validationConfigJson = jsonObject.getValueOrThrow(JsonFields.VALIDATION_CONFIG);
        final ImmutableWotValidationConfig validationConfig = ImmutableWotValidationConfig.fromJson(validationConfigJson);
        final String configIdString = jsonObject.getValueOrThrow(WotValidationConfigCommand.JsonFields.CONFIG_ID);

        return of(WotValidationConfigId.of(configIdString), validationConfig, dittoHeaders);
    }

    public Optional<JsonValue> getEntity() {
        return Optional.of(validationConfig.toJson());
    }

    public Optional<JsonValue> getEntity(final JsonSchemaVersion schemaVersion) {
        return getEntity();
    }

    public ModifyWotValidationConfig setEntity(final JsonValue entity) {
        return new ModifyWotValidationConfig(getEntityId(), ImmutableWotValidationConfig.fromJson(entity.asObject()), getDittoHeaders());
    }

    @Override
    public JsonPointer getResourcePath() {
        return JsonPointer.empty();
    }

    @Override
    public String getTypePrefix() {
        return WotValidationConfigCommand.TYPE_PREFIX;
    }

    @Override
    public Command.Category getCategory() {
        return Command.Category.MODIFY;
    }

    public EntityType getEntityType() {
        return EntityType.of("wot-validation-config");
    }

    public boolean changesAuthorization() {
        return false;
    }

    @Override
    protected void appendPayload(final JsonObjectBuilder jsonObjectBuilder, final JsonSchemaVersion schemaVersion,
            final Predicate<JsonField> thePredicate) {
        final Predicate<JsonField> predicate = schemaVersion.and(thePredicate);
        jsonObjectBuilder.set(JsonFields.VALIDATION_CONFIG, validationConfig.toJson(), predicate);
    }

    @Override
    public ModifyWotValidationConfig setDittoHeaders(final DittoHeaders dittoHeaders) {
        return of(getEntityId(), validationConfig, dittoHeaders);
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final ModifyWotValidationConfig that = (ModifyWotValidationConfig) obj;
        return Objects.equals(validationConfig, that.validationConfig);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), validationConfig);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" +
                "configId=" + getEntityId() +
                ", validationConfig=" + validationConfig +
                ", dittoHeaders=" + getDittoHeaders() +
                "]";
    }

    /**
     * Returns the WoT validation config as a JSON object.
     *
     * @return the WoT validation config as a JSON object.
     */
    public JsonObject getConfig() {
        return validationConfig.toJson();
    }

    /**
     * This class contains definitions for all specific fields of a {@code ModifyWotValidationConfig}'s JSON representation.
     */
    @Immutable
    public static final class JsonFields {

        /**
         * JSON field containing the WoT validation config.
         */
        public static final JsonFieldDefinition<JsonObject> VALIDATION_CONFIG =
                JsonFactory.newJsonObjectFieldDefinition("validationConfig", FieldType.REGULAR,
                        JsonSchemaVersion.V_2);

        private JsonFields() {
            throw new AssertionError();
        }
    }
}
