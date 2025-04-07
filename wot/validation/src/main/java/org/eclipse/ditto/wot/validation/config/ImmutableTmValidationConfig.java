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
package org.eclipse.ditto.wot.validation.config;

import static org.eclipse.ditto.base.model.common.ConditionChecker.checkNotNull;

import java.util.Objects;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.json.JsonFieldDefinition;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonObjectBuilder;
import org.eclipse.ditto.json.JsonFactory;
import org.eclipse.ditto.wot.validation.ValidationContext;

/**
 * An immutable implementation of {@link TmValidationConfig}.
 */
@Immutable
public final class ImmutableTmValidationConfig implements TmValidationConfig {

    private final String id;
    private final boolean enabled;
    private final boolean logWarningInsteadOfFailingApiCalls;
    private final ThingValidationConfig thingValidationConfig;
    private final FeatureValidationConfig featureValidationConfig;

    private ImmutableTmValidationConfig(final String id,
            final boolean enabled,
            final boolean logWarningInsteadOfFailingApiCalls,
            final ThingValidationConfig thingValidationConfig,
            final FeatureValidationConfig featureValidationConfig) {
        this.id = checkNotNull(id, "id");
        this.enabled = enabled;
        this.logWarningInsteadOfFailingApiCalls = logWarningInsteadOfFailingApiCalls;
        this.thingValidationConfig = checkNotNull(thingValidationConfig, "thingValidationConfig");
        this.featureValidationConfig = checkNotNull(featureValidationConfig, "featureValidationConfig");
    }

    /**
     * Creates a new {@code ImmutableTmValidationConfig} from the given parameters.
     *
     * @param id the ID.
     * @param enabled whether WoT validation is enabled.
     * @param logWarningInsteadOfFailingApiCalls whether to log warnings instead of failing API calls.
     * @param thingValidationConfig the Thing validation configuration.
     * @param featureValidationConfig the Feature validation configuration.
     * @return the validation config.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static ImmutableTmValidationConfig of(final String id,
            final boolean enabled,
            final boolean logWarningInsteadOfFailingApiCalls,
            final ThingValidationConfig thingValidationConfig,
            final FeatureValidationConfig featureValidationConfig) {
        return new ImmutableTmValidationConfig(id,
                enabled,
                logWarningInsteadOfFailingApiCalls,
                thingValidationConfig,
                featureValidationConfig);
    }

    /**
     * Creates a new {@code ImmutableTmValidationConfig} from a JSON object.
     *
     * @param jsonObject the JSON object of which the validation config is to be created.
     * @return the validation config.
     * @throws NullPointerException if {@code jsonObject} is {@code null}.
     */
    public static ImmutableTmValidationConfig fromJson(final JsonObject jsonObject) {
        final String id = jsonObject.getValueOrThrow(JsonFields.ID);
        final boolean enabled = jsonObject.getValueOrThrow(JsonFields.ENABLED);
        final boolean logWarningInsteadOfFailingApiCalls = jsonObject.getValueOrThrow(JsonFields.LOG_WARNING_INSTEAD_OF_FAILING_API_CALLS);
        // Since ThingValidationConfig and FeatureValidationConfig don't have fromJson methods,
        // we'll need to create default instances for now
        final ThingValidationConfig thingValidationConfig = DefaultThingValidationConfig.of();
        final FeatureValidationConfig featureValidationConfig = DefaultFeatureValidationConfig.of();
        return of(id, enabled, logWarningInsteadOfFailingApiCalls, thingValidationConfig, featureValidationConfig);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean logWarningInsteadOfFailingApiCalls() {
        return logWarningInsteadOfFailingApiCalls;
    }

    @Override
    public ThingValidationConfig getThingValidationConfig() {
        return thingValidationConfig;
    }

    @Override
    public FeatureValidationConfig getFeatureValidationConfig() {
        return featureValidationConfig;
    }

    @Override
    public TmValidationConfig withValidationContext(@Nullable final ValidationContext context) {
        // Since this is an immutable implementation, we just return this instance
        // as the context doesn't affect the configuration
        return this;
    }

    @Override
    public JsonObject toJson() {
        final JsonObjectBuilder jsonObjectBuilder = JsonObject.newBuilder();
        jsonObjectBuilder.set(JsonFields.ID, id);
        jsonObjectBuilder.set(JsonFields.ENABLED, enabled);
        jsonObjectBuilder.set(JsonFields.LOG_WARNING_INSTEAD_OF_FAILING_API_CALLS, logWarningInsteadOfFailingApiCalls);
        // Since FeatureValidationConfig doesn't have toJson, we'll just skip it
        if (thingValidationConfig instanceof JsonObject) {
            jsonObjectBuilder.set(JsonFields.THING_VALIDATION_CONFIG, (JsonObject) thingValidationConfig);
        }
        return jsonObjectBuilder.build();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ImmutableTmValidationConfig that = (ImmutableTmValidationConfig) o;
        return enabled == that.enabled &&
                logWarningInsteadOfFailingApiCalls == that.logWarningInsteadOfFailingApiCalls &&
                Objects.equals(id, that.id) &&
                Objects.equals(thingValidationConfig, that.thingValidationConfig) &&
                Objects.equals(featureValidationConfig, that.featureValidationConfig);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, enabled, logWarningInsteadOfFailingApiCalls, thingValidationConfig, featureValidationConfig);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" +
                "id=" + id +
                ", enabled=" + enabled +
                ", logWarningInsteadOfFailingApiCalls=" + logWarningInsteadOfFailingApiCalls +
                ", thingValidationConfig=" + thingValidationConfig +
                ", featureValidationConfig=" + featureValidationConfig +
                "]";
    }

    /**
     * JSON field definitions.
     */
    static final class JsonFields {
        static final JsonFieldDefinition<String> ID = 
            JsonFactory.newStringFieldDefinition("id");
        static final JsonFieldDefinition<Boolean> ENABLED = 
            JsonFactory.newBooleanFieldDefinition("enabled");
        static final JsonFieldDefinition<Boolean> LOG_WARNING_INSTEAD_OF_FAILING_API_CALLS = 
            JsonFactory.newBooleanFieldDefinition("logWarningInsteadOfFailingApiCalls");
        static final JsonFieldDefinition<JsonObject> THING_VALIDATION_CONFIG = 
            JsonFactory.newJsonObjectFieldDefinition("thingValidationConfig");
    }
} 