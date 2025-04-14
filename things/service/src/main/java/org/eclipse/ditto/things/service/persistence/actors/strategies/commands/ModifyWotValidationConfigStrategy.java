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
package org.eclipse.ditto.things.service.persistence.actors.strategies.commands;

import java.util.Optional;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.apache.pekko.actor.ActorSystem;
import org.eclipse.ditto.base.model.entity.metadata.Metadata;
import org.eclipse.ditto.base.model.headers.DittoHeaders;
import org.eclipse.ditto.base.model.headers.entitytag.EntityTag;
import org.eclipse.ditto.things.model.Thing;
import org.eclipse.ditto.things.model.ThingId;
import org.eclipse.ditto.things.model.devops.ImmutableWoTValidationConfig;
import org.eclipse.ditto.things.model.devops.commands.ModifyWotValidationConfig;
import org.eclipse.ditto.things.model.signals.events.ThingEvent;
import org.eclipse.ditto.things.model.signals.events.WotValidationConfigCreated;
import org.eclipse.ditto.internal.utils.persistentactors.results.Result;
import org.eclipse.ditto.internal.utils.persistentactors.results.ResultFactory;
import org.eclipse.ditto.things.service.common.config.DittoThingsConfig;
import org.eclipse.ditto.wot.validation.config.ImmutableTmValidationConfig;
import org.eclipse.ditto.wot.validation.config.TmValidationConfig;
import org.eclipse.ditto.wot.validation.config.ImmutableThingValidationConfig;
import org.eclipse.ditto.wot.validation.config.ImmutableFeatureValidationConfig;
import org.eclipse.ditto.things.model.devops.ImmutableThingEnforceOverrides;
import org.eclipse.ditto.things.model.devops.ImmutableThingForbidOverrides;
import org.eclipse.ditto.things.model.devops.ImmutableFeatureEnforceOverrides;
import org.eclipse.ditto.things.model.devops.ImmutableFeatureForbidOverrides;

/**
 * This strategy handles the {@link ModifyWotValidationConfig} command.
 */
@Immutable
final class ModifyWotValidationConfigStrategy extends AbstractThingCommandStrategy<ModifyWotValidationConfig> {

    private final WotValidationConfigDData ddata;

    /**
     * Constructs a new {@code ModifyWotValidationConfigStrategy} object.
     *
     * @param thingsConfig the configuration settings of the Things service.
     * @param ddata the distributed data handler for WoT validation configs.
     * @param actorSystem the actor system.
     */
    ModifyWotValidationConfigStrategy(final DittoThingsConfig thingsConfig, final WotValidationConfigDData ddata,
            final ActorSystem actorSystem) {
        super(ModifyWotValidationConfig.class, actorSystem);
        this.ddata = ddata;
    }

    @Override
    protected Result<ThingEvent<?>> doApply(final Context<ThingId> context,
            final Thing thing,
            final long revision,
            final ModifyWotValidationConfig command,
            @Nullable final Metadata metadata) {

        final ImmutableWoTValidationConfig config = ImmutableWoTValidationConfig.fromJson(command.getConfig());
        final DittoHeaders dittoHeaders = command.getDittoHeaders();

        final WotValidationConfigCreated event =
                WotValidationConfigCreated.of(context.getState(), config, revision,
                        getEventTimestamp(), dittoHeaders, metadata);

        // Convert to TmValidationConfig and add to distributed data
        final ImmutableThingValidationConfig thingConfig = ImmutableThingValidationConfig.of(
                config.getThingConfig().isPresent() && config.getThingConfig().get().getEnforce().isPresent() && 
                config.getThingConfig().get().getEnforce().get().getThingDescriptionModification().orElse(false),
                config.getThingConfig().isPresent() && config.getThingConfig().get().getEnforce().isPresent() && 
                config.getThingConfig().get().getEnforce().get().getAttributes().orElse(false),
                config.getThingConfig().isPresent() && config.getThingConfig().get().getEnforce().isPresent() && 
                config.getThingConfig().get().getEnforce().get().getInboxMessagesInput().orElse(false),
                config.getThingConfig().isPresent() && config.getThingConfig().get().getEnforce().isPresent() && 
                config.getThingConfig().get().getEnforce().get().getInboxMessagesOutput().orElse(false),
                config.getThingConfig().isPresent() && config.getThingConfig().get().getEnforce().isPresent() && 
                config.getThingConfig().get().getEnforce().get().getOutboxMessages().orElse(false),
                config.getThingConfig().isPresent() && config.getThingConfig().get().getForbid().isPresent() && 
                config.getThingConfig().get().getForbid().get().getThingDescriptionDeletion().orElse(false),
                config.getThingConfig().isPresent() && config.getThingConfig().get().getForbid().isPresent() && 
                config.getThingConfig().get().getForbid().get().getNonModeledAttributes().orElse(false),
                config.getThingConfig().isPresent() && config.getThingConfig().get().getForbid().isPresent() && 
                config.getThingConfig().get().getForbid().get().getNonModeledInboxMessages().orElse(false),
                config.getThingConfig().isPresent() && config.getThingConfig().get().getForbid().isPresent() && 
                config.getThingConfig().get().getForbid().get().getNonModeledOutboxMessages().orElse(false)
        );

        final ImmutableFeatureValidationConfig featureConfig = ImmutableFeatureValidationConfig.of(
                config.getFeatureConfig().isPresent() && config.getFeatureConfig().get().getEnforce().isPresent() && 
                config.getFeatureConfig().get().getEnforce().get().getFeatureDescriptionModification().orElse(false),
                config.getFeatureConfig().isPresent() && config.getFeatureConfig().get().getEnforce().isPresent() && 
                config.getFeatureConfig().get().getEnforce().get().getPresenceOfModeledFeatures().orElse(false),
                config.getFeatureConfig().isPresent() && config.getFeatureConfig().get().getEnforce().isPresent() && 
                config.getFeatureConfig().get().getEnforce().get().getProperties().orElse(false),
                config.getFeatureConfig().isPresent() && config.getFeatureConfig().get().getEnforce().isPresent() && 
                config.getFeatureConfig().get().getEnforce().get().getDesiredProperties().orElse(false),
                config.getFeatureConfig().isPresent() && config.getFeatureConfig().get().getEnforce().isPresent() && 
                config.getFeatureConfig().get().getEnforce().get().getInboxMessagesInput().orElse(false),
                config.getFeatureConfig().isPresent() && config.getFeatureConfig().get().getEnforce().isPresent() && 
                config.getFeatureConfig().get().getEnforce().get().getInboxMessagesOutput().orElse(false),
                config.getFeatureConfig().isPresent() && config.getFeatureConfig().get().getEnforce().isPresent() && 
                config.getFeatureConfig().get().getEnforce().get().getOutboxMessages().orElse(false),
                config.getFeatureConfig().isPresent() && config.getFeatureConfig().get().getForbid().isPresent() && 
                config.getFeatureConfig().get().getForbid().get().getFeatureDescriptionDeletion().orElse(false),
                config.getFeatureConfig().isPresent() && config.getFeatureConfig().get().getForbid().isPresent() && 
                config.getFeatureConfig().get().getForbid().get().getNonModeledFeatures().orElse(false),
                config.getFeatureConfig().isPresent() && config.getFeatureConfig().get().getForbid().isPresent() && 
                config.getFeatureConfig().get().getForbid().get().getNonModeledProperties().orElse(false),
                config.getFeatureConfig().isPresent() && config.getFeatureConfig().get().getForbid().isPresent() && 
                config.getFeatureConfig().get().getForbid().get().getNonModeledDesiredProperties().orElse(false),
                config.getFeatureConfig().isPresent() && config.getFeatureConfig().get().getForbid().isPresent() && 
                config.getFeatureConfig().get().getForbid().get().getNonModeledInboxMessages().orElse(false),
                config.getFeatureConfig().isPresent() && config.getFeatureConfig().get().getForbid().isPresent() && 
                config.getFeatureConfig().get().getForbid().get().getNonModeledOutboxMessages().orElse(false)
        );

        final TmValidationConfig tmConfig = ImmutableTmValidationConfig.of(
                context.getState().toString(),
                config.isEnabled(),
                config.isLogWarningInsteadOfFailing(),
                thingConfig,
                featureConfig
        );

        // Add config to distributed data and wait for completion
        return ddata.add(tmConfig)
            .thenApply(done -> ResultFactory.<ThingEvent<?>>newMutationResult(command, event, event))
            .toCompletableFuture()
            .join();
    }

    @Override
    public Optional<EntityTag> previousEntityTag(final ModifyWotValidationConfig command, @Nullable final Thing thing) {
        return Optional.empty();
    }

    @Override
    public Optional<EntityTag> nextEntityTag(final ModifyWotValidationConfig command, @Nullable final Thing thing) {
        return Optional.empty();
    }

    @Override
    protected Optional<Metadata> calculateRelativeMetadata(@Nullable final Thing entity, final ModifyWotValidationConfig command) {
        return Optional.empty();
    }
} 