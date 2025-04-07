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
package org.eclipse.ditto.things.model.devops.events;

import org.eclipse.ditto.base.model.json.FieldType;
import org.eclipse.ditto.base.model.json.JsonSchemaVersion;
import org.eclipse.ditto.json.JsonFieldDefinition;
import org.eclipse.ditto.json.JsonFactory;
import org.eclipse.ditto.json.JsonValue;

/**
 * JSON fields for WoT validation config events.
 */
public final class JsonFields {

    /**
     * JSON field containing the WoT validation config.
     */
    public static final JsonFieldDefinition<JsonValue> CONFIG = JsonFactory.newJsonValueFieldDefinition(
            "config", FieldType.REGULAR, JsonSchemaVersion.V_2);

    /**
     * JSON field containing the scope ID.
     */
    public static final JsonFieldDefinition<String> SCOPE_ID = JsonFactory.newStringFieldDefinition(
            "scopeId", FieldType.REGULAR, JsonSchemaVersion.V_2);

    private JsonFields() {
        throw new AssertionError();
    }
} 