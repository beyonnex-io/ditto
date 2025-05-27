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
import org.eclipse.ditto.things.model.devops.ImmutableDynamicValidationConfig;
import org.eclipse.ditto.things.model.devops.WotValidationConfigId;
import org.eclipse.ditto.things.model.devops.commands.ModifyDynamicConfigSection;
import org.eclipse.ditto.things.model.devops.commands.ModifyWotValidationConfigResponse;
import org.eclipse.ditto.things.model.devops.events.WotValidationConfigEvent;
import org.eclipse.ditto.things.model.devops.events.WotValidationConfigModified;
import org.eclipse.ditto.things.model.devops.WotValidationConfigRevision;
import org.eclipse.ditto.things.model.signals.commands.exceptions.WotValidationConfigNotAccessibleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionException;

/**
 * Strategy for handling {@link org.eclipse.ditto.things.model.devops.commands.ModifyDynamicConfigSection} commands.
 * <p>
 * This strategy modifies or creates a specific dynamic config section in a WoT validation configuration, identified by its scope ID.
 * If a section with the given scope ID already exists, it is updated; otherwise, a new section is created.
 * A modification event is emitted and the distributed data store is updated accordingly.
 * </p>
 * <p>
 * This class is immutable and thread-safe.
 * </p>
 */
final class ModifyDynamicConfigSectionStrategy extends AbstractWotValidationConfigCommandStrategy<ModifyDynamicConfigSection> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModifyDynamicConfigSectionStrategy.class);
    private final WotValidationConfigDData ddata;

    /**
     * Constructs a new {@code ModifyDynamicConfigSectionStrategy} object.
     *
     * @param ddata the DData instance for WoT validation configs.
     */
    ModifyDynamicConfigSectionStrategy(final WotValidationConfigDData ddata) {
        super(ModifyDynamicConfigSection.class);
        this.ddata = ddata;
    }

    /**
     * Calculates relative metadata for the modified dynamic config section. Always returns empty for this strategy.
     *
     * @param previousEntity the current WoT validation config entity, or {@code null} if not found.
     * @param command the modify dynamic config section command.
     * @return always {@code Optional.empty()}.
     */
    @Override
    protected Optional<Metadata> calculateRelativeMetadata(@Nullable final ImmutableWotValidationConfig previousEntity,
            final ModifyDynamicConfigSection command) {
        return Optional.empty();
    }

    @Override
    public Optional<EntityTag> previousEntityTag(final ModifyDynamicConfigSection command,
            @Nullable final ImmutableWotValidationConfig previousEntity) {
        return Optional.ofNullable(previousEntity).flatMap(EntityTag::fromEntity);
    }

    @Override
    public Optional<EntityTag> nextEntityTag(final ModifyDynamicConfigSection command,
            @Nullable final ImmutableWotValidationConfig newEntity) {
        return Optional.ofNullable(newEntity).flatMap(EntityTag::fromEntity);
    }

    /**
     * Applies the modify dynamic config section command to the current entity, updating or creating the section.
     *
     * @param context the command context.
     * @param entity the current WoT validation config entity, or {@code null} if not found.
     * @param nextRevision the next revision number.
     * @param command the modify dynamic config section command.
     * @param metadata optional metadata.
     * @return a successful result with the modification event and updated config, or an error result if not found or invalid.
     */
    @Override
    protected Result<WotValidationConfigEvent<?>> doApply(final Context<WotValidationConfigId> context,
            @Nullable final ImmutableWotValidationConfig entity,
            final long nextRevision,
            final ModifyDynamicConfigSection command,
            @Nullable final Metadata metadata) {
        final DittoHeaders dittoHeaders = command.getDittoHeaders();
        final String scopeId = command.getScopeId();
        final ImmutableDynamicValidationConfig newSection = command.getDynamicConfigSection();
        final Instant now = Instant.now();

        LOGGER.info("Received ModifyDynamicConfigSection: scopeId={}, newSection={}", scopeId, newSection.toJson());

        // First validate the scope ID
        if (!newSection.getScopeId().equals(scopeId)) {
            LOGGER.error("Scope ID mismatch: command scopeId={}, section scopeId={}", scopeId, newSection.getScopeId());
            return ResultFactory.newErrorResult(
                    WotValidationConfigNotAccessibleException.newBuilderForScope(scopeId)
                            .description("Scope ID mismatch: command scopeId=" + scopeId +
                                    ", section scopeId=" + newSection.getScopeId())
                            .dittoHeaders(dittoHeaders)
                            .build(),
                    command
            );
        }

        // Then handle the config creation/update
        final ImmutableWotValidationConfig configToSave;
        final boolean isNewConfig;

        if (entity == null) {
            // Create a new global config with just this dynamic section
            LOGGER.info("No global WoT validation config found, creating new one with dynamic section for scope: {}", scopeId);
            configToSave = ImmutableWotValidationConfig.of(
                    command.getEntityId(),
                    null, // enabled
                    null, // logWarningInsteadOfFailingApiCalls
                    null, // thingConfig
                    null, // featureConfig
                    List.of(newSection), // dynamicConfig
                    WotValidationConfigRevision.of(nextRevision),
                    now, // created
                    now, // modified
                    false, // deleted
                    metadata
            );
            isNewConfig = true;
        } else {
            // Update existing config
            List<ImmutableDynamicValidationConfig> updatedDynamicConfig = new ArrayList<>();
            boolean replaced = false;

            for (ImmutableDynamicValidationConfig section : entity.getDynamicConfig()) {
                if (section.getScopeId().equals(scopeId)) {
                    updatedDynamicConfig.add(newSection);
                    replaced = true;
                } else {
                    updatedDynamicConfig.add(section);
                }
            }

            if (!replaced) {
                updatedDynamicConfig.add(newSection);
            }

            configToSave = createWotValidationConfig(entity, updatedDynamicConfig,
                    WotValidationConfigRevision.of(nextRevision), now, metadata);
            isNewConfig = !replaced;
        }

        final WotValidationConfigEvent<?> event = WotValidationConfigModified.of(
                command.getEntityId(),
                configToSave,
                nextRevision,
                now,
                dittoHeaders,
                metadata
        );

        try {
            ddata.add(configToSave)
                    .thenRun(() -> LOGGER.info("Successfully {} global config with dynamic section",
                            isNewConfig ? "created new" : "updated"))
                    .exceptionally(error -> {
                        LOGGER.error("Failed to {} global config: {}",
                                isNewConfig ? "create new" : "update",
                                error instanceof CompletionException ? error.getCause().getMessage() : error.getMessage());
                        return null;
                    });
        } catch (Exception e) {
            LOGGER.error("Error while {} global config: {}",
                    isNewConfig ? "creating new" : "updating", e.getMessage(), e);
            return ResultFactory.newErrorResult(
                    WotValidationConfigNotAccessibleException.newBuilderForScope(scopeId)
                            .description("Failed to " + (isNewConfig ? "create new" : "update") +
                                    " WoT validation config: " + e.getMessage())
                            .dittoHeaders(dittoHeaders)
                            .build(),
                    command
            );
        }

        final ModifyWotValidationConfigResponse response;
        if (isNewConfig) {
            response = ModifyWotValidationConfigResponse.created(
                    command.getEntityId(),
                    configToSave.toJson(),
                    createCommandResponseDittoHeaders(dittoHeaders, nextRevision)
            );
        } else {
            response = ModifyWotValidationConfigResponse.modified(
                    command.getEntityId(),
                    configToSave,
                    createCommandResponseDittoHeaders(dittoHeaders, nextRevision)
            );
        }
        return ResultFactory.newMutationResult(command, event, response, isNewConfig, false);
    }

    @Override
    public boolean isDefined(final Context<WotValidationConfigId> context,
            @Nullable final ImmutableWotValidationConfig entity,
            final ModifyDynamicConfigSection command) {
        return true;
    }

    private static ImmutableWotValidationConfig createWotValidationConfig(final ImmutableWotValidationConfig entity,
            final List<ImmutableDynamicValidationConfig> updatedDynamicConfig,
            final WotValidationConfigRevision nextRevision,
            final Instant now,
            final Metadata metadata) {
        final WotValidationConfigId entityId = (WotValidationConfigId) entity.getEntityId()
                .orElseThrow(() -> new IllegalStateException("Entity ID is required"));
        return ImmutableWotValidationConfig.of(
                entityId,
                entity.isEnabled().orElse(null),
                entity.logWarningInsteadOfFailingApiCalls().orElse(null),
                entity.getThingConfig().orElse(null),
                entity.getFeatureConfig().orElse(null),
                updatedDynamicConfig,
                nextRevision,
                entity.getCreated().orElse(now),
                now,
                entity.isDeleted(),
                metadata
        );
    }
} 