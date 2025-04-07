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

import org.eclipse.ditto.base.model.json.FieldType;
import org.eclipse.ditto.base.model.json.JsonSchemaVersion;
import org.eclipse.ditto.base.model.json.Jsonifiable;
import org.eclipse.ditto.json.JsonFactory;
import org.eclipse.ditto.json.JsonField;
import org.eclipse.ditto.json.JsonFieldSelector;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonObjectBuilder;
import org.eclipse.ditto.json.JsonValue;

import javax.annotation.concurrent.Immutable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Represents the full WoT ThingModel validation configuration including overrides for dynamic contexts.
 */
@Immutable
public final class ImmutableWoTValidationConfig implements Jsonifiable.WithFieldSelectorAndPredicate<JsonField> {

    private static final String ENABLED_FIELD = "enabled";
    private static final String LOG_WARNING_FIELD = "log-warning-instead-of-failing-api-calls";
    private static final String THING_FIELD = "thing";
    private static final String FEATURE_FIELD = "feature";
    private static final String DYNAMIC_CONFIG_FIELD = "dynamicConfig";

    private final boolean enabled;
    private final boolean logWarningInsteadOfFailing;
    private final ImmutableThingConfigOverrides thingConfig;
    private final ImmutableFeatureConfigOverrides featureConfig;
    private final List<ImmutableDynamicValidationConfig> dynamicConfigs;

    private ImmutableWoTValidationConfig(
            final boolean enabled,
            final boolean logWarningInsteadOfFailing,
            final ImmutableThingConfigOverrides thingConfig,
            final ImmutableFeatureConfigOverrides featureConfig,
            final List<ImmutableDynamicValidationConfig> dynamicConfigs) {
        this.enabled = enabled;
        this.logWarningInsteadOfFailing = logWarningInsteadOfFailing;
        this.thingConfig = thingConfig;
        this.featureConfig = featureConfig;
        this.dynamicConfigs = dynamicConfigs != null ? Collections.unmodifiableList(dynamicConfigs) : Collections.emptyList();
    }

    public static ImmutableWoTValidationConfig of(
            final boolean enabled,
            final boolean logWarningInsteadOfFailing,
            final ImmutableThingConfigOverrides thingConfig,
            final ImmutableFeatureConfigOverrides featureConfig,
            final List<ImmutableDynamicValidationConfig> dynamicConfigs) {
        return new ImmutableWoTValidationConfig(enabled, logWarningInsteadOfFailing, thingConfig, featureConfig, dynamicConfigs);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isLogWarningInsteadOfFailing() {
        return logWarningInsteadOfFailing;
    }

    public Optional<ImmutableThingConfigOverrides> getThingConfig() {
        return Optional.ofNullable(thingConfig);
    }

    public Optional<ImmutableFeatureConfigOverrides> getFeatureConfig() {
        return Optional.ofNullable(featureConfig);
    }

    public List<ImmutableDynamicValidationConfig> getDynamicConfigs() {
        return dynamicConfigs;
    }

    @Override
    public JsonObject toJson() {
        return toJson(FieldType.notHidden());
    }

    @Override
    public JsonObject toJson(final JsonSchemaVersion schemaVersion, final Predicate<JsonField> predicate) {
        final JsonObjectBuilder builder = JsonObject.newBuilder()
                .set(ENABLED_FIELD, JsonFactory.newValue(enabled))
                .set(LOG_WARNING_FIELD, JsonFactory.newValue(logWarningInsteadOfFailing));

        if (thingConfig != null) {
            builder.set(THING_FIELD, thingConfig.toJson());
        }
        if (featureConfig != null) {
            builder.set(FEATURE_FIELD, featureConfig.toJson());
        }
        if (!dynamicConfigs.isEmpty()) {
            builder.set(DYNAMIC_CONFIG_FIELD,
                    JsonFactory.newArrayBuilder()
                            .addAll(dynamicConfigs.stream()
                                    .map(ImmutableDynamicValidationConfig::toJson)
                                    .collect(Collectors.toList()))
                            .build());
        }

        return builder.build();
    }

    @Override
    public JsonObject toJson(final JsonSchemaVersion schemaVersion, final JsonFieldSelector fieldSelector) {
        return toJson(schemaVersion, FieldType.notHidden()).get(fieldSelector);
    }

    public static ImmutableWoTValidationConfig fromJson(final JsonObject json) {
        final boolean enabled = json.getValue(ENABLED_FIELD).map(JsonValue::asBoolean).orElse(false);
        final boolean logWarning = json.getValue(LOG_WARNING_FIELD).map(JsonValue::asBoolean).orElse(false);

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

        final List<ImmutableDynamicValidationConfig> dynamic = json.getValue(DYNAMIC_CONFIG_FIELD)
                .map(JsonValue::asArray)
                .map(array -> array.stream()
                        .map(JsonValue::asObject)
                        .map(ImmutableDynamicValidationConfig::fromJson)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());

        return of(enabled, logWarning, thing, feature, dynamic);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ImmutableWoTValidationConfig)) return false;
        final ImmutableWoTValidationConfig that = (ImmutableWoTValidationConfig) o;
        return enabled == that.enabled &&
                logWarningInsteadOfFailing == that.logWarningInsteadOfFailing &&
                Objects.equals(thingConfig, that.thingConfig) &&
                Objects.equals(featureConfig, that.featureConfig) &&
                Objects.equals(dynamicConfigs, that.dynamicConfigs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enabled, logWarningInsteadOfFailing, thingConfig, featureConfig, dynamicConfigs);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" +
                "enabled=" + enabled +
                ", logWarningInsteadOfFailing=" + logWarningInsteadOfFailing +
                ", thingConfig=" + thingConfig +
                ", featureConfig=" + featureConfig +
                ", dynamicConfigs=" + dynamicConfigs +
                "]";
    }
}