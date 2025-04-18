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
package org.eclipse.ditto.wot.validation.config;

import org.eclipse.ditto.base.model.json.FieldType;
import org.eclipse.ditto.base.model.json.JsonSchemaVersion;
import org.eclipse.ditto.base.model.json.Jsonifiable;
import org.eclipse.ditto.json.JsonField;
import org.eclipse.ditto.json.JsonObject;

import java.util.function.Predicate;

public class TmValidationConfig implements Jsonifiable.WithFieldSelectorAndPredicate<JsonField> {

    @Override
    public JsonObject toJson() {
        return toJson(FieldType.notHidden());
    }

    @Override
    public JsonObject toJson(final JsonSchemaVersion schemaVersion, final Predicate<JsonField> predicate) {
        // TODO: Implement proper JSON serialization
        return JsonObject.empty();
    }
} 