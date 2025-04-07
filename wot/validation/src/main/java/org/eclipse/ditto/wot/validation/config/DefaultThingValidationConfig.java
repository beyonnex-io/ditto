/*
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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

import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.json.JsonObject;

/**
 * Default implementation of {@link ThingValidationConfig}.
 */
@Immutable
public final class DefaultThingValidationConfig implements ThingValidationConfig {

    private static final DefaultThingValidationConfig INSTANCE = new DefaultThingValidationConfig();

    private DefaultThingValidationConfig() {
        // No-op
    }

    /**
     * Returns an instance of DefaultThingValidationConfig.
     *
     * @return the instance.
     */
    public static DefaultThingValidationConfig of() {
        return INSTANCE;
    }

    @Override
    public boolean isForbidThingDescriptionDeletion() {
        return false;
    }

    @Override
    public boolean isForbidNonModeledAttributes() {
        return false;
    }

    @Override
    public boolean isEnforceOutboxMessages() {
        return false;
    }

    @Override
    public boolean isEnforceInboxMessagesOutput() {
        return false;
    }

    @Override
    public boolean isEnforceAttributes() {
        return false;
    }

    @Override
    public boolean isEnforceThingDescriptionModification() {
        return false;
    }

    @Override
    public boolean isEnforceInboxMessagesInput() {
        return false;
    }

    @Override
    public boolean isForbidNonModeledInboxMessages() {
        return false;
    }

    @Override
    public boolean isForbidNonModeledOutboxMessages() {
        return false;
    }

    @Override
    public JsonObject toJson() {
        return JsonObject.empty();
    }
} 