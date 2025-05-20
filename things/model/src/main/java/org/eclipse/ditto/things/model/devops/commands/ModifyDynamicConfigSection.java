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
package org.eclipse.ditto.things.model.devops.commands;

import java.util.Objects;
import java.util.function.Predicate;

import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.base.model.headers.DittoHeaders;
import org.eclipse.ditto.base.model.json.FieldType;
import org.eclipse.ditto.base.model.json.JsonParsableCommand;
import org.eclipse.ditto.base.model.json.JsonSchemaVersion;
import org.eclipse.ditto.json.JsonFactory;
import org.eclipse.ditto.json.JsonField;
import org.eclipse.ditto.json.JsonFieldDefinition;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonObjectBuilder;
import org.eclipse.ditto.things.model.devops.WotValidationConfigId;
import org.eclipse.ditto.things.model.devops.ImmutableDynamicValidationConfig;
import org.eclipse.ditto.json.JsonPointer;
import org.eclipse.ditto.base.model.signals.commands.Command;

/**
 * Command to modify a single dynamic config section in the WoT (Web of Things) validation config.
 * <p>
 * This command is used to modify or create a specific dynamic configuration section identified by its scope ID.
 * The dynamic config section contains validation settings that can be overridden for a specific scope.
 * If a section with the given scope ID already exists, it will be updated; otherwise, a new section will be created.
 * This command is immutable and thread-safe.
 * </p>
 *
 * @since 3.8.0
 */
