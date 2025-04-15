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
package org.eclipse.ditto.things.model.devops.commands;

/**
 * JSON fields of WoT validation config commands and responses.
 */
public final class JsonFields {

    /**
     * JSON field containing the thing ID.
     */
    public static final String THING_ID = "thingId";

    /**
     * JSON field containing the validation config.
     */
    public static final String VALIDATION_CONFIG = "validationConfig";

    private JsonFields() {
        throw new AssertionError();
    }
} 