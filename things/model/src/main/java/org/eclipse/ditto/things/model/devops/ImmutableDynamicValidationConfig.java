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

import org.eclipse.ditto.base.model.json.Jsonifiable;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonValue;

import javax.annotation.concurrent.Immutable;
import java.util.Objects;

/**
 * Represents a dynamic validation configuration consisting of a validation context and config overrides.
 */
@Immutable
public final class ImmutableDynamicValidationConfig implements Jsonifiable<JsonObject> {

    private static final String VALIDATION_CONTEXT_FIELD = "validationContext";
    private static final String CONFIG_OVERRIDES_FIELD = "configOverrides";

    private final ImmutableValidationContext validationContext;
    private final ImmutableConfigOverrides configOverrides;

    private ImmutableDynamicValidationConfig(final ImmutableValidationContext validationContext,
            final ImmutableConfigOverrides configOverrides) {
        this.validationContext = validationContext;
        this.configOverrides = configOverrides;
    }

    public static ImmutableDynamicValidationConfig of(final ImmutableValidationContext validationContext,
            final ImmutableConfigOverrides configOverrides) {
        return new ImmutableDynamicValidationConfig(validationContext, configOverrides);
    }

    public ImmutableValidationContext getValidationContext() {
        return validationContext;
    }

    public ImmutableConfigOverrides getConfigOverrides() {
        return configOverrides;
    }

    @Override
    public JsonObject toJson() {
        return JsonObject.newBuilder()
                .set(VALIDATION_CONTEXT_FIELD, validationContext.toJson())
                .set(CONFIG_OVERRIDES_FIELD, configOverrides.toJson())
                .build();
    }

    public static ImmutableDynamicValidationConfig fromJson(final JsonObject json) {
        final ImmutableValidationContext context = json.getValue(VALIDATION_CONTEXT_FIELD)
                .map(JsonValue::asObject)
                .map(ImmutableValidationContext::fromJson)
                .orElseThrow(() -> new IllegalArgumentException("Missing validationContext"));

        final ImmutableConfigOverrides overrides = json.getValue(CONFIG_OVERRIDES_FIELD)
                .map(JsonValue::asObject)
                .map(ImmutableConfigOverrides::fromJson)
                .orElseThrow(() -> new IllegalArgumentException("Missing configOverrides"));

        return of(context, overrides);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ImmutableDynamicValidationConfig)) return false;
        final ImmutableDynamicValidationConfig that = (ImmutableDynamicValidationConfig) o;
        return Objects.equals(validationContext, that.validationContext)
                && Objects.equals(configOverrides, that.configOverrides);
    }

    @Override
    public int hashCode() {
        return Objects.hash(validationContext, configOverrides);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" +
                "validationContext=" + validationContext +
                ", configOverrides=" + configOverrides +
                "]";
    }
}
