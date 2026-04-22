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
package org.eclipse.ditto.policies.model.enforcers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import org.eclipse.ditto.base.model.auth.AuthorizationContext;
import org.eclipse.ditto.base.model.auth.AuthorizationSubject;
import org.eclipse.ditto.base.model.auth.DittoAuthorizationContextType;
import org.eclipse.ditto.policies.model.EffectedPermissions;
import org.eclipse.ditto.policies.model.Label;
import org.eclipse.ditto.policies.model.PoliciesModelFactory;
import org.eclipse.ditto.policies.model.PoliciesResourceType;
import org.eclipse.ditto.policies.model.PolicyEntry;
import org.eclipse.ditto.policies.model.Resource;
import org.eclipse.ditto.policies.model.ResourceKey;
import org.eclipse.ditto.policies.model.Subject;
import org.eclipse.ditto.policies.model.SubjectType;
import org.junit.Test;

/**
 * Unit tests for {@link PolicyEnforcers}, specifically the optimization that filters out entries with
 * empty subjects or empty resources before building the enforcer.
 */
public final class PolicyEnforcersTest {

    private static final AuthorizationSubject ALICE = AuthorizationSubject.newInstance("ditto:alice");
    private static final AuthorizationSubject BOB = AuthorizationSubject.newInstance("ditto:bob");
    private static final AuthorizationSubject CHARLIE = AuthorizationSubject.newInstance("ditto:charlie");

    private static final ResourceKey THING_ROOT = ResourceKey.newInstance("thing", "/");

    private static AuthorizationContext authContextOf(final AuthorizationSubject subject) {
        return AuthorizationContext.newInstance(DittoAuthorizationContextType.UNSPECIFIED, subject);
    }

    @Test
    public void entryWithSubjectsAndResourcesIsIncluded() {
        final PolicyEntry entry = PoliciesModelFactory.newPolicyEntry(Label.of("included"),
                List.of(Subject.newInstance("ditto:alice", SubjectType.GENERATED)),
                List.of(Resource.newInstance(PoliciesResourceType.thingResource("/"),
                        EffectedPermissions.newInstance(Set.of("READ"), Set.of()))));

        final Enforcer enforcer = PolicyEnforcers.defaultEvaluator(List.of(entry));

        assertThat(enforcer.hasUnrestrictedPermissions(THING_ROOT, authContextOf(ALICE), "READ"))
                .isTrue();
    }

    @Test
    public void entryWithSubjectsButNoResourcesIsFiltered() {
        // "subjects-only" has subjects but no resources — should be filtered out
        final PolicyEntry subjectsOnly = PoliciesModelFactory.newPolicyEntry(Label.of("subjects-only"),
                List.of(Subject.newInstance("ditto:bob", SubjectType.GENERATED)),
                List.of());

        // "valid" has both — should remain
        final PolicyEntry valid = PoliciesModelFactory.newPolicyEntry(Label.of("valid"),
                List.of(Subject.newInstance("ditto:alice", SubjectType.GENERATED)),
                List.of(Resource.newInstance(PoliciesResourceType.thingResource("/"),
                        EffectedPermissions.newInstance(Set.of("READ"), Set.of()))));

        final Enforcer enforcer = PolicyEnforcers.defaultEvaluator(List.of(subjectsOnly, valid));

        // Alice (from "valid" entry) should have READ
        assertThat(enforcer.hasUnrestrictedPermissions(THING_ROOT, authContextOf(ALICE), "READ"))
                .isTrue();

        // Bob (from "subjects-only" entry) should NOT have any permissions
        assertThat(enforcer.hasUnrestrictedPermissions(THING_ROOT, authContextOf(BOB), "READ"))
                .isFalse();
    }

    @Test
    public void entryWithResourcesButNoSubjectsIsFiltered() {
        // "resources-only" has resources but no subjects — should be filtered out
        final PolicyEntry resourcesOnly = PoliciesModelFactory.newPolicyEntry(Label.of("resources-only"),
                List.of(),
                List.of(Resource.newInstance(PoliciesResourceType.thingResource("/features/secret"),
                        EffectedPermissions.newInstance(Set.of("READ", "WRITE"), Set.of()))));

        // "valid" has both — should remain
        final PolicyEntry valid = PoliciesModelFactory.newPolicyEntry(Label.of("valid"),
                List.of(Subject.newInstance("ditto:alice", SubjectType.GENERATED)),
                List.of(Resource.newInstance(PoliciesResourceType.thingResource("/"),
                        EffectedPermissions.newInstance(Set.of("READ"), Set.of()))));

        final Enforcer enforcer = PolicyEnforcers.defaultEvaluator(List.of(resourcesOnly, valid));

        // Alice should have READ on thing:/ from "valid"
        assertThat(enforcer.hasUnrestrictedPermissions(THING_ROOT, authContextOf(ALICE), "READ"))
                .isTrue();

        // No subject should have WRITE (the resources-only entry was filtered)
        assertThat(enforcer.getSubjectsWithUnrestrictedPermission(
                ResourceKey.newInstance("thing", "/features/secret"), "WRITE"))
                .isEmpty();
    }

    @Test
    public void entryWithNoSubjectsAndNoResourcesIsFiltered() {
        final PolicyEntry empty = PoliciesModelFactory.newPolicyEntry(Label.of("empty"),
                List.of(), List.of());

        final PolicyEntry valid = PoliciesModelFactory.newPolicyEntry(Label.of("valid"),
                List.of(Subject.newInstance("ditto:alice", SubjectType.GENERATED)),
                List.of(Resource.newInstance(PoliciesResourceType.thingResource("/"),
                        EffectedPermissions.newInstance(Set.of("READ"), Set.of()))));

        final Enforcer enforcer = PolicyEnforcers.defaultEvaluator(List.of(empty, valid));

        assertThat(enforcer.hasUnrestrictedPermissions(THING_ROOT, authContextOf(ALICE), "READ"))
                .isTrue();
    }

    @Test
    public void mixOfValidAndEmptyEntriesFiltersCorrectly() {
        // Entry 1: Alice has READ on thing:/ (valid)
        final PolicyEntry aliceEntry = PoliciesModelFactory.newPolicyEntry(Label.of("alice"),
                List.of(Subject.newInstance("ditto:alice", SubjectType.GENERATED)),
                List.of(Resource.newInstance(PoliciesResourceType.thingResource("/"),
                        EffectedPermissions.newInstance(Set.of("READ"), Set.of()))));

        // Entry 2: subjects but no resources (should be filtered)
        final PolicyEntry bobNoResources = PoliciesModelFactory.newPolicyEntry(Label.of("bob-no-resources"),
                List.of(Subject.newInstance("ditto:bob", SubjectType.GENERATED)),
                List.of());

        // Entry 3: resources but no subjects (should be filtered) — uses message resource type
        //          to avoid overlap with thing resources from other entries
        final PolicyEntry noSubjectsResources = PoliciesModelFactory.newPolicyEntry(Label.of("no-subjects"),
                List.of(),
                List.of(Resource.newInstance(PoliciesResourceType.messageResource("/inbox"),
                        EffectedPermissions.newInstance(Set.of("WRITE"), Set.of()))));

        // Entry 4: Charlie has WRITE on thing:/ (valid)
        final PolicyEntry charlieEntry = PoliciesModelFactory.newPolicyEntry(Label.of("charlie"),
                List.of(Subject.newInstance("ditto:charlie", SubjectType.GENERATED)),
                List.of(Resource.newInstance(PoliciesResourceType.thingResource("/"),
                        EffectedPermissions.newInstance(Set.of("WRITE"), Set.of()))));

        final Enforcer enforcer = PolicyEnforcers.defaultEvaluator(
                List.of(aliceEntry, bobNoResources, noSubjectsResources, charlieEntry));

        // Alice has READ
        assertThat(enforcer.hasUnrestrictedPermissions(THING_ROOT, authContextOf(ALICE), "READ"))
                .isTrue();
        // Alice does NOT have WRITE
        assertThat(enforcer.hasUnrestrictedPermissions(THING_ROOT, authContextOf(ALICE), "WRITE"))
                .isFalse();

        // Bob has nothing (entry was filtered)
        assertThat(enforcer.hasUnrestrictedPermissions(THING_ROOT, authContextOf(BOB), "READ"))
                .isFalse();
        assertThat(enforcer.hasUnrestrictedPermissions(THING_ROOT, authContextOf(BOB), "WRITE"))
                .isFalse();

        // Charlie has WRITE
        assertThat(enforcer.hasUnrestrictedPermissions(THING_ROOT, authContextOf(CHARLIE), "WRITE"))
                .isTrue();
        // Charlie does NOT have READ
        assertThat(enforcer.hasUnrestrictedPermissions(THING_ROOT, authContextOf(CHARLIE), "READ"))
                .isFalse();

        // No subject has WRITE on message:/inbox (the no-subjects entry was filtered)
        assertThat(enforcer.getSubjectsWithUnrestrictedPermission(
                ResourceKey.newInstance("message", "/inbox"), "WRITE"))
                .isEmpty();
    }

