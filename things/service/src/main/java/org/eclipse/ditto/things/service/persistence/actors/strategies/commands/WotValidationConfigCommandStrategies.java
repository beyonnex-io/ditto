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

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.apache.pekko.actor.ActorSystem;
import org.eclipse.ditto.base.model.entity.metadata.Metadata;
import org.eclipse.ditto.internal.utils.persistentactors.commands.AbstractCommandStrategies;
import org.eclipse.ditto.internal.utils.persistentactors.commands.CommandStrategy;
import org.eclipse.ditto.internal.utils.persistentactors.results.Result;
import org.eclipse.ditto.internal.utils.persistentactors.results.ResultFactory;
import org.eclipse.ditto.things.model.devops.WotValidationConfigId;
import org.eclipse.ditto.things.model.devops.commands.WotValidationConfigCommand;
import org.eclipse.ditto.things.model.devops.events.WotValidationConfigEvent;

@Immutable
public final class WotValidationConfigCommandStrategies 
    extends AbstractCommandStrategies<WotValidationConfigCommand<?>, WotValidationConfigDData, WotValidationConfigId, WotValidationConfigEvent> {

    private static final Result<WotValidationConfigEvent> EMPTY_RESULT = ResultFactory.emptyResult();

    @SuppressWarnings("java:S3077") // volatile because of double checked locking pattern
    @Nullable
    private static volatile WotValidationConfigCommandStrategies instance;

    private WotValidationConfigCommandStrategies(final ActorSystem system) {
        super(WotValidationConfigCommand.class);
    }

    public static WotValidationConfigCommandStrategies getInstance(final ActorSystem system) {
        WotValidationConfigCommandStrategies localInstance = instance;
        if (null == localInstance) {
            synchronized (WotValidationConfigCommandStrategies.class) {
                localInstance = instance;
                if (null == localInstance) {
                    instance = localInstance = new WotValidationConfigCommandStrategies(system);
                }
            }
        }
        return localInstance;
    }

    @Override
    protected Result<WotValidationConfigEvent> getEmptyResult() {
        return EMPTY_RESULT;
    }

    @Override
    public boolean isDefined(final WotValidationConfigCommand<?> command) {
        return true;
    }

    @Override
    protected Result<WotValidationConfigEvent> doApply(final CommandStrategy.Context<WotValidationConfigId> context,
            @Nullable final WotValidationConfigDData entity,
            final long nextRevision,
            final WotValidationConfigCommand<?> command,
            @Nullable final Metadata metadata) {
        return getEmptyResult();
    }

    @Override
    public boolean isDefined(final CommandStrategy.Context<WotValidationConfigId> context,
            @Nullable final WotValidationConfigDData entity,
            final WotValidationConfigCommand<?> command) {
        return true;
    }
} 