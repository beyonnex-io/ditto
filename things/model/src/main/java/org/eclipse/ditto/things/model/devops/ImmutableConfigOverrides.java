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
import org.eclipse.ditto.json.JsonFactory;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonObjectBuilder;
import org.eclipse.ditto.json.JsonValue;

import javax.annotation.concurrent.Immutable;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents the override configuration applied when a validation context matches.
 */
@Immutable
public final class ImmutableConfigOverrides implements Jsonifiable<JsonObject> {

    private static final String ENABLED_FIELD = "enabled";
    private static final String LOG_WARNING_INSTEAD_OF_FAILING_FIELD = "log-warning-instead-of-failing-api-calls";
    private static final String THING_FIELD = "thing";
    private static final String FEATURE_FIELD = "feature";

    private final Boolean enabled;
    private final Boolean logWarningInsteadOfFailing;
    private final ImmutableThingConfigOverrides thingConfig;
    private final ImmutableFeatureConfigOverrides featureConfig;

    private ImmutableConfigOverrides(final Boolean enabled,
            final Boolean logWarningInsteadOfFailing,
            final ImmutableThingConfigOverrides thingConfig,
            final ImmutableFeatureConfigOverrides featureConfig) {
        this.enabled = enabled;
        this.logWarningInsteadOfFailing = logWarningInsteadOfFailing;
        this.thingConfig = thingConfig;
        this.featureConfig = featureConfig;
    }

    public static ImmutableConfigOverrides of(final Boolean enabled,
            final Boolean logWarningInsteadOfFailing,
            final ImmutableThingConfigOverrides thingConfig,
            final ImmutableFeatureConfigOverrides featureConfig) {
        return new ImmutableConfigOverrides(enabled, logWarningInsteadOfFailing, thingConfig, featureConfig);
    }

    public Optional<Boolean> getEnabled() {
        return Optional.ofNullable(enabled);
    }

    public Optional<Boolean> getLogWarningInsteadOfFailing() {
        return Optional.ofNullable(logWarningInsteadOfFailing);
    }

    public Optional<ImmutableThingConfigOverrides> getThingConfig() {
        return Optional.ofNullable(thingConfig);
    }

    public Optional<ImmutableFeatureConfigOverrides> getFeatureConfig() {
        return Optional.ofNullable(featureConfig);
    }

    @Override
    public JsonObject toJson() {
        final JsonObjectBuilder builder = JsonObject.newBuilder();
        if (enabled != null) {
            builder.set(ENABLED_FIELD, JsonFactory.newValue(enabled));
        }
        if (logWarningInsteadOfFailing != null) {
            builder.set(LOG_WARNING_INSTEAD_OF_FAILING_FIELD, JsonFactory.newValue(logWarningInsteadOfFailing));
        }
        if (thingConfig != null) {
            builder.set(THING_FIELD, thingConfig.toJson());
        }
        if (featureConfig != null) {
            builder.set(FEATURE_FIELD, featureConfig.toJson());
        }
        return builder.build();
    }

    public static ImmutableConfigOverrides fromJson(final JsonObject json) {
        final Boolean enabled = json.getValue(ENABLED_FIELD).map(JsonValue::asBoolean).orElse(null);
        final Boolean logWarning = json.getValue(LOG_WARNING_INSTEAD_OF_FAILING_FIELD).map(JsonValue::asBoolean).orElse(null);
        final ImmutableThingConfigOverrides thing = json.getValue(THING_FIELD)
                .filter(JsonValue::isObject)
                .map(JsonValue::asObject)
                .map(ImmutableThingConfigOverrides::fromJson)
                .orElse(null);
        final ImmutableFeatureConfigOverrides feature = json.getValue(FEATURE_FIELD)
                .filter(JsonValue::isObject)
                .map(JsonValue::asObject)
                .map(ImmutableFeatureConfigOverrides::fromJson)
                .orElse(null);
        return of(enabled, logWarning, thing, feature);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ImmutableConfigOverrides)) return false;
        final ImmutableConfigOverrides that = (ImmutableConfigOverrides) o;
        return Objects.equals(enabled, that.enabled) &&
                Objects.equals(logWarningInsteadOfFailing, that.logWarningInsteadOfFailing) &&
                Objects.equals(thingConfig, that.thingConfig) &&
                Objects.equals(featureConfig, that.featureConfig);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enabled, logWarningInsteadOfFailing, thingConfig, featureConfig);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" +
                "enabled=" + enabled +
                ", logWarningInsteadOfFailing=" + logWarningInsteadOfFailing +
                ", thingConfig=" + thingConfig +
                ", featureConfig=" + featureConfig +
                "]";
    }
}
