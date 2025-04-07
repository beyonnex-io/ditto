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

import java.util.Optional;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.apache.pekko.actor.ActorSystem;
import org.eclipse.ditto.base.model.entity.metadata.Metadata;
import org.eclipse.ditto.base.model.headers.WithDittoHeaders;
import org.eclipse.ditto.base.model.headers.entitytag.EntityTag;
import org.eclipse.ditto.things.model.Thing;
import org.eclipse.ditto.things.model.ThingId;
import org.eclipse.ditto.things.model.devops.commands.ModifyWotValidationConfig;
import org.eclipse.ditto.things.service.common.config.DittoThingsConfig;
import org.eclipse.ditto.internal.utils.persistentactors.results.Result;
import org.eclipse.ditto.internal.utils.persistentactors.results.ResultFactory;
import org.eclipse.ditto.things.model.signals.events.ThingEvent;

/**
 * This strategy handles the {@link ModifyWotValidationConfig} command.
 */
@Immutable
public final class ModifyWotValidationConfigStrategy extends AbstractThingCommandStrategy<ModifyWotValidationConfig> {

    private final WotValidationConfigDData ddata;

    /**
     * Constructs a new {@code ModifyWotValidationConfigStrategy} object.
     *
     * @param thingsConfig the configuration settings of the Things service.
     * @param ddata the distributed data handler for WoT validation configs.
     * @param actorSystem the actor system.
     */
    public ModifyWotValidationConfigStrategy(final DittoThingsConfig thingsConfig, final WotValidationConfigDData ddata,
            final ActorSystem actorSystem) {
        super(ModifyWotValidationConfig.class, actorSystem);
        this.ddata = ddata;
    }

    @Override
    public boolean isDefined(final ModifyWotValidationConfig command) {
        return command instanceof ModifyWotValidationConfig;
    }

    @Override
    public boolean isDefined(final Context<ThingId> context, @Nullable final Thing entity,
            final ModifyWotValidationConfig command) {
        return isDefined(command);
    }

    @Override
    public Result<ThingEvent<?>> doApply(final Context<ThingId> context, @Nullable final Thing entity,
            final long nextRevision, final ModifyWotValidationConfig command, @Nullable final Metadata metadata) {
        return ResultFactory.newMutationResult(command, null, command);
    }

    @Override
    protected Optional<Metadata> calculateRelativeMetadata(@Nullable final Thing entity, final ModifyWotValidationConfig command) {
        return Optional.empty();
    }

    @Override
    public Optional<EntityTag> previousEntityTag(final ModifyWotValidationConfig command, @Nullable final Thing entity) {
        return Optional.empty();
    }

    @Override
    public Optional<EntityTag> nextEntityTag(final ModifyWotValidationConfig command, @Nullable final Thing entity) {
        return Optional.empty();
    }
} 