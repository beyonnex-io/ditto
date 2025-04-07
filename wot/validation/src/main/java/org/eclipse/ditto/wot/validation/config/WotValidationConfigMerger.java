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
package org.eclipse.ditto.wot.validation.config;

import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.wot.validation.ValidationContext;

/**
 * Merges static and dynamic WoT validation configurations.
 */
@Immutable
public final class WotValidationConfigMerger {

    private final TmValidationConfig staticConfig;
    private final Set<TmValidationConfig> dynamicConfigs;

    private WotValidationConfigMerger(final TmValidationConfig staticConfig, final Set<TmValidationConfig> dynamicConfigs) {
        this.staticConfig = staticConfig;
        this.dynamicConfigs = dynamicConfigs;
    }

    /**
     * Creates a new instance of {@code WotValidationConfigMerger}.
     *
     * @param staticConfig the static configuration from Helm.
     * @param dynamicConfigs the dynamic configurations from the API.
     * @return the new instance.
     */
    public static WotValidationConfigMerger of(final TmValidationConfig staticConfig, final Set<TmValidationConfig> dynamicConfigs) {
        return new WotValidationConfigMerger(staticConfig, dynamicConfigs);
    }

    /**
     * Merges the static and dynamic configurations based on the validation context.
     *
     * @param context the validation context.
     * @return the merged configuration.
     */
    public TmValidationConfig merge(@Nullable final ValidationContext context) {
        // If validation is not enabled in the static config, return it as is
        if (!staticConfig.isEnabled()) {
            return staticConfig;
        }

        // Find a matching dynamic config based on the validation context
        final Optional<TmValidationConfig> matchingDynamicConfig = findMatchingDynamicConfig(context);
        
        // If no matching dynamic config is found, return the static config
        if (matchingDynamicConfig.isEmpty()) {
            return staticConfig;
        }

        // Merge the static and dynamic configs
        return mergeConfigs(staticConfig, matchingDynamicConfig.get());
    }

    private Optional<TmValidationConfig> findMatchingDynamicConfig(@Nullable final ValidationContext context) {
        if (context == null) {
            return Optional.empty();
        }

        // TODO: Implement matching logic based on validation context
        // For now, just return the first dynamic config if available
        return dynamicConfigs.stream().findFirst();
    }

    private TmValidationConfig mergeConfigs(final TmValidationConfig staticConfig, final TmValidationConfig dynamicConfig) {
        // Create a new config that takes values from the dynamic config if available,
        // otherwise from the static config
        return ImmutableTmValidationConfig.of(
                dynamicConfig.getId(),
                dynamicConfig.isEnabled(),
                dynamicConfig.logWarningInsteadOfFailingApiCalls(),
                mergeThingValidationConfig(staticConfig.getThingValidationConfig(), dynamicConfig.getThingValidationConfig()),
                mergeFeatureValidationConfig(staticConfig.getFeatureValidationConfig(), dynamicConfig.getFeatureValidationConfig())
        );
    }

    private ThingValidationConfig mergeThingValidationConfig(final ThingValidationConfig staticConfig, final ThingValidationConfig dynamicConfig) {
        // TODO: Implement merging logic for ThingValidationConfig
        return staticConfig;
    }

    private FeatureValidationConfig mergeFeatureValidationConfig(final FeatureValidationConfig staticConfig, final FeatureValidationConfig dynamicConfig) {
        // TODO: Implement merging logic for FeatureValidationConfig
        return staticConfig;
    }
} 