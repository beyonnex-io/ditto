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
package org.eclipse.ditto.wot.api.config;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.base.model.json.FieldType;
import org.eclipse.ditto.base.model.json.JsonSchemaVersion;
import org.eclipse.ditto.internal.utils.config.ConfigWithFallback;
import org.eclipse.ditto.internal.utils.config.DefaultScopedConfig;
import org.eclipse.ditto.internal.utils.config.KnownConfigValue;
import org.eclipse.ditto.internal.utils.config.ScopedConfig;
import org.eclipse.ditto.json.JsonField;
import org.eclipse.ditto.json.JsonFieldSelector;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonObjectBuilder;
import org.eclipse.ditto.wot.validation.ValidationContext;
import org.eclipse.ditto.wot.validation.config.FeatureValidationConfig;
import org.eclipse.ditto.wot.validation.config.ThingValidationConfig;
import org.eclipse.ditto.wot.validation.config.TmValidationConfig;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * This class is the default implementation of the WoT (Web of Things) {@link org.eclipse.ditto.wot.validation.config.TmValidationConfig}.
 */
@Immutable
final class DefaultTmValidationConfig implements TmValidationConfig {

    private static final String CONFIG_PATH = "tm-model-validation";

    private static final String CONFIG_KEY_DYNAMIC_CONFIGURATION = "dynamic-configuration";

    private final ScopedConfig scopedConfig;
    private final List<InternalDynamicTmValidationConfiguration> dynamicTmValidationConfigurations;
    private final String id;

    private final boolean enabled;
    private final boolean logWarningInsteadOfFailingApiCalls;
    private final ThingValidationConfig thingValidationConfig;
    private final FeatureValidationConfig featureValidationConfig;

    private DefaultTmValidationConfig(final ScopedConfig scopedConfig,
            final List<InternalDynamicTmValidationConfiguration> dynamicTmValidationConfigurations,
            @Nullable final ValidationContext context
    ) {
        this.scopedConfig = scopedConfig;
        this.dynamicTmValidationConfigurations = dynamicTmValidationConfigurations;
        this.id = "default";

        final Config effectiveConfig = dynamicTmValidationConfigurations.stream()
                .flatMap(dynamicConfig -> dynamicConfig.calculateDynamicTmValidationConfigOverrides(context).stream())
                .reduce(ConfigFactory.empty(), (a, b) -> b.withFallback(a))
                .withFallback(scopedConfig.resolve());
        enabled = effectiveConfig.getBoolean("enabled");
        logWarningInsteadOfFailingApiCalls = effectiveConfig.getBoolean("log-warning-instead-of-failing-api-calls");

        thingValidationConfig = DefaultThingValidationConfig.of(effectiveConfig);
        featureValidationConfig = DefaultFeatureValidationConfig.of(effectiveConfig);
    }

    /**
     * Returns an instance of the thing config based on the settings of the specified Config.
     *
     * @param config is supposed to provide the settings of the thing config at {@value #CONFIG_PATH}.
     * @return the instance.
     * @throws org.eclipse.ditto.internal.utils.config.DittoConfigError if {@code config} is invalid.
     */
    public static DefaultTmValidationConfig of(final Config config) {
        final List<InternalDynamicTmValidationConfiguration> dynamicTmValidationConfigurations =
                DefaultScopedConfig.newInstance(config, CONFIG_PATH)
                        .getConfigList(CONFIG_KEY_DYNAMIC_CONFIGURATION)
                        .stream()
                        .map(InternalDynamicTmValidationConfiguration::new)
                        .toList();
        return new DefaultTmValidationConfig(ConfigWithFallback.newInstance(config, CONFIG_PATH, ConfigValue.values()), dynamicTmValidationConfigurations, null);
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
    public JsonObject toJson() {
        final JsonObjectBuilder jsonObjectBuilder = JsonObject.newBuilder();
        jsonObjectBuilder.set("id", id);
        jsonObjectBuilder.set("enabled", enabled);
        jsonObjectBuilder.set("logWarningInsteadOfFailingApiCalls", logWarningInsteadOfFailingApiCalls);
        
        // Add thing validation config fields
        final JsonObjectBuilder thingConfigBuilder = JsonObject.newBuilder();
        thingConfigBuilder.set("enforceThingDescriptionModification", thingValidationConfig.isEnforceThingDescriptionModification());
        thingConfigBuilder.set("enforceAttributes", thingValidationConfig.isEnforceAttributes());
        thingConfigBuilder.set("enforceInboxMessagesInput", thingValidationConfig.isEnforceInboxMessagesInput());
        thingConfigBuilder.set("enforceInboxMessagesOutput", thingValidationConfig.isEnforceInboxMessagesOutput());
        thingConfigBuilder.set("enforceOutboxMessages", thingValidationConfig.isEnforceOutboxMessages());
        thingConfigBuilder.set("forbidThingDescriptionDeletion", thingValidationConfig.isForbidThingDescriptionDeletion());
        thingConfigBuilder.set("forbidNonModeledAttributes", thingValidationConfig.isForbidNonModeledAttributes());
        thingConfigBuilder.set("forbidNonModeledInboxMessages", thingValidationConfig.isForbidNonModeledInboxMessages());
        thingConfigBuilder.set("forbidNonModeledOutboxMessages", thingValidationConfig.isForbidNonModeledOutboxMessages());
        jsonObjectBuilder.set("thingValidationConfig", thingConfigBuilder.build());
        
        // Add feature validation config fields
        final JsonObjectBuilder featureConfigBuilder = JsonObject.newBuilder();
        featureConfigBuilder.set("enforceFeatureDescriptionModification", featureValidationConfig.isEnforceFeatureDescriptionModification());
        featureConfigBuilder.set("enforcePresenceOfModeledFeatures", featureValidationConfig.isEnforcePresenceOfModeledFeatures());
        featureConfigBuilder.set("enforceProperties", featureValidationConfig.isEnforceProperties());
        featureConfigBuilder.set("enforceDesiredProperties", featureValidationConfig.isEnforceDesiredProperties());
        featureConfigBuilder.set("enforceInboxMessagesInput", featureValidationConfig.isEnforceInboxMessagesInput());
        featureConfigBuilder.set("enforceInboxMessagesOutput", featureValidationConfig.isEnforceInboxMessagesOutput());
        featureConfigBuilder.set("enforceOutboxMessages", featureValidationConfig.isEnforceOutboxMessages());
        featureConfigBuilder.set("forbidFeatureDescriptionDeletion", featureValidationConfig.isForbidFeatureDescriptionDeletion());
        featureConfigBuilder.set("forbidNonModeledFeatures", featureValidationConfig.isForbidNonModeledFeatures());
        featureConfigBuilder.set("forbidNonModeledProperties", featureValidationConfig.isForbidNonModeledProperties());
        featureConfigBuilder.set("forbidNonModeledDesiredProperties", featureValidationConfig.isForbidNonModeledDesiredProperties());
        featureConfigBuilder.set("forbidNonModeledInboxMessages", featureValidationConfig.isForbidNonModeledInboxMessages());
        featureConfigBuilder.set("forbidNonModeledOutboxMessages", featureValidationConfig.isForbidNonModeledOutboxMessages());
        jsonObjectBuilder.set("featureValidationConfig", featureConfigBuilder.build());
        
        return jsonObjectBuilder.build();
    }

    @Override
    public TmValidationConfig withValidationContext(@Nullable final ValidationContext context) {
        if (dynamicTmValidationConfigurations.isEmpty()) {
            return this;
        } else {
            return new DefaultTmValidationConfig(scopedConfig, dynamicTmValidationConfigurations, context);
        }
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
        return Objects.equals(dynamicTmValidationConfigurations, that.dynamicTmValidationConfigurations) &&
                enabled == that.enabled &&
                logWarningInsteadOfFailingApiCalls == that.logWarningInsteadOfFailingApiCalls &&
                Objects.equals(thingValidationConfig, that.thingValidationConfig) &&
                Objects.equals(featureValidationConfig, that.featureValidationConfig) &&
                Objects.equals(scopedConfig, that.scopedConfig);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dynamicTmValidationConfigurations, enabled, logWarningInsteadOfFailingApiCalls,
                thingValidationConfig, featureValidationConfig, scopedConfig);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" +
                "dynamicTmValidationConfiguration=" + dynamicTmValidationConfigurations +
                ", enabled=" + enabled +
                ", logWarningInsteadOfFailingApiCalls=" + logWarningInsteadOfFailingApiCalls +
                ", thingValidationConfig=" + thingValidationConfig +
                ", featureValidationConfig=" + featureValidationConfig +
                ", scopedConfig=" + scopedConfig +
                "]";
    }

    @Override
    public JsonObject toJson(final JsonSchemaVersion schemaVersion, final Predicate<JsonField> predicate) {
        return  toJson(JsonSchemaVersion.LATEST, predicate);
    }

    @Override
    public JsonObject toJson(final JsonSchemaVersion schemaVersion, final JsonFieldSelector fieldSelector) {
        return toJson(FieldType.notHidden());
    }

    /**
     * An enumeration of the known config path expressions and their associated default values for
     * {@code DefaultTmValidationConfig}.
     */
    enum ConfigValue implements KnownConfigValue {

        ENABLED("enabled", true),

        LOG_WARNING_INSTEAD_OF_FAILING_API_CALLS("log-warning-instead-of-failing-api-calls", false);

        private final String path;
        private final Object defaultValue;

        ConfigValue(final String thePath, final Object theDefaultValue) {
            path = thePath;
            defaultValue = theDefaultValue;
        }

        @Override
        public Object getDefaultValue() {
            return defaultValue;
        }

        @Override
        public String getConfigPath() {
            return path;
        }
    }
}
