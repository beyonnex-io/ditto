/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation
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
package org.eclipse.ditto.policies.service.persistence.actors.strategies.commands;

import static org.eclipse.ditto.base.model.common.ConditionChecker.checkNotNull;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.base.model.entity.metadata.Metadata;
import org.eclipse.ditto.base.model.headers.DittoHeaders;
import org.eclipse.ditto.base.model.headers.WithDittoHeaders;
import org.eclipse.ditto.base.model.headers.entitytag.EntityTag;
import org.eclipse.ditto.internal.utils.persistentactors.results.Result;
import org.eclipse.ditto.internal.utils.persistentactors.results.ResultFactory;
import org.eclipse.ditto.policies.model.EntryReference;
import org.eclipse.ditto.policies.model.Label;
import org.eclipse.ditto.policies.model.Policy;
import org.eclipse.ditto.policies.model.PolicyEntry;
import org.eclipse.ditto.policies.model.PolicyId;
import org.eclipse.ditto.policies.model.signals.commands.modify.ModifyPolicyEntryReferences;
import org.eclipse.ditto.policies.model.signals.commands.modify.ModifyPolicyEntryReferencesResponse;
import org.eclipse.ditto.policies.model.signals.events.PolicyEntryReferencesModified;
import org.eclipse.ditto.policies.model.signals.events.PolicyEvent;
import org.eclipse.ditto.policies.service.common.config.PolicyConfig;

/**
 * This strategy handles the {@link ModifyPolicyEntryReferences} command.
 */
@Immutable
final class ModifyPolicyEntryReferencesStrategy
        extends AbstractPolicyCommandStrategy<ModifyPolicyEntryReferences, PolicyEvent<?>> {

    ModifyPolicyEntryReferencesStrategy(final PolicyConfig policyConfig) {
        super(ModifyPolicyEntryReferences.class, policyConfig);
    }

    @Override
    protected Result<PolicyEvent<?>> doApply(final Context<PolicyId> context,
            @Nullable final Policy policy,
            final long nextRevision,
            final ModifyPolicyEntryReferences command,
            @Nullable final Metadata metadata) {

        final Policy nonNullPolicy = checkNotNull(policy, "policy");
        final PolicyId policyId = context.getState();
        final Label label = command.getLabel();
        final List<EntryReference> references = command.getReferences();
        final DittoHeaders dittoHeaders = command.getDittoHeaders();

        if (nonNullPolicy.getEntryFor(label).isEmpty()) {
            return ResultFactory.newErrorResult(
                    policyEntryNotFound(policyId, label, dittoHeaders), command);
        }

        // Validate references
        final java.util.Set<String> seenRefs = new java.util.LinkedHashSet<>();
        for (final EntryReference ref : references) {
            final String refKey = ref.getImportedPolicyId().map(id -> id + ":").orElse("") +
                    ref.getEntryLabel();
            if (!seenRefs.add(refKey)) {
                return ResultFactory.newErrorResult(
                        policyEntryReferenceInvalid(policyId, label,
                                "Duplicate reference to entry '" + ref.getEntryLabel() + "'.",
                                dittoHeaders),
                        command);
            }
            if (ref.isLocalReference() && ref.getEntryLabel().equals(label)) {
                return ResultFactory.newErrorResult(
                        policyEntryReferenceInvalid(policyId, label,
                                "Entry must not reference itself.",
                                dittoHeaders),
                        command);
            }
            if (ref.isLocalReference() && nonNullPolicy.getEntryFor(ref.getEntryLabel()).isEmpty()) {
                return ResultFactory.newErrorResult(
                        policyEntryReferenceInvalid(policyId, label,
                                "Local reference targets entry '" + ref.getEntryLabel() +
                                        "' which does not exist in the policy.",
                                dittoHeaders),
                        command);
            }
        }

        final PolicyEntryReferencesModified event =
                PolicyEntryReferencesModified.of(policyId, label, references, nextRevision,
                        getEventTimestamp(), dittoHeaders, metadata);

        final boolean existingReferencesEmpty = nonNullPolicy.getEntryFor(label)
                .map(entry -> entry.getReferences().isEmpty())
                .orElse(true);

        final ModifyPolicyEntryReferencesResponse rawResponse;
        if (existingReferencesEmpty && !references.isEmpty()) {
            rawResponse = ModifyPolicyEntryReferencesResponse.created(policyId, label, references,
                    createCommandResponseDittoHeaders(dittoHeaders, nextRevision));
        } else {
            rawResponse = ModifyPolicyEntryReferencesResponse.modified(policyId, label,
                    createCommandResponseDittoHeaders(dittoHeaders, nextRevision));
        }

        final WithDittoHeaders response = appendETagHeaderIfProvided(command, rawResponse, nonNullPolicy);
        return ResultFactory.newMutationResult(command, event, response);
    }

    @Override
    public Optional<EntityTag> previousEntityTag(final ModifyPolicyEntryReferences command,
            @Nullable final Policy previousEntity) {
        return Optional.ofNullable(previousEntity)
                .flatMap(p -> p.getEntryFor(command.getLabel()))
                .map(PolicyEntry::getReferences)
                .filter(refs -> !refs.isEmpty())
                .flatMap(EntityTag::fromEntity);
    }

    @Override
    public Optional<EntityTag> nextEntityTag(final ModifyPolicyEntryReferences command,
            @Nullable final Policy newEntity) {
        final List<EntryReference> refs = command.getReferences();
        return refs.isEmpty() ? Optional.empty() : EntityTag.fromEntity(refs);
    }
}
