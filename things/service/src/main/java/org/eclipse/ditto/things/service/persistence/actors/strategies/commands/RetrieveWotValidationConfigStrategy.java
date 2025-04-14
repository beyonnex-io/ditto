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
import java.util.Set;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.cluster.ddata.ORSet;
import org.eclipse.ditto.base.model.entity.metadata.Metadata;
import org.eclipse.ditto.base.model.headers.WithDittoHeaders;
import org.eclipse.ditto.base.model.headers.entitytag.EntityTag;
import org.eclipse.ditto.things.model.Thing;
import org.eclipse.ditto.things.model.ThingId;
import org.eclipse.ditto.things.model.devops.commands.RetrieveWotValidationConfig;
import org.eclipse.ditto.things.service.common.config.DittoThingsConfig;
import org.eclipse.ditto.internal.utils.persistentactors.results.Result;
import org.eclipse.ditto.internal.utils.persistentactors.results.ResultFactory;
import org.eclipse.ditto.things.model.signals.events.ThingEvent;
import org.eclipse.ditto.wot.validation.config.TmValidationConfig;
import org.eclipse.ditto.things.model.signals.commands.exceptions.ThingNotAccessibleException;

/**
 * This strategy handles the {@link RetrieveWotValidationConfig} command.
 */
@Immutable
public final class RetrieveWotValidationConfigStrategy extends AbstractThingCommandStrategy<RetrieveWotValidationConfig> {

    private final WotValidationConfigDData ddata;

    /**
     * Constructs a new {@code RetrieveWotValidationConfigStrategy} object.
     *
     * @param thingsConfig the configuration settings of the Things service.
     * @param ddata the distributed data handler for WoT validation configs.
     * @param actorSystem the actor system.
     */
    public RetrieveWotValidationConfigStrategy(final DittoThingsConfig thingsConfig, final WotValidationConfigDData ddata,
            final ActorSystem actorSystem) {
        super(RetrieveWotValidationConfig.class, actorSystem);
        this.ddata = ddata;
    }

    @Override
    public boolean isDefined(final RetrieveWotValidationConfig command) {
        return command instanceof RetrieveWotValidationConfig;
    }

    @Override
    public boolean isDefined(final Context<ThingId> context, @Nullable final Thing entity,
            final RetrieveWotValidationConfig command) {
        return isDefined(command);
    }

    @Override
    public Result<ThingEvent<?>> doApply(final Context<ThingId> context, @Nullable final Thing entity,
            final long nextRevision, final RetrieveWotValidationConfig command, @Nullable final Metadata metadata) {
        return ddata.getConfigs()
            .thenApply(orSet -> {
                final Set<TmValidationConfig> configs = orSet.getElements();
                // Find the config for the requested ID
                final Optional<TmValidationConfig> config = configs.stream()
                    .filter(c -> c.getId().equals(context.getState().toString()))
                    .findFirst();
                    
                if (config.isPresent()) {
                    return ResultFactory.<ThingEvent<?>>newQueryResult(command, command);
                } else {
                    return ResultFactory.<ThingEvent<?>>newErrorResult(
                        ThingNotAccessibleException.newBuilder(context.getState())
                            .dittoHeaders(command.getDittoHeaders())
                            .build(),
                        command
                    );
                }
            })
            .toCompletableFuture()
            .join();
    }

    @Override
    protected Optional<Metadata> calculateRelativeMetadata(@Nullable final Thing entity, final RetrieveWotValidationConfig command) {
        return Optional.empty();
    }

    @Override
    public Optional<EntityTag> previousEntityTag(final RetrieveWotValidationConfig command, @Nullable final Thing entity) {
        return Optional.empty();
    }

    @Override
    public Optional<EntityTag> nextEntityTag(final RetrieveWotValidationConfig command, @Nullable final Thing entity) {
        return Optional.empty();
    }
} 