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
package org.eclipse.ditto.policies.model;

import static org.eclipse.ditto.base.model.common.ConditionChecker.checkNotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.base.model.json.JsonSchemaVersion;
import org.eclipse.ditto.json.JsonFactory;
import org.eclipse.ditto.json.JsonField;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonObjectBuilder;

/**
 * An immutable implementation of {@link EntryReference}.
 */
@Immutable
final class ImmutableEntryReference implements EntryReference {

    @Nullable private final PolicyId importedPolicyId;
    private final Label entryLabel;

    private ImmutableEntryReference(@Nullable final PolicyId importedPolicyId, final Label entryLabel) {
        this.importedPolicyId = importedPolicyId;
        this.entryLabel = checkNotNull(entryLabel, "entryLabel");
    }

    /**
     * Returns a new import {@code EntryReference} pointing to an entry in an imported policy.
     *
     * @param importedPolicyId the ID of the imported policy.
     * @param entryLabel the label of the entry within the imported policy.
     * @return the new EntryReference.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static EntryReference of(final PolicyId importedPolicyId, final Label entryLabel) {
        checkNotNull(importedPolicyId, "importedPolicyId");
        return new ImmutableEntryReference(importedPolicyId, entryLabel);
    }

    /**
     * Returns a new local {@code EntryReference} pointing to an entry within the same policy.
     *
     * @param entryLabel the label of the local entry.
     * @return the new EntryReference.
     * @throws NullPointerException if {@code entryLabel} is {@code null}.
     */
    public static EntryReference ofLocal(final Label entryLabel) {
        return new ImmutableEntryReference(null, entryLabel);
    }

    /**
     * Creates a new {@code EntryReference} from the specified JSON object.
     * If the JSON contains an {@code "import"} field, an import reference is created.
     * Otherwise, a local reference is created.
     *
     * @param jsonObject the JSON object providing the data.
     * @return a new EntryReference.
     * @throws NullPointerException if {@code jsonObject} is {@code null}.
     */
    public static EntryReference fromJson(final JsonObject jsonObject) {
        checkNotNull(jsonObject, "JSON object");
        final Label entryLabel = Label.of(jsonObject.getValueOrThrow(EntryReference.JsonFields.ENTRY));
        final Optional<String> importOpt = jsonObject.getValue(EntryReference.JsonFields.IMPORT);
        if (importOpt.isPresent()) {
            return of(PolicyId.of(importOpt.get()), entryLabel);
        } else {
            return ofLocal(entryLabel);
        }
    }

    @Override
    public Optional<PolicyId> getImportedPolicyId() {
        return Optional.ofNullable(importedPolicyId);
    }

    @Override
    public Label getEntryLabel() {
        return entryLabel;
    }

    @Override
    public JsonObject toJson(final JsonSchemaVersion schemaVersion, final Predicate<JsonField> thePredicate) {
        final Predicate<JsonField> predicate = schemaVersion.and(thePredicate);
        final JsonObjectBuilder builder = JsonFactory.newObjectBuilder();
        if (importedPolicyId != null) {
            builder.set(EntryReference.JsonFields.IMPORT, importedPolicyId.toString(), predicate);
        }
        builder.set(EntryReference.JsonFields.ENTRY, entryLabel.toString(), predicate);
        return builder.build();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ImmutableEntryReference that = (ImmutableEntryReference) o;
        return Objects.equals(importedPolicyId, that.importedPolicyId) &&
                Objects.equals(entryLabel, that.entryLabel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(importedPolicyId, entryLabel);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" +
                "importedPolicyId=" + importedPolicyId +
                ", entryLabel=" + entryLabel +
                "]";
    }

}