    @Test
    public void entryWithOnlyRevokePermissionsIsNotFiltered() {
        // Entry with subjects and resources (revoke only) — must NOT be filtered
        final PolicyEntry grantEntry = PoliciesModelFactory.newPolicyEntry(Label.of("grant"),
                List.of(Subject.newInstance("ditto:alice", SubjectType.GENERATED)),
                List.of(Resource.newInstance(PoliciesResourceType.thingResource("/"),
                        EffectedPermissions.newInstance(Set.of("READ", "WRITE"), Set.of()))));

        final PolicyEntry revokeEntry = PoliciesModelFactory.newPolicyEntry(Label.of("revoke"),
                List.of(Subject.newInstance("ditto:alice", SubjectType.GENERATED)),
                List.of(Resource.newInstance(PoliciesResourceType.thingResource("/features/secret"),
                        EffectedPermissions.newInstance(Set.of(), Set.of("READ", "WRITE")))));

        final Enforcer enforcer = PolicyEnforcers.defaultEvaluator(List.of(grantEntry, revokeEntry));

        // Alice has READ on thing:/
        assertThat(enforcer.hasUnrestrictedPermissions(THING_ROOT, authContextOf(ALICE), "READ"))
                .isFalse(); // revoked below in hierarchy → unrestricted returns false

        // Alice has READ on thing:/attributes (no revoke here)
        assertThat(enforcer.hasUnrestrictedPermissions(
                ResourceKey.newInstance("thing", "/attributes"), authContextOf(ALICE), "READ"))
                .isTrue();

        // Alice does NOT have READ on the revoked path
        assertThat(enforcer.hasUnrestrictedPermissions(
                ResourceKey.newInstance("thing", "/features/secret"), authContextOf(ALICE), "READ"))
                .isFalse();
    }

    @Test
    public void allEntriesFilteredProducesEmptyEnforcer() {
        final PolicyEntry noResources = PoliciesModelFactory.newPolicyEntry(Label.of("no-resources"),
                List.of(Subject.newInstance("ditto:alice", SubjectType.GENERATED)),
                List.of());

        final PolicyEntry noSubjects = PoliciesModelFactory.newPolicyEntry(Label.of("no-subjects"),
                List.of(),
                List.of(Resource.newInstance(PoliciesResourceType.thingResource("/"),
                        EffectedPermissions.newInstance(Set.of("READ"), Set.of()))));

        final Enforcer enforcer = PolicyEnforcers.defaultEvaluator(List.of(noResources, noSubjects));

        // Nobody has any permissions
        assertThat(enforcer.hasUnrestrictedPermissions(THING_ROOT, authContextOf(ALICE), "READ"))
                .isFalse();
        assertThat(enforcer.getSubjectsWithUnrestrictedPermission(THING_ROOT, "READ"))
                .isEmpty();
    }
}