@Immutable
@JsonParsableCommand(typePrefix = WotValidationConfigCommand.TYPE_PREFIX, name = ModifyDynamicConfigSection.NAME)
public final class ModifyDynamicConfigSection extends AbstractWotValidationConfigCommand<ModifyDynamicConfigSection>
        implements WotValidationConfigCommand<ModifyDynamicConfigSection> {

    /**
     * Name of this command.
     * This is used to identify the command type in the command journal and for deserialization.
     */
    public static final String NAME = "modifyDynamicConfigSection";

    /**
     * Type of this command.
     * This is the full type identifier including the prefix.
     */
    public static final String TYPE = WotValidationConfigCommand.TYPE_PREFIX + NAME;

    /**
     * JSON field definition for the scope ID.
     * This field identifies the specific dynamic config section to modify or create.
     */
    private static final JsonFieldDefinition<String> SCOPE_ID =
            JsonFactory.newStringFieldDefinition("scopeId", FieldType.REGULAR, JsonSchemaVersion.V_2);

    private final String scopeId;
    private final ImmutableDynamicValidationConfig dynamicConfigSection;

    /**
     * Constructs a new {@code ModifyDynamicConfigSection} command.
     *
     * @param configId the ID of the WoT validation config.
     * @param scopeId the ID of the dynamic config section to modify or create.
     * @param dynamicConfigSection the new or updated dynamic config section.
     * @param dittoHeaders the headers of the command.
     * @throws NullPointerException if any argument is {@code null}.
     */
    private ModifyDynamicConfigSection(final WotValidationConfigId configId,
            final String scopeId,
            final ImmutableDynamicValidationConfig dynamicConfigSection,
            final DittoHeaders dittoHeaders) {
        super(TYPE, configId, dittoHeaders);
        if (scopeId == null) {
            throw new NullPointerException("scopeId");
        }
        if (scopeId.isEmpty()) {
            throw new IllegalArgumentException("Scope ID must not be empty");
        }
        this.scopeId = scopeId;
        this.dynamicConfigSection = Objects.requireNonNull(dynamicConfigSection, "dynamicConfigSection");
    }

    /**
     * Creates a new instance of {@code ModifyDynamicConfigSection}.
     *
     * @param configId the ID of the WoT validation config.
     * @param scopeId the ID of the dynamic config section to modify or create.
     * @param dynamicConfigSection the new or updated dynamic config section.
     * @param dittoHeaders the headers of the command.
     * @return the new instance.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static ModifyDynamicConfigSection of(final WotValidationConfigId configId,
            final String scopeId,
            final ImmutableDynamicValidationConfig dynamicConfigSection,
            final DittoHeaders dittoHeaders) {
        return new ModifyDynamicConfigSection(configId, scopeId, dynamicConfigSection, dittoHeaders);
    }

    /**
     * Creates a new instance of {@code ModifyDynamicConfigSection} from a JSON object.
     * The JSON object should contain the following fields:
     * <ul>
     *     <li>{@code configId} (required): The ID of the WoT validation config</li>
     *     <li>{@code scopeId} (required): The ID of the dynamic config section to modify or create</li>
     *     <li>{@code validationContext} (required): The validation context for the section</li>
     *     <li>{@code configOverrides} (required): The configuration overrides for the section</li>
     * </ul>
     *
     * @param jsonObject the JSON object.
     * @param dittoHeaders the headers of the command.
     * @return the new instance.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws IllegalArgumentException if any required field is missing or invalid.
     * @throws org.eclipse.ditto.json.JsonParseException if the passed in {@code jsonObject} was not in the expected format.
     */
    public static ModifyDynamicConfigSection fromJson(final JsonObject jsonObject, final DittoHeaders dittoHeaders) {
        if (!jsonObject.getValue(SCOPE_ID).isPresent()) {
            throw new IllegalArgumentException("Missing required field 'scopeId' in payload");
        }

        final String configIdString = jsonObject.getValueOrThrow(WotValidationConfigCommand.JsonFields.CONFIG_ID);
        final String scopeId = jsonObject.getValueOrThrow(SCOPE_ID);

        final JsonObject dynamicConfigJson = JsonObject.newBuilder()
                .set("scopeId", scopeId)
                .set("validationContext", jsonObject.getValue("validationContext").orElseThrow(() ->
                        new IllegalArgumentException("Missing required field 'validationContext' in payload")))
                .set("configOverrides", jsonObject.getValue("configOverrides").orElseThrow(() ->
                        new IllegalArgumentException("Missing required field 'configOverrides' in payload")))
                .build();

        final ImmutableDynamicValidationConfig dynamicConfigSection =
                ImmutableDynamicValidationConfig.fromJson(dynamicConfigJson);

        return of(WotValidationConfigId.of(configIdString), scopeId, dynamicConfigSection, dittoHeaders);
    }

    /**
     * Returns the scope ID of the dynamic config section to modify or create.
     *
     * @return the scope ID.
     */
    public String getScopeId() {
        return scopeId;
    }

    /**
     * Returns the new or updated dynamic config section.
     *
     * @return the dynamic config section.
     */
    public ImmutableDynamicValidationConfig getDynamicConfigSection() {
        return dynamicConfigSection;
    }

    @Override
    public String getTypePrefix() {
        return WotValidationConfigCommand.TYPE_PREFIX;
    }

    @Override
    public ModifyDynamicConfigSection setDittoHeaders(final DittoHeaders dittoHeaders) {
        return of(getEntityId(), getScopeId(), dynamicConfigSection, dittoHeaders);
    }

    @Override
    protected void appendPayload(final JsonObjectBuilder jsonObjectBuilder, final JsonSchemaVersion schemaVersion,
            final Predicate<JsonField> predicate) {
        super.appendPayload(jsonObjectBuilder, schemaVersion, predicate);
        jsonObjectBuilder.set(SCOPE_ID, getScopeId(), predicate);
        if(dynamicConfigSection.getValidationContext().isPresent()){
            jsonObjectBuilder.set("validationContext", dynamicConfigSection.getValidationContext().get().toJson(), predicate);
        }
        if(dynamicConfigSection.getConfigOverrides().isPresent()) {
            jsonObjectBuilder.set("configOverrides", dynamicConfigSection.getConfigOverrides().get().toJson(), predicate);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final ModifyDynamicConfigSection that = (ModifyDynamicConfigSection) o;
        return Objects.equals(scopeId, that.scopeId) && Objects.equals(dynamicConfigSection, that.dynamicConfigSection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), scopeId, dynamicConfigSection);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" +
                super.toString() +
                ", scopeId=" + scopeId +
                ", dynamicConfigSection=" + dynamicConfigSection +
                "]";
    }

    @Override
    public JsonPointer getResourcePath() {
        return JsonPointer.of("/wot/validation/config");
    }

    @Override
    public Command.Category getCategory() {
        return Command.Category.MODIFY;
    }
} 