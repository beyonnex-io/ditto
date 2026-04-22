/*
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
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
package org.eclipse.ditto.policies.enforcement.pre;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.pekko.actor.ActorSystem;
import org.eclipse.ditto.base.model.auth.AuthorizationContext;
import org.eclipse.ditto.base.model.exceptions.DittoRuntimeException;
import org.eclipse.ditto.base.model.headers.DittoHeaders;
import org.eclipse.ditto.base.model.signals.Signal;
import org.eclipse.ditto.internal.utils.pekko.logging.DittoLoggerFactory;
import org.eclipse.ditto.internal.utils.pekko.logging.ThreadSafeDittoLogger;
import org.eclipse.ditto.policies.api.Permission;
import org.eclipse.ditto.policies.enforcement.PolicyEnforcer;
import org.eclipse.ditto.policies.enforcement.PolicyEnforcerProvider;
import org.eclipse.ditto.policies.enforcement.PolicyEnforcerProviderExtension;
import org.eclipse.ditto.policies.model.AllowedImportAddition;
import org.eclipse.ditto.policies.model.EntryReference;
import org.eclipse.ditto.policies.model.ImportableType;
import org.eclipse.ditto.policies.model.Label;
import org.eclipse.ditto.policies.model.PoliciesModelFactory;
import org.eclipse.ditto.policies.model.Policy;
import org.eclipse.ditto.policies.model.PolicyEntry;
import org.eclipse.ditto.policies.model.PolicyId;
import org.eclipse.ditto.policies.model.PolicyImport;
import org.eclipse.ditto.policies.model.PolicyImportInvalidException;
import org.eclipse.ditto.policies.model.ResourceKey;
import org.eclipse.ditto.policies.model.enforcers.Enforcer;
import org.eclipse.ditto.policies.model.signals.commands.exceptions.PolicyNotAccessibleException;
import org.eclipse.ditto.policies.model.signals.commands.modify.CreatePolicy;
import org.eclipse.ditto.policies.model.signals.commands.modify.ModifyPolicy;
import org.eclipse.ditto.policies.model.signals.commands.modify.ModifyPolicyEntries;
import org.eclipse.ditto.policies.model.signals.commands.modify.ModifyPolicyEntry;
import org.eclipse.ditto.policies.model.signals.commands.modify.ModifyPolicyImport;
import org.eclipse.ditto.policies.model.signals.commands.modify.ModifyPolicyImports;
import org.eclipse.ditto.policies.model.signals.commands.modify.ModifyPolicyEntryReferences;
import org.eclipse.ditto.policies.model.signals.commands.modify.PolicyModifyCommand;

import com.typesafe.config.Config;

/**
 * Pre-Enforcer for authorizing modifications to policy imports.
 * <p>
 * <b>Note:</b> The validation performed here is best-effort. The pre-enforcer reads policies from the enforcer cache,
 * but the persistence actor applies the command against its authoritative state. A concurrent modification (e.g. a
 * racing modification) between cache read and persistence write could change the targets or
 * allowedImportAdditions, making the pre-enforcer check stale (TOCTOU). The pre-enforcer therefore acts as a
 * first line of defense, not a guarantee.
 */
public class PolicyImportsPreEnforcer implements PreEnforcer {

    private static final ThreadSafeDittoLogger LOG =
            DittoLoggerFactory.getThreadSafeLogger(PolicyImportsPreEnforcer.class);
    private static final String POLICY_RESOURCE = "policy";
    private static final String ENTRIES_PREFIX = "/entries/";
    private final PolicyEnforcerProvider policyEnforcerProvider;

    /**
     * Constructs a new instance of PolicyImportsPreEnforcer extension.
     *
     * @param actorSystem the actor system in which to load the extension.
     * @param config the configuration for this extension.
     */
    @SuppressWarnings("unused")
    public PolicyImportsPreEnforcer(final ActorSystem actorSystem, final Config config) {
        policyEnforcerProvider = PolicyEnforcerProviderExtension.get(actorSystem).getPolicyEnforcerProvider();
    }

    /**
     * Package-private constructor to pass a PolicyEnforcerProvider in tests.
     *
     * @param policyEnforcerProvider a PolicyEnforcerProvider
     */
    PolicyImportsPreEnforcer(final PolicyEnforcerProvider policyEnforcerProvider) {
        this.policyEnforcerProvider = policyEnforcerProvider;
    }

    @Override
    public CompletionStage<Signal<?>> apply(final Signal<?> signal) {
        if (signal instanceof ModifyPolicy modifyPolicy) {
            return doApply(modifyPolicy.getPolicy().getPolicyImports().stream(), modifyPolicy)
                    .thenCompose(s -> validateImportRefsInEntries(modifyPolicy.getPolicy(), modifyPolicy));
        } else if (signal instanceof CreatePolicy createPolicy) {
            return doApply(createPolicy.getPolicy().getPolicyImports().stream(), createPolicy)
                    .thenCompose(s -> validateImportRefsInEntries(createPolicy.getPolicy(), createPolicy));
        } else if (signal instanceof ModifyPolicyImports modifyPolicyImports) {
            return doApply(modifyPolicyImports.getPolicyImports().stream(), modifyPolicyImports);
        } else if (signal instanceof ModifyPolicyImport modifyPolicyImport) {
            return doApply(Stream.of(modifyPolicyImport.getPolicyImport()), modifyPolicyImport);
        } else if (signal instanceof ModifyPolicyEntryReferences modifyReferences) {
            return validateReferencesModification(modifyReferences);
        } else if (signal instanceof ModifyPolicyEntry modifyPolicyEntry) {
            return validateImportRefsInEntries(List.of(modifyPolicyEntry.getPolicyEntry()),
                    modifyPolicyEntry);
        } else if (signal instanceof ModifyPolicyEntries modifyPolicyEntries) {
            final List<PolicyEntry> entries = java.util.stream.StreamSupport
                    .stream(modifyPolicyEntries.getPolicyEntries().spliterator(), false)
                    .collect(Collectors.toList());
            return validateImportRefsInEntries(entries, modifyPolicyEntries);
        } else {
            return CompletableFuture.completedFuture(signal);
        }
    }

    private CompletionStage<Signal<?>> doApply(final Stream<PolicyImport> policyImportStream,
            final PolicyModifyCommand<?> command) {

        if (LOG.isDebugEnabled()) {
            LOG.withCorrelationId(command)
                    .debug("Applying policy import pre-enforcement on policy <{}>.", command.getEntityId());
        }

        final DittoHeaders dittoHeaders = command.getDittoHeaders();
        return policyImportStream.map(
                        policyImport -> getPolicyEnforcer(policyImport.getImportedPolicyId(), dittoHeaders).thenApply(
                                importedPolicyEnforcer -> authorize(command, importedPolicyEnforcer,
                                        policyImport)))
                .reduce(CompletableFuture.completedFuture(true), (s1, s2) -> s1.thenCombine(s2, (b1, b2) -> b1 && b2))
                .thenApply(ignored -> command);
    }

    private CompletionStage<PolicyEnforcer> getPolicyEnforcer(final PolicyId policyId,
            final DittoHeaders dittoHeaders) {
        return policyEnforcerProvider.getPolicyEnforcer(policyId)
                .thenApply(policyEnforcerOpt -> policyEnforcerOpt.orElseThrow(
                        policyNotAccessible(policyId, dittoHeaders)));
    }

    private static Supplier<PolicyNotAccessibleException> policyNotAccessible(
            final PolicyId policyId, final DittoHeaders dittoHeaders) {
        return () -> PolicyNotAccessibleException.newBuilder(policyId).dittoHeaders(dittoHeaders).build();
    }

    private static Set<ResourceKey> getImportedResourceKeys(final Policy importedPolicy, final PolicyImport policyImport) {
        final Stream<Label> implicitImports = importedPolicy.stream()
                .filter(entry -> ImportableType.IMPLICIT.equals(entry.getImportableType()))
                .map(PolicyEntry::getLabel);

        final Stream<Label> explicitImports =
                policyImport.getEffectedImports().orElse(PoliciesModelFactory.emptyEffectedImportedEntries())
                        .getImportedLabels()
                        .stream();

        return Stream.concat(implicitImports, explicitImports)
                .map(l -> ENTRIES_PREFIX + l)
                .map(path -> ResourceKey.newInstance(POLICY_RESOURCE, path))
                .collect(Collectors.toSet());
    }

    private boolean authorize(final PolicyModifyCommand<?> command, final PolicyEnforcer policyEnforcer,
            final PolicyImport policyImport) {
        final Enforcer enforcer = policyEnforcer.getEnforcer();
        final Policy importedPolicy = policyEnforcer.getPolicy().orElseThrow(policyNotAccessible(command.getEntityId(), command.getDittoHeaders()));
        final Set<ResourceKey> resourceKeys = getImportedResourceKeys(importedPolicy, policyImport);
        final AuthorizationContext authorizationContext = command.getDittoHeaders().getAuthorizationContext();
        // the authorized subject must have READ access on the given entries of the imported policy
        final boolean hasAccess =
                enforcer.hasUnrestrictedPermissions(resourceKeys, authorizationContext, Permission.READ);
        if (LOG.isDebugEnabled()) {
            LOG.withCorrelationId(command)
                    .debug("Enforcement result for command <{}> and policy import {}: {}.", command, policyImport,
                            hasAccess ? "authorized" : "not authorized");
        }
        if (!hasAccess) {
            throw errorForPolicyModifyCommand(policyImport);
        }

        return true;
    }

    /**
     * Validates import references found in the entries of a whole policy (used for CreatePolicy/ModifyPolicy).
     */
    private CompletionStage<Signal<?>> validateImportRefsInEntries(final Policy policy,
            final PolicyModifyCommand<?> command) {
        return validateImportRefsInEntries(
                java.util.stream.StreamSupport.stream(policy.spliterator(), false).collect(Collectors.toList()),
                command);
    }

    /**
     * Validates import references found in the given entries. For each import reference, loads the referenced
     * policy and verifies entry existence, importable type, READ access, and allowedImportAdditions.
     */
    private CompletionStage<Signal<?>> validateImportRefsInEntries(final List<PolicyEntry> entries,
            final PolicyModifyCommand<?> command) {

        final DittoHeaders dittoHeaders = command.getDittoHeaders();

        // Build a list of (entry, importRef) pairs for validation
        final List<CompletableFuture<Boolean>> validations = new java.util.ArrayList<>();
        for (final PolicyEntry entry : entries) {
            for (final EntryReference ref : entry.getReferences()) {
                if (ref.isImportReference()) {
                    validations.add(
                            validateSingleImportReference(entry, ref, dittoHeaders).toCompletableFuture());
                }
            }
        }

        if (validations.isEmpty()) {
            return CompletableFuture.completedFuture(command);
        }

        return CompletableFuture.allOf(validations.toArray(new CompletableFuture[0]))
                .thenApply(ignored -> command);
    }

    /**
     * Validates a single import reference from a referencing entry: loads the referenced policy,
     * verifies entry existence, importable type, READ access, and allowedImportAdditions.
     */
    private CompletionStage<Boolean> validateSingleImportReference(final PolicyEntry referencingEntry,
            final EntryReference ref, final DittoHeaders dittoHeaders) {

        final PolicyId referencedPolicyId = ref.getImportedPolicyId()
                .orElseThrow(java.util.NoSuchElementException::new);
        final Label referencedEntryLabel = ref.getEntryLabel();

        return getPolicyEnforcer(referencedPolicyId, dittoHeaders)
                .thenApply(importedEnforcer -> {
                    final Policy importedPolicy = importedEnforcer.getPolicy()
                            .orElseThrow(policyNotAccessible(referencedPolicyId, dittoHeaders));
                    final Optional<PolicyEntry> entryOpt =
                            importedPolicy.getEntryFor(referencedEntryLabel);
                    if (!entryOpt.isPresent()) {
                        throw PolicyImportInvalidException.newBuilder()
                                .message("A reference targets entry '" +
                                        referencedEntryLabel + "' of policy '" +
                                        referencedPolicyId + "' which does not exist.")
                                .dittoHeaders(dittoHeaders)
                                .build();
                    }
                    final PolicyEntry referencedEntry = entryOpt.get();
                    if (referencedEntry.getImportableType() == ImportableType.NEVER) {
                        throw PolicyImportInvalidException.newBuilder()
                                .message("A reference targets entry '" +
                                        referencedEntryLabel + "' of policy '" +
                                        referencedPolicyId +
                                        "' which is marked as importable='never'.")
                                .dittoHeaders(dittoHeaders)
                                .build();
                    }
                    // Verify READ access on the referenced entry
                    final ResourceKey resourceKey = ResourceKey.newInstance(
                            POLICY_RESOURCE, ENTRIES_PREFIX + referencedEntryLabel);
                    final AuthorizationContext authCtx = dittoHeaders.getAuthorizationContext();
                    if (!importedEnforcer.getEnforcer().hasUnrestrictedPermissions(
                            Set.of(resourceKey), authCtx, Permission.READ)) {
                        throw PolicyNotAccessibleException.newBuilder(referencedPolicyId)
                                .description("Insufficient permissions to reference entry '" +
                                        referencedEntryLabel + "' of policy '" +
                                        referencedPolicyId + "'.")
                                .dittoHeaders(dittoHeaders)
                                .build();
                    }
                    // Validate allowedImportAdditions
                    validateAllowedImportAdditions(referencingEntry, referencedEntry,
                            referencedPolicyId, referencedEntryLabel, dittoHeaders);
                    return true;
                });
    }

    /**
     * Checks that the referencing entry only adds subjects/resources that the referenced template
     * entry permits via {@code allowedImportAdditions}.
     */
    private static void validateAllowedImportAdditions(final PolicyEntry referencingEntry,
            final PolicyEntry referencedEntry, final PolicyId referencedPolicyId,
            final Label referencedEntryLabel, final DittoHeaders dittoHeaders) {

        final Set<AllowedImportAddition> allowed = referencedEntry.getAllowedImportAdditions()
                .orElse(Collections.emptySet());

        if (!referencingEntry.getSubjects().isEmpty() && !allowed.contains(AllowedImportAddition.SUBJECTS)) {
            throw PolicyImportInvalidException.newBuilder()
                    .message("Entry references '" + referencedEntryLabel + "' of policy '" +
                            referencedPolicyId + "' which does not allow subject additions.")
                    .description("The referenced entry's 'allowedImportAdditions' must include " +
                            "'subjects' to permit adding subjects on the referencing entry.")
                    .dittoHeaders(dittoHeaders)
                    .build();
        }

        if (!referencingEntry.getResources().isEmpty() && !allowed.contains(AllowedImportAddition.RESOURCES)) {
            throw PolicyImportInvalidException.newBuilder()
                    .message("Entry references '" + referencedEntryLabel + "' of policy '" +
                            referencedPolicyId + "' which does not allow resource additions.")
                    .description("The referenced entry's 'allowedImportAdditions' must include " +
                            "'resources' to permit adding resources on the referencing entry.")
                    .dittoHeaders(dittoHeaders)
                    .build();
        }
    }

    /**
     * Validates a {@link ModifyPolicyEntryReferences} command by checking each import reference
     * in the list: verifying that the referenced import exists in the importing policy, that the
     * referenced entry in the imported policy permits being referenced (importable != NEVER), and
     * that the caller has READ access on the referenced entry. Local references (within the same
     * policy) are not validated here -- the persistence actor handles entry existence checks.
     */
    private CompletionStage<Signal<?>> validateReferencesModification(
            final ModifyPolicyEntryReferences command) {

        final PolicyId importingPolicyId = command.getEntityId();
        final List<EntryReference> references = command.getReferences();
        final DittoHeaders dittoHeaders = command.getDittoHeaders();

        // Filter to only import references (local references need no pre-enforcement validation)
        final List<EntryReference> importReferences = references.stream()
                .filter(EntryReference::isImportReference)
                .collect(Collectors.toList());

        if (importReferences.isEmpty()) {
            return CompletableFuture.completedFuture(command);
        }

        // Load the importing policy to verify each referenced import exists
        return policyEnforcerProvider.getPolicyEnforcer(importingPolicyId)
                .thenCompose(importingEnforcerOpt -> {
                    final Optional<Policy> importingPolicyOpt = importingEnforcerOpt
                            .flatMap(PolicyEnforcer::getPolicy);

                    // If the importing policy is not in cache, skip the import-exists check.
                    // The persistence actor applies the command against its authoritative state.
                    if (importingPolicyOpt.isPresent()) {
                        final Policy importingPolicy = importingPolicyOpt.get();
                        for (final EntryReference ref : importReferences) {
                            final PolicyId referencedPolicyId = ref.getImportedPolicyId().orElseThrow();
                            final boolean importExists = importingPolicy.getPolicyImports().stream()
                                    .anyMatch(imp -> imp.getImportedPolicyId().equals(referencedPolicyId));
                            if (!importExists) {
                                throw PolicyImportInvalidException.newBuilder()
                                        .message("A reference points to policy '" + referencedPolicyId +
                                                "' which is not in this policy's imports.")
                                        .description("Add an import for policy '" + referencedPolicyId +
                                                "' before referencing its entries.")
                                        .dittoHeaders(dittoHeaders)
                                        .build();
                            }
                        }
                    }

                    // Validate each import reference against its imported policy.
                    // For allowedImportAdditions checks, use the entry from the cached policy.
                    final PolicyEntry referencingEntry = importingPolicyOpt
                            .flatMap(p -> p.getEntryFor(command.getLabel()))
                            .orElse(null);
                    if (referencingEntry != null) {
                        return validateImportRefsForEntry(referencingEntry, importReferences, dittoHeaders)
                                .thenApply(ignored -> command);
                    }
                    // If entry not in cache, skip allowedImportAdditions check (TOCTOU accepted)
                    return validateImportRefsBasic(importReferences, dittoHeaders)
                            .thenApply(ignored -> command);
                });
    }

    /**
     * Validates import references for a specific entry (includes allowedImportAdditions check).
     */
    private CompletionStage<Boolean> validateImportRefsForEntry(final PolicyEntry referencingEntry,
            final List<EntryReference> importReferences, final DittoHeaders dittoHeaders) {

        return importReferences.stream()
                .map(ref -> validateSingleImportReference(referencingEntry, ref, dittoHeaders)
                        .toCompletableFuture())
                .reduce(CompletableFuture.completedFuture(true),
                        (s1, s2) -> s1.thenCombine(s2, (b1, b2) -> b1 && b2));
    }

    /**
     * Basic import reference validation without allowedImportAdditions (used as fallback
     * when the referencing entry is not available from cache).
     */
    private CompletionStage<Boolean> validateImportRefsBasic(final List<EntryReference> importReferences,
            final DittoHeaders dittoHeaders) {

        // Create a dummy entry with empty subjects/resources to skip allowedImportAdditions checks
        final PolicyEntry dummyEntry = PoliciesModelFactory.newPolicyEntry(Label.of("_dummy"),
                PoliciesModelFactory.emptySubjects(), PoliciesModelFactory.emptyResources());
        return validateImportRefsForEntry(dummyEntry, importReferences, dittoHeaders);
    }

    private static DittoRuntimeException errorForPolicyModifyCommand(final PolicyImport policyImport) {
        return PolicyNotAccessibleException.newBuilder(policyImport.getImportedPolicyId())
                .description("Check if the ID of the imported Policy was correct and you " +
                        "have sufficient permissions on all imported policy entries.")
                .build();
    }
}
