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

        // Validate that all local references point to existing entries
        for (final EntryReference ref : references) {
            if (ref.isLocalReference() && nonNullPolicy.getEntryFor(ref.getEntryLabel()).isEmpty()) {
                return ResultFactory.newErrorResult(
                        policyEntryInvalid(policyId, label,
                                "Local reference targets entry '" + ref.getEntryLabel() +
                                        "' which does not exist in the policy.",
                                dittoHeaders),
                        command);
            }
        }

        final PolicyEntryReferencesModified event =
                PolicyEntryReferencesModified.of(policyId, label, references, nextRevision,
                        getEventTimestamp(), dittoHeaders, metadata);
        final WithDittoHeaders response = appendETagHeaderIfProvided(command,
                ModifyPolicyEntryReferencesResponse.of(policyId, label,
                        createCommandResponseDittoHeaders(dittoHeaders, nextRevision)),
                nonNullPolicy);
        return ResultFactory.newMutationResult(command, event, response);
    }

    @Override
    public Optional<EntityTag> previousEntityTag(final ModifyPolicyEntryReferences command,
            @Nullable final Policy previousEntity) {
        return Optional.empty();
    }

    @Override
    public Optional<EntityTag> nextEntityTag(final ModifyPolicyEntryReferences command,
            @Nullable final Policy newEntity) {
        return Optional.empty();
    }
}
