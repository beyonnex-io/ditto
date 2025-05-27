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

import org.eclipse.ditto.base.model.entity.metadata.Metadata;
import org.eclipse.ditto.base.model.headers.DittoHeaders;
import org.eclipse.ditto.base.model.headers.entitytag.EntityTag;
import org.eclipse.ditto.internal.utils.persistentactors.results.Result;
import org.eclipse.ditto.internal.utils.persistentactors.results.ResultFactory;
import org.eclipse.ditto.things.model.devops.ImmutableWotValidationConfig;
import org.eclipse.ditto.things.model.devops.WotValidationConfigId;
import org.eclipse.ditto.things.model.devops.WotValidationConfigRevision;
import org.eclipse.ditto.things.model.devops.commands.ModifyWotValidationConfig;
import org.eclipse.ditto.things.model.devops.commands.ModifyWotValidationConfigResponse;
import org.eclipse.ditto.things.model.devops.events.WotValidationConfigCreated;
import org.eclipse.ditto.things.model.devops.events.WotValidationConfigEvent;
import org.eclipse.ditto.things.model.devops.events.WotValidationConfigModified;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.Optional;

/**
 * Strategy for handling {@link ModifyWotValidationConfig} commands.
 */
final class ModifyWotValidationConfigStrategy extends AbstractWotValidationConfigCommandStrategy<ModifyWotValidationConfig> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModifyWotValidationConfigStrategy.class);
    private final WotValidationConfigDData ddata;

    ModifyWotValidationConfigStrategy( final WotValidationConfigDData ddata) {
        super(ModifyWotValidationConfig.class);
        this.ddata = ddata;
    }

    @Override
    protected Optional<Metadata> calculateRelativeMetadata(@Nullable final ImmutableWotValidationConfig previousEntity,
            final ModifyWotValidationConfig command) {
        return Optional.empty();
    }

    @Override
    public Optional<EntityTag> previousEntityTag(final ModifyWotValidationConfig command,
            @Nullable final ImmutableWotValidationConfig previousEntity) {
        return Optional.ofNullable(previousEntity).flatMap(EntityTag::fromEntity);
    }

    @Override
    public Optional<EntityTag> nextEntityTag(final ModifyWotValidationConfig command,
            @Nullable final ImmutableWotValidationConfig newEntity) {
        return Optional.ofNullable(newEntity).flatMap(EntityTag::fromEntity);
    }

    @Override
    protected Result<WotValidationConfigEvent<?>> doApply(final Context<WotValidationConfigId> context,
                                                          @Nullable final ImmutableWotValidationConfig entity,
                                                          final long nextRevision,
                                                          final ModifyWotValidationConfig command,
                                                          @Nullable final Metadata metadata) {
        final Instant now = Instant.now();
        final ImmutableWotValidationConfig inputConfig = command.getValidationConfig();
        final ImmutableWotValidationConfig configWithRevision = ImmutableWotValidationConfig.of(
                inputConfig.getConfigId(),
                inputConfig.isEnabled().orElse(null),
                inputConfig.logWarningInsteadOfFailingApiCalls().orElse(null),
                inputConfig.getThingConfig().orElse(null),
                inputConfig.getFeatureConfig().orElse(null),
                inputConfig.getDynamicConfig(),
                WotValidationConfigRevision.of(nextRevision),
                (entity != null ? entity.getCreated().orElse(now) : now),
                now,
                inputConfig.isDeleted(),
                metadata
        );

        ddata.add(configWithRevision).thenRun(() -> {
            LOGGER.info("Successfully updated DData with merged config");
        });

        final WotValidationConfigEvent<?> event;
        final boolean becameCreated;
        if (entity == null) {
            event = WotValidationConfigCreated.of(
                    command.getEntityId(),
                    configWithRevision,
                    nextRevision,
                    now,
                    command.getDittoHeaders(),
                    metadata
            );
            becameCreated = true;
        } else {
            event = WotValidationConfigModified.of(
                    command.getEntityId(),
                    configWithRevision,
                    nextRevision,
                    now,
                    command.getDittoHeaders(),
                    metadata
            );
            becameCreated = false;
        }
        final boolean isNewConfig = entity == null;

        if (isNewConfig) {
            final ModifyWotValidationConfigResponse response = ModifyWotValidationConfigResponse.created(
                    command.getEntityId(),
                    configWithRevision.toJson(),
                    createCommandResponseDittoHeaders(command.getDittoHeaders(), nextRevision)
            );
            return ResultFactory.newMutationResult(command, event, response, true, false);
        } else {
            final ModifyWotValidationConfigResponse response = ModifyWotValidationConfigResponse.modified(
                    command.getEntityId(),
                    configWithRevision,
                    createCommandResponseDittoHeaders(command.getDittoHeaders(), nextRevision));
            return ResultFactory.newMutationResult(command, event, response, false, false);
        }
    }

    @Override
    public boolean isDefined(final Context<WotValidationConfigId> context, @Nullable final ImmutableWotValidationConfig entity,
            final ModifyWotValidationConfig command) {
        if (entity == null) {
            return true;
        }
        return super.isDefined(context, entity, command);
    }
}