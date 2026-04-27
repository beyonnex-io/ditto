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

import java.util.Optional;

import org.eclipse.ditto.base.model.json.FieldType;
import org.eclipse.ditto.base.model.json.JsonSchemaVersion;
import org.eclipse.ditto.base.model.json.Jsonifiable;
import org.eclipse.ditto.json.JsonFactory;
import org.eclipse.ditto.json.JsonField;
import org.eclipse.ditto.json.JsonFieldDefinition;
import org.eclipse.ditto.json.JsonFieldSelector;
import org.eclipse.ditto.json.JsonObject;

/**
 * A reference from a {@link PolicyEntry} to another entry. The reference can point to either:
 * <ul>
 *   <li>An entry in an imported policy ({@code "import"} + {@code "entry"} fields present) — the referencing entry
 *       inherits subjects, resources, and namespaces from the referenced entry.</li>
 *   <li>A local entry within the same policy ({@code "entry"} field only) — the referencing entry inherits
 *       subjects, resources, and namespaces from the referenced local entry.</li>
 * </ul>
 * <p>
 * For import references, the referenced entry's {@code allowedImportAdditions} acts as a runtime filter on the
 * referencing entry's <em>own</em> subjects/resources additions: at resolution time, own additions that are not
 * permitted by the strictest {@code allowedImportAdditions} across all import references on the entry are silently
 * stripped. There is no write-time rejection — persisted state may contain additions that are not effective at
 * runtime. Local references are not subject to this filter.
 *
 * @since 3.9.0
 */
public interface EntryReference extends Jsonifiable.WithFieldSelectorAndPredicate<JsonField> {

    /**
     * Returns the {@link PolicyId} of the imported policy this reference points to, if this is an import reference.
     * For local references (within the same policy), this returns an empty Optional.
     *
     * @return an Optional containing the imported policy ID, or empty for local references.
     */
    Optional<PolicyId> getImportedPolicyId();

    /**
     * Returns the {@link Label} of the referenced entry.
     *
     * @return the entry label.
     */
    Label getEntryLabel();

    /**
     * Returns whether this reference points to an entry in an imported policy.
     *
     * @return {@code true} if this is an import reference, {@code false} if it is a local reference.
     */
    default boolean isImportReference() {
        return getImportedPolicyId().isPresent();
    }

    /**
     * Returns whether this reference points to a local entry within the same policy.
     *
     * @return {@code true} if this is a local reference, {@code false} if it is an import reference.
     */
    default boolean isLocalReference() {
        return !isImportReference();
    }

    /**
     * EntryReference is only available in JsonSchemaVersion V_2.
     *
     * @return the supported JsonSchemaVersions.
     */
    @Override
    default JsonSchemaVersion[] getSupportedSchemaVersions() {
        return new JsonSchemaVersion[]{JsonSchemaVersion.V_2};
    }

    /**
     * Returns all non-hidden marked fields of this EntryReference.
     *
     * @return a JSON object representation including only non-hidden marked fields.
     */
    @Override
    default JsonObject toJson() {
        return toJson(FieldType.notHidden());
    }

    @Override
    default JsonObject toJson(final JsonSchemaVersion schemaVersion, final JsonFieldSelector fieldSelector) {
        return toJson(schemaVersion, FieldType.regularOrSpecial()).get(fieldSelector);
    }

    /**
     * Known JSON fields of an EntryReference.
     */
    final class JsonFields {

        /**
         * JSON field containing the imported policy ID. Absent for local references.
         */
        public static final JsonFieldDefinition<String> IMPORT =
                JsonFactory.newStringFieldDefinition("import", FieldType.REGULAR, JsonSchemaVersion.V_2);

        /**
         * JSON field containing the entry label within the (imported or local) policy.
         */
        public static final JsonFieldDefinition<String> ENTRY =
                JsonFactory.newStringFieldDefinition("entry", FieldType.REGULAR, JsonSchemaVersion.V_2);

        private JsonFields() {
            throw new AssertionError();
        }
    }

}
