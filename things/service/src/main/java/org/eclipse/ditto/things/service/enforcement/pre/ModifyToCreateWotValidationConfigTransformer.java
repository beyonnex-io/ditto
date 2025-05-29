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
package org.eclipse.ditto.things.service.enforcement.pre;

import org.apache.pekko.actor.ActorSystem;
import org.eclipse.ditto.base.model.signals.Signal;
import org.eclipse.ditto.base.service.signaltransformer.SignalTransformer;
import org.eclipse.ditto.things.model.devops.ImmutableDynamicValidationConfig;
import org.eclipse.ditto.things.model.devops.ImmutableWotValidationConfig;
import org.eclipse.ditto.things.model.devops.WotValidationConfigId;
import org.eclipse.ditto.things.model.devops.WotValidationConfigRevision;
import org.eclipse.ditto.things.model.devops.commands.CreateWotValidationConfig;
import org.eclipse.ditto.things.model.devops.commands.ModifyDynamicConfigSection;
import org.eclipse.ditto.things.model.devops.commands.ModifyWotValidationConfig;
import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Transforms a ModifyWotValidationConfig or ModifyDynamicConfigSection command into a CreateWotValidationConfig
 * if the config does not exist already.
 */
public final class ModifyToCreateWotValidationConfigTransformer implements SignalTransformer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModifyToCreateWotValidationConfigTransformer.class);

    private final WotValidationConfigExistenceChecker existenceChecker;

    public ModifyToCreateWotValidationConfigTransformer(final ActorSystem actorSystem) {
        this(new WotValidationConfigExistenceChecker(actorSystem));
    }

    public ModifyToCreateWotValidationConfigTransformer(final WotValidationConfigExistenceChecker existenceChecker) {
        this.existenceChecker = existenceChecker;
    }

    public ModifyToCreateWotValidationConfigTransformer(final ActorSystem actorSystem, final Config config) {
        this(actorSystem);
    }

    @Override
    public CompletionStage<Signal<?>> apply(final Signal<?> signal) {
        if (signal instanceof ModifyWotValidationConfig modifyCmd) {
            return handleModifyWotValidationConfig(modifyCmd);
        } else if (signal instanceof ModifyDynamicConfigSection modifyDynamicCmd) {
            return handleModifyDynamicConfigSection(modifyDynamicCmd);
        }
        return CompletableFuture.completedFuture(signal);
    }

    private CompletionStage<Signal<?>> handleModifyWotValidationConfig(final ModifyWotValidationConfig modifyCmd) {
        final WotValidationConfigId configId = modifyCmd.getEntityId();
        final ImmutableWotValidationConfig config = modifyCmd.getValidationConfig();

        LOGGER.info("Checking existence for WoT validation config: {}", configId);
        return existenceChecker.checkExistence(configId)
                .thenApply(exists -> {
                    if (!exists) {
                        LOGGER.info("Transforming ModifyWotValidationConfig to CreateWotValidationConfig for: {}", configId);
                        return CreateWotValidationConfig.of(configId, config, modifyCmd.getDittoHeaders());
                    }

                    LOGGER.info("Keeping ModifyWotValidationConfig for existing config: {}", configId);
                    return modifyCmd.setDittoHeaders(modifyCmd.getDittoHeaders());
                });
    }

    private CompletionStage<Signal<?>> handleModifyDynamicConfigSection(final ModifyDynamicConfigSection modifyDynamicCmd) {
        final WotValidationConfigId configId = modifyDynamicCmd.getEntityId();
        final String scopeId = modifyDynamicCmd.getScopeId();
        final ImmutableDynamicValidationConfig dynamicConfig = modifyDynamicCmd.getDynamicConfigSection();

        LOGGER.info("Checking existence for WoT validation config with dynamic section {}: {}", scopeId, configId);
        return existenceChecker.checkExistence(configId)
                .thenApply(exists -> {
                    if (!exists) {
                        LOGGER.info("Transforming ModifyDynamicConfigSection to CreateWotValidationConfig for: {} with scope: {}", configId, scopeId);
                        // Create a new config with just the dynamic section
                        final Instant now = Instant.now();
                        final ImmutableWotValidationConfig newConfig = ImmutableWotValidationConfig.of(
                                configId,
                                null, // enabled
                                null, // logWarningInsteadOfFailingApiCalls
                                null, // thingConfig
                                null, // featureConfig
                                Collections.singletonList(dynamicConfig), // dynamicConfig
                                WotValidationConfigRevision.of(1L), // revision
                                now, // created
                                now, // modified
                                false, // deleted
                                null // metadata
                        );
                        return CreateWotValidationConfig.of(configId, newConfig, modifyDynamicCmd.getDittoHeaders());
                    }

                    LOGGER.info("Keeping ModifyDynamicConfigSection for existing config: {} with scope: {}", configId, scopeId);
                    return modifyDynamicCmd.setDittoHeaders(modifyDynamicCmd.getDittoHeaders());
                });
    }
} 