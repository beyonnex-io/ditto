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
package org.eclipse.ditto.things.service.persistence.actors.strategies.commands;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.apache.pekko.actor.ActorSystem;
import org.eclipse.ditto.base.model.entity.metadata.Metadata;
import org.eclipse.ditto.base.model.headers.entitytag.EntityTag;
import org.eclipse.ditto.internal.utils.persistentactors.results.Result;
import org.eclipse.ditto.internal.utils.persistentactors.results.ResultFactory;
import org.eclipse.ditto.things.model.Thing;
import org.eclipse.ditto.things.model.ThingId;
import org.eclipse.ditto.things.model.signals.events.ThingEvent;
import org.eclipse.ditto.things.model.devops.ImmutableWoTValidationConfig;
import org.eclipse.ditto.things.model.devops.ImmutableThingConfigOverrides;
import org.eclipse.ditto.things.model.devops.ImmutableFeatureConfigOverrides;
import org.eclipse.ditto.things.model.devops.ImmutableThingEnforceOverrides;
import org.eclipse.ditto.things.model.devops.ImmutableThingForbidOverrides;
import org.eclipse.ditto.things.model.devops.ImmutableFeatureEnforceOverrides;
import org.eclipse.ditto.things.model.devops.ImmutableFeatureForbidOverrides;
import org.eclipse.ditto.things.model.devops.commands.RetrieveMergedWotValidationConfig;
import org.eclipse.ditto.things.model.devops.commands.RetrieveMergedWotValidationConfigResponse;
import org.eclipse.ditto.wot.api.config.WotConfig;
import org.eclipse.ditto.wot.validation.config.TmValidationConfig;
import org.eclipse.ditto.wot.validation.config.WotValidationConfigMerger;
import org.eclipse.ditto.things.model.ValidationContext;

/**
 * This strategy handles the {@link RetrieveMergedWotValidationConfig} command.
 */
@Immutable
final class RetrieveMergedWotValidationConfigStrategy extends AbstractThingCommandStrategy<RetrieveMergedWotValidationConfig> {

    private final WotValidationConfigDData ddata;
    private final WotConfig wotConfig;

    /**
     * Constructs a new {@code RetrieveMergedWotValidationConfigStrategy} object.
     *
     * @param ddata the distributed data handler for WoT validation configs.
     * @param wotConfig the WoT configuration.
     * @param actorSystem the actor system.
     */
    RetrieveMergedWotValidationConfigStrategy(final WotValidationConfigDData ddata, final WotConfig wotConfig, final ActorSystem actorSystem) {
        super(RetrieveMergedWotValidationConfig.class, actorSystem);
        this.ddata = ddata;
        this.wotConfig = wotConfig;
    }

    @Override
    protected Result<ThingEvent<?>> doApply(final Context<ThingId> context,
            @Nullable final Thing thing,
            final long nextRevision,
            final RetrieveMergedWotValidationConfig command,
            @Nullable final Metadata metadata) {

        return ddata.getConfigs()
                .thenApply(orSet -> {
                    final Set<TmValidationConfig> dynamicConfigs = orSet.getElements();

                    final TmValidationConfig mergedConfig = WotValidationConfigMerger.of(wotConfig.getValidationConfig(), dynamicConfigs)
                            .merge(convertToWotValidationContext(command.getValidationContext()));

                    final ImmutableThingConfigOverrides thingConfig = ImmutableThingConfigOverrides.of(
                            ImmutableThingEnforceOverrides.of(
                                    mergedConfig.getThingValidationConfig().isEnforceThingDescriptionModification(),
                                    mergedConfig.getThingValidationConfig().isEnforceAttributes(),
                                    mergedConfig.getThingValidationConfig().isEnforceInboxMessagesInput(),
                                    mergedConfig.getThingValidationConfig().isEnforceInboxMessagesOutput(),
                                    mergedConfig.getThingValidationConfig().isEnforceOutboxMessages()
                            ),
                            ImmutableThingForbidOverrides.of(
                                    mergedConfig.getThingValidationConfig().isForbidThingDescriptionDeletion(),
                                    mergedConfig.getThingValidationConfig().isForbidNonModeledAttributes(),
                                    mergedConfig.getThingValidationConfig().isForbidNonModeledInboxMessages(),
                                    mergedConfig.getThingValidationConfig().isForbidNonModeledOutboxMessages()
                            )
                    );

                    final ImmutableFeatureConfigOverrides featureConfig = ImmutableFeatureConfigOverrides.of(
                            ImmutableFeatureEnforceOverrides.of(
                                    mergedConfig.getFeatureValidationConfig().isEnforceFeatureDescriptionModification(),
                                    mergedConfig.getFeatureValidationConfig().isEnforcePresenceOfModeledFeatures(),
                                    mergedConfig.getFeatureValidationConfig().isEnforceProperties(),
                                    mergedConfig.getFeatureValidationConfig().isEnforceDesiredProperties(),
                                    mergedConfig.getFeatureValidationConfig().isEnforceInboxMessagesInput(),
                                    mergedConfig.getFeatureValidationConfig().isEnforceInboxMessagesOutput(),
                                    mergedConfig.getFeatureValidationConfig().isEnforceOutboxMessages()
                            ),
                            ImmutableFeatureForbidOverrides.of(
                                    mergedConfig.getFeatureValidationConfig().isForbidFeatureDescriptionDeletion(),
                                    mergedConfig.getFeatureValidationConfig().isForbidNonModeledFeatures(),
                                    mergedConfig.getFeatureValidationConfig().isForbidNonModeledProperties(),
                                    mergedConfig.getFeatureValidationConfig().isForbidNonModeledDesiredProperties(),
                                    mergedConfig.getFeatureValidationConfig().isForbidNonModeledInboxMessages(),
                                    mergedConfig.getFeatureValidationConfig().isForbidNonModeledOutboxMessages()
                            )
                    );

                    final ImmutableWoTValidationConfig wotConfig = ImmutableWoTValidationConfig.of(
                            mergedConfig.isEnabled(),
                            mergedConfig.logWarningInsteadOfFailingApiCalls(),
                            thingConfig,
                            featureConfig,
                            Collections.emptyList()
                    );

                    final RetrieveMergedWotValidationConfigResponse response = RetrieveMergedWotValidationConfigResponse.of(
                            Set.of(wotConfig),
                            command.getDittoHeaders()
                    );

                    return ResultFactory.<ThingEvent<?>>newQueryResult(command, response);
                })
                .toCompletableFuture()
                .join();
    }

    @Override
    public boolean isDefined(final RetrieveMergedWotValidationConfig command) {
        return true;
    }

    @Override
    protected Optional<Metadata> calculateRelativeMetadata(@Nullable final Thing entity, final RetrieveMergedWotValidationConfig command) {
        return Optional.empty();
    }

    @Override
    public Optional<EntityTag> previousEntityTag(final RetrieveMergedWotValidationConfig command, @Nullable final Thing entity) {
        return Optional.empty();
    }

    @Override
    public Optional<EntityTag> nextEntityTag(final RetrieveMergedWotValidationConfig command, @Nullable final Thing entity) {
        return Optional.empty();
    }

    /**
     * Converts a ValidationContext from things/model to wot/validation.
     *
     * @param thingsValidationContext the ValidationContext from things/model
     * @return the ValidationContext from wot/validation
     */
    private org.eclipse.ditto.wot.validation.ValidationContext convertToWotValidationContext(
            final ValidationContext thingsValidationContext) {
        return org.eclipse.ditto.wot.validation.ValidationContext.buildValidationContext(
                thingsValidationContext.dittoHeaders(),
                thingsValidationContext.thingDefinition(),
                thingsValidationContext.featureDefinition()
        );
    }
} 