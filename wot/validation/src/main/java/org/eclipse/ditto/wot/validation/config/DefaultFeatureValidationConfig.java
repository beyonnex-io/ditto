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

/**
 * Default implementation of {@link FeatureValidationConfig}.
 */
@Immutable
public final class DefaultFeatureValidationConfig implements FeatureValidationConfig {

    private static final DefaultFeatureValidationConfig INSTANCE = new DefaultFeatureValidationConfig();

    private DefaultFeatureValidationConfig() {
        // No-op
    }

    /**
     * Returns an instance of DefaultFeatureValidationConfig.
     *
     * @return the instance.
     */
    public static DefaultFeatureValidationConfig of() {
        return INSTANCE;
    }

    @Override
    public boolean isForbidNonModeledProperties() {
        return false;
    }

    @Override
    public boolean isEnforceProperties() {
        return false;
    }

    @Override
    public boolean isForbidFeatureDescriptionDeletion() {
        return false;
    }

    @Override
    public boolean isEnforceOutboxMessages() {
        return false;
    }

    @Override
    public boolean isEnforceFeatureDescriptionModification() {
        return false;
    }

    @Override
    public boolean isForbidNonModeledFeatures() {
        return false;
    }

    @Override
    public boolean isEnforcePresenceOfModeledFeatures() {
        return false;
    }

    @Override
    public boolean isEnforceInboxMessagesOutput() {
        return false;
    }

    @Override
    public boolean isForbidNonModeledDesiredProperties() {
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
    public boolean isEnforceDesiredProperties() {
        return false;
    }

    @Override
    public boolean isForbidNonModeledOutboxMessages() {
        return false;
    }
} 