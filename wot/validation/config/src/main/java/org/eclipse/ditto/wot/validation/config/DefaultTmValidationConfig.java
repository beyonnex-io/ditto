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

import java.util.Objects;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.internal.utils.config.DefaultScopedConfig;
import org.eclipse.ditto.internal.utils.config.ScopedConfig;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.wot.validation.ValidationContext;

import com.typesafe.config.Config;

/**
 * Default implementation of {@link TmValidationConfig}.
 */
@Immutable
public final class DefaultTmValidationConfig implements TmValidationConfig {

    private static final String CONFIG_PATH = "validation";

    private final String id;
    private final boolean enabled;
    private final boolean logWarningInsteadOfFailingApiCalls;
    private final ThingValidationConfig thingValidationConfig;
    private final FeatureValidationConfig featureValidationConfig;

    private DefaultTmValidationConfig(final ScopedConfig scopedConfig) {
        this.id = scopedConfig.getString(TmValidationConfig.ConfigValue.ID.getConfigPath());
        this.enabled = scopedConfig.getBoolean(TmValidationConfig.ConfigValue.ENABLED.getConfigPath());
        this.logWarningInsteadOfFailingApiCalls = scopedConfig.getBoolean(TmValidationConfig.ConfigValue.LOG_WARNING_INSTEAD_OF_FAILING_API_CALLS.getConfigPath());
        this.thingValidationConfig = DefaultThingValidationConfig.of(scopedConfig);
        this.featureValidationConfig = DefaultFeatureValidationConfig.of(scopedConfig);
    }

    /**
     * Returns an instance of {@code DefaultTmValidationConfig} based on the settings of the specified Config.
     *
     * @param config is supposed to provide the settings of the validation config at {@value #CONFIG_PATH}.
     * @return the instance.
     * @throws org.eclipse.ditto.internal.utils.config.DittoConfigError if {@code config} is invalid.
     */
    public static DefaultTmValidationConfig of(final Config config) {
        return new DefaultTmValidationConfig(DefaultScopedConfig.newInstance(config, CONFIG_PATH));
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
    public boolean isLogWarningInsteadOfFailingApiCalls() {
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
    public JsonObject toJson() {
        return JsonObject.newBuilder()
                .set(TmValidationConfig.JsonFields.ID, id)
                .set(TmValidationConfig.JsonFields.ENABLED, enabled)
                .set(TmValidationConfig.JsonFields.LOG_WARNING_INSTEAD_OF_FAILING_API_CALLS, logWarningInsteadOfFailingApiCalls)
                .set(TmValidationConfig.JsonFields.THING_VALIDATION_CONFIG, thingValidationConfig.toJson())
                .set(TmValidationConfig.JsonFields.FEATURE_VALIDATION_CONFIG, featureValidationConfig.toJson())
                .build();
    }

    @Override
    public TmValidationConfig withValidationContext(@Nullable final ValidationContext context) {
        // Since this is a default implementation, we just return this instance
        // as the context doesn't affect the configuration
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final DefaultTmValidationConfig that = (DefaultTmValidationConfig) o;
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
} 