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
package org.eclipse.ditto.things.model.devops;

import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.json.JsonFactory;
import org.eclipse.ditto.json.JsonFieldDefinition;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonObjectBuilder;
import org.eclipse.ditto.json.JsonValue;
import org.eclipse.ditto.base.model.json.FieldType;
import org.eclipse.ditto.base.model.json.JsonSchemaVersion;
import org.eclipse.ditto.base.model.json.Jsonifiable;

/**
 * Immutable value object representing a dynamic validation configuration for WoT (Web of Things).
 * <p>
 * This class encapsulates configuration overrides that should be applied when a specific validation context matches.
 * The configuration includes a scope identifier, validation context, and configuration overrides.
 * </p>
 * <p>
 * Instances of this class are immutable and thread-safe.
 * </p>
 *
 * @since 3.8.0
 */
@Immutable
public final class ImmutableDynamicValidationConfig implements Jsonifiable<JsonObject> {


    private static final JsonFieldDefinition<String> SCOPE_ID_FIELD =
            JsonFactory.newStringFieldDefinition("scopeId", FieldType.REGULAR, JsonSchemaVersion.V_2);
    private static final JsonFieldDefinition<JsonObject> VALIDATION_CONTEXT_FIELD =
            JsonFactory.newJsonObjectFieldDefinition("validationContext", FieldType.REGULAR, JsonSchemaVersion.V_2);
    private static final JsonFieldDefinition<JsonObject> CONFIG_OVERRIDES_FIELD =
            JsonFactory.newJsonObjectFieldDefinition("configOverrides", FieldType.REGULAR, JsonSchemaVersion.V_2);

    private final String scopeId;
    @Nullable private final ImmutableValidationContext validationContext;
    @Nullable private final ImmutableConfigOverrides configOverrides;

    private ImmutableDynamicValidationConfig(
            final String scopeId,
            @Nullable final ImmutableValidationContext validationContext,
            @Nullable final ImmutableConfigOverrides configOverrides) {
        this.scopeId = Objects.requireNonNull(scopeId, "scopeId");
        this.validationContext = validationContext;
        this.configOverrides = configOverrides;
    }

    /**
     * Creates a new instance of {@code ImmutableDynamicValidationConfig}.
     *
     * @param scopeId the scope identifier for this configuration
     * @param validationContext the validation context that determines when this configuration should be applied
     * @param configOverrides the configuration overrides to apply when the validation context matches
     * @return a new instance with the specified values
     * @throws NullPointerException if {@code scopeId} is {@code null}
     */
    public static ImmutableDynamicValidationConfig of(
            final String scopeId,
            @Nullable final ImmutableValidationContext validationContext,
            @Nullable final ImmutableConfigOverrides configOverrides) {
        return new ImmutableDynamicValidationConfig(scopeId, validationContext, configOverrides);
    }

    /**
     * Creates a new instance of {@code ImmutableDynamicValidationConfig} from a JSON object.
     * The JSON object should contain the following fields:
     * <ul>
     *     <li>{@code scopeId} (required): The scope identifier for this configuration</li>
     *     <li>{@code validationContext} (optional): The validation context that determines when this configuration should be applied</li>
     *     <li>{@code configOverrides} (optional): The configuration overrides to apply when the validation context matches</li>
     * </ul>
     *
     * @param jsonObject the JSON object to create the configuration from
     * @return a new instance created from the JSON object
     * @throws NullPointerException if {@code jsonObject} is {@code null}
     * @throws IllegalArgumentException if the JSON object is invalid
     */
    public static ImmutableDynamicValidationConfig fromJson(final JsonObject jsonObject) {
        if (jsonObject == null) {
            throw new IllegalArgumentException("JSON object must not be null");
        }


        final String scopeId = jsonObject.getValue(SCOPE_ID_FIELD)
                .orElseThrow(() -> new IllegalArgumentException("Missing required field: scopeId"));

        final ImmutableValidationContext validationContext = jsonObject.getValue(VALIDATION_CONTEXT_FIELD)
                .filter(JsonValue::isObject)
                .map(JsonValue::asObject)
                .map(ImmutableValidationContext::fromJson)
                .orElse(null);

        final ImmutableConfigOverrides configOverrides = jsonObject.getValue(CONFIG_OVERRIDES_FIELD)
                .filter(JsonValue::isObject)
                .map(JsonValue::asObject)
                .map(ImmutableConfigOverrides::fromJson)
                .orElse(null);

        return of(scopeId, validationContext, configOverrides);
    }

    public String getScopeId() {
        return scopeId;
    }

    public Optional<ImmutableValidationContext> getValidationContext() {
        return Optional.ofNullable(validationContext);
    }

    public Optional<ImmutableConfigOverrides> getConfigOverrides() {
        return Optional.ofNullable(configOverrides);
    }

    @Override
    public JsonObject toJson() {
        final JsonObjectBuilder builder = JsonFactory.newObjectBuilder();
        builder.set(SCOPE_ID_FIELD, scopeId);
        getValidationContext().ifPresent(context -> builder.set(VALIDATION_CONTEXT_FIELD, context.toJson()));
        getConfigOverrides().ifPresent(overrides -> builder.set(CONFIG_OVERRIDES_FIELD, overrides.toJson()));
        return builder.build();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ImmutableDynamicValidationConfig that = (ImmutableDynamicValidationConfig) o;
        return Objects.equals(scopeId, that.scopeId) &&
                Objects.equals(validationContext, that.validationContext) &&
                Objects.equals(configOverrides, that.configOverrides);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scopeId, validationContext, configOverrides);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" +
                "scopeId=" + scopeId +
                ", validationContext=" + validationContext +
                ", configOverrides=" + configOverrides +
                "]";
    }
}
