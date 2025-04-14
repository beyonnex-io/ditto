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

import org.eclipse.ditto.base.model.entity.id.EntityId;
import org.eclipse.ditto.base.model.entity.id.WithEntityId;
import org.eclipse.ditto.base.model.entity.type.EntityType;
import org.eclipse.ditto.base.model.headers.DittoHeaders;
import org.eclipse.ditto.base.model.json.JsonParsableCommand;
import org.eclipse.ditto.base.model.json.JsonSchemaVersion;
import org.eclipse.ditto.base.model.signals.commands.AbstractCommand;
import org.eclipse.ditto.json.JsonField;
import org.eclipse.ditto.json.JsonFieldDefinition;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonObjectBuilder;
import org.eclipse.ditto.json.JsonPointer;

/**
 * Command which modifies the WoT validation configuration.
 */
@Immutable
@JsonParsableCommand(typePrefix = WotValidationConfigCommand.TYPE_PREFIX, name = ModifyWotValidationConfig.NAME)
public final class ModifyWotValidationConfig extends AbstractCommand<ModifyWotValidationConfig>
        implements WotValidationConfigCommand<ModifyWotValidationConfig>, WithEntityId {

    /**
     * Name of the command.
     */
    public static final String NAME = "modify";

    /**
     * Type of this command.
     */
    public static final String TYPE = WotValidationConfigCommand.TYPE_PREFIX + NAME;

    private static final EntityId DUMMY_ENTITY_ID = EntityId.of(EntityType.of("wot"), "validation:config");
    private final JsonObject config;

    private ModifyWotValidationConfig(final JsonObject config,
            final DittoHeaders dittoHeaders) {
        super(TYPE, dittoHeaders);
        this.config = checkNotNull(config, "config");
    }

    /**
     * Creates a new {@code ModifyWotValidationConfig} command.
     *
     * @param config the configuration to set.
     * @param dittoHeaders the headers of the command.
     * @return the command.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static ModifyWotValidationConfig of(final JsonObject config,
            final DittoHeaders dittoHeaders) {
        return new ModifyWotValidationConfig(config, dittoHeaders);
    }

    /**
     * Creates a new {@code ModifyWotValidationConfig} from a JSON object.
     *
     * @param jsonObject the JSON object of which the command is to be created.
     * @param dittoHeaders the headers of the command.
     * @return the command.
     * @throws NullPointerException if {@code jsonObject} is {@code null}.
     */
    public static ModifyWotValidationConfig fromJson(final JsonObject jsonObject,
            final DittoHeaders dittoHeaders) {
        final JsonObject config = jsonObject.getValueOrThrow(JsonFields.CONFIG);
        return of(config, dittoHeaders);
    }

    @Override
    public EntityId getEntityId() {
        return DUMMY_ENTITY_ID;
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
        jsonObjectBuilder.set(JsonFields.CONFIG, config);
    }

    @Override
    public Category getCategory() {
        return Category.MODIFY;
    }

    @Override
    public String getTypePrefix() {
        return WotValidationConfigCommand.TYPE_PREFIX;
    }

    @Override
    public ModifyWotValidationConfig setDittoHeaders(final DittoHeaders dittoHeaders) {
        return of(config, dittoHeaders);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), config);
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
        final ModifyWotValidationConfig that = (ModifyWotValidationConfig) o;
        return Objects.equals(config, that.config);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" +
                super.toString() +
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
