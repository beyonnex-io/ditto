/*
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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
import org.eclipse.ditto.internal.utils.persistentactors.results.Result;
import org.eclipse.ditto.internal.utils.persistentactors.results.ResultFactory;
import org.eclipse.ditto.json.JsonPointer;
import org.eclipse.ditto.things.model.Thing;
import org.eclipse.ditto.things.model.ThingId;
import org.eclipse.ditto.things.model.signals.commands.modify.CreateWotValidationConfig;
import org.eclipse.ditto.things.model.signals.commands.modify.CreateWotValidationConfigResponse;
import org.eclipse.ditto.things.model.signals.events.ThingEvent;
import org.eclipse.ditto.things.model.signals.events.WotValidationConfigCreated;
import org.eclipse.ditto.things.model.devops.ImmutableWoTValidationConfig;

/**
 * This strategy handles the {@link CreateWotValidationConfig} command.
 */
@Immutable
final class CreateWotValidationConfigStrategy extends AbstractThingCommandStrategy<CreateWotValidationConfig> {

    /**
     * Constructs a new {@code CreateWotValidationConfigStrategy} object.
     *
     * @param actorSystem the actor system to use for loading the WoT extension.
     */
    CreateWotValidationConfigStrategy(final ActorSystem actorSystem) {
        super(CreateWotValidationConfig.class, actorSystem);
    }

    @Override
    protected Result<ThingEvent<?>> doApply(final Context<ThingId> context,
            @Nullable final Thing thing,
            final long nextRevision,
            final CreateWotValidationConfig command,
            @Nullable final Metadata metadata) {

        final DittoHeaders dittoHeaders = command.getDittoHeaders();
        final ThingId thingId = context.getState();

        final ImmutableWoTValidationConfig immutableConfig = ImmutableWoTValidationConfig.fromJson(command.getConfig().toJson());

        final WotValidationConfigCreated event = WotValidationConfigCreated.of(
                thingId,
                immutableConfig,
                nextRevision,
                getEventTimestamp(),
                dittoHeaders,
                metadata);

        final CreateWotValidationConfigResponse response = CreateWotValidationConfigResponse.of(thingId, dittoHeaders);

        return ResultFactory.newMutationResult(command, event, response);
    }

    @Override
    public Optional<EntityTag> previousEntityTag(final CreateWotValidationConfig command,
            @Nullable final Thing previousEntity) {
        return Optional.empty();
    }

    @Override
    public Optional<EntityTag> nextEntityTag(final CreateWotValidationConfig command, @Nullable final Thing newEntity) {
        return Optional.empty();
    }
} 