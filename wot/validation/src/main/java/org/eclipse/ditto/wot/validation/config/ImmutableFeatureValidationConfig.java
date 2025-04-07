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
 * Immutable implementation of {@link FeatureValidationConfig}.
 */
@Immutable
public final class ImmutableFeatureValidationConfig implements FeatureValidationConfig {

    private final boolean enforceFeatureDescriptionModification;
    private final boolean enforcePresenceOfModeledFeatures;
    private final boolean enforceProperties;
    private final boolean enforceDesiredProperties;
    private final boolean enforceInboxMessagesInput;
    private final boolean enforceInboxMessagesOutput;
    private final boolean enforceOutboxMessages;
    private final boolean forbidFeatureDescriptionDeletion;
    private final boolean forbidNonModeledFeatures;
    private final boolean forbidNonModeledProperties;
    private final boolean forbidNonModeledDesiredProperties;
    private final boolean forbidNonModeledInboxMessages;
    private final boolean forbidNonModeledOutboxMessages;

    private ImmutableFeatureValidationConfig(final boolean enforceFeatureDescriptionModification,
            final boolean enforcePresenceOfModeledFeatures,
            final boolean enforceProperties,
            final boolean enforceDesiredProperties,
            final boolean enforceInboxMessagesInput,
            final boolean enforceInboxMessagesOutput,
            final boolean enforceOutboxMessages,
            final boolean forbidFeatureDescriptionDeletion,
            final boolean forbidNonModeledFeatures,
            final boolean forbidNonModeledProperties,
            final boolean forbidNonModeledDesiredProperties,
            final boolean forbidNonModeledInboxMessages,
            final boolean forbidNonModeledOutboxMessages) {
        this.enforceFeatureDescriptionModification = enforceFeatureDescriptionModification;
        this.enforcePresenceOfModeledFeatures = enforcePresenceOfModeledFeatures;
        this.enforceProperties = enforceProperties;
        this.enforceDesiredProperties = enforceDesiredProperties;
        this.enforceInboxMessagesInput = enforceInboxMessagesInput;
        this.enforceInboxMessagesOutput = enforceInboxMessagesOutput;
        this.enforceOutboxMessages = enforceOutboxMessages;
        this.forbidFeatureDescriptionDeletion = forbidFeatureDescriptionDeletion;
        this.forbidNonModeledFeatures = forbidNonModeledFeatures;
        this.forbidNonModeledProperties = forbidNonModeledProperties;
        this.forbidNonModeledDesiredProperties = forbidNonModeledDesiredProperties;
        this.forbidNonModeledInboxMessages = forbidNonModeledInboxMessages;
        this.forbidNonModeledOutboxMessages = forbidNonModeledOutboxMessages;
    }

    /**
     * Returns a new instance of {@code ImmutableFeatureValidationConfig}.
     *
     * @param enforceFeatureDescriptionModification whether to enforce/validate a feature whenever its description is modified.
     * @param enforcePresenceOfModeledFeatures whether to enforce that all modeled features are present.
     * @param enforceProperties whether to enforce/validate properties of a feature following the defined WoT properties.
     * @param enforceDesiredProperties whether to enforce/validate desired properties of a feature following the defined WoT properties.
     * @param enforceInboxMessagesInput whether to enforce/validate inbox messages to a feature following the defined WoT action "input".
     * @param enforceInboxMessagesOutput whether to enforce/validate inbox message responses to a feature following the defined WoT action "output".
     * @param enforceOutboxMessages whether to enforce/validate outbox messages from a feature following the defined WoT events.
     * @param forbidFeatureDescriptionDeletion whether to forbid deletion of a feature's description.
     * @param forbidNonModeledFeatures whether to forbid adding features to a Thing which were not defined in its definition's WoT model.
     * @param forbidNonModeledProperties whether to forbid persisting properties which are not defined as properties in the WoT model.
     * @param forbidNonModeledDesiredProperties whether to forbid persisting desired properties which are not defined as properties in the WoT model.
     * @param forbidNonModeledInboxMessages whether to forbid dispatching of inbox messages which are not defined as actions in the WoT model.
     * @param forbidNonModeledOutboxMessages whether to forbid dispatching of outbox messages which are not defined as events in the WoT model.
     * @return the new instance.
     */
    public static ImmutableFeatureValidationConfig of(final boolean enforceFeatureDescriptionModification,
            final boolean enforcePresenceOfModeledFeatures,
            final boolean enforceProperties,
            final boolean enforceDesiredProperties,
            final boolean enforceInboxMessagesInput,
            final boolean enforceInboxMessagesOutput,
            final boolean enforceOutboxMessages,
            final boolean forbidFeatureDescriptionDeletion,
            final boolean forbidNonModeledFeatures,
            final boolean forbidNonModeledProperties,
            final boolean forbidNonModeledDesiredProperties,
            final boolean forbidNonModeledInboxMessages,
            final boolean forbidNonModeledOutboxMessages) {
        return new ImmutableFeatureValidationConfig(enforceFeatureDescriptionModification,
                enforcePresenceOfModeledFeatures,
                enforceProperties,
                enforceDesiredProperties,
                enforceInboxMessagesInput,
                enforceInboxMessagesOutput,
                enforceOutboxMessages,
                forbidFeatureDescriptionDeletion,
                forbidNonModeledFeatures,
                forbidNonModeledProperties,
                forbidNonModeledDesiredProperties,
                forbidNonModeledInboxMessages,
                forbidNonModeledOutboxMessages);
    }

    @Override
    public boolean isEnforceFeatureDescriptionModification() {
        return enforceFeatureDescriptionModification;
    }

    @Override
    public boolean isEnforcePresenceOfModeledFeatures() {
        return enforcePresenceOfModeledFeatures;
    }

    @Override
    public boolean isEnforceProperties() {
        return enforceProperties;
    }

    @Override
    public boolean isEnforceDesiredProperties() {
        return enforceDesiredProperties;
    }

    @Override
    public boolean isEnforceInboxMessagesInput() {
        return enforceInboxMessagesInput;
    }

    @Override
    public boolean isEnforceInboxMessagesOutput() {
        return enforceInboxMessagesOutput;
    }

    @Override
    public boolean isEnforceOutboxMessages() {
        return enforceOutboxMessages;
    }

    @Override
    public boolean isForbidFeatureDescriptionDeletion() {
        return forbidFeatureDescriptionDeletion;
    }

    @Override
    public boolean isForbidNonModeledFeatures() {
        return forbidNonModeledFeatures;
    }

    @Override
    public boolean isForbidNonModeledProperties() {
        return forbidNonModeledProperties;
    }

    @Override
    public boolean isForbidNonModeledDesiredProperties() {
        return forbidNonModeledDesiredProperties;
    }

    @Override
    public boolean isForbidNonModeledInboxMessages() {
        return forbidNonModeledInboxMessages;
    }

    @Override
    public boolean isForbidNonModeledOutboxMessages() {
        return forbidNonModeledOutboxMessages;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ImmutableFeatureValidationConfig)) return false;
        final ImmutableFeatureValidationConfig that = (ImmutableFeatureValidationConfig) o;
        return enforceFeatureDescriptionModification == that.enforceFeatureDescriptionModification
                && enforcePresenceOfModeledFeatures == that.enforcePresenceOfModeledFeatures
                && enforceProperties == that.enforceProperties
                && enforceDesiredProperties == that.enforceDesiredProperties
                && enforceInboxMessagesInput == that.enforceInboxMessagesInput
                && enforceInboxMessagesOutput == that.enforceInboxMessagesOutput
                && enforceOutboxMessages == that.enforceOutboxMessages
                && forbidFeatureDescriptionDeletion == that.forbidFeatureDescriptionDeletion
                && forbidNonModeledFeatures == that.forbidNonModeledFeatures
                && forbidNonModeledProperties == that.forbidNonModeledProperties
                && forbidNonModeledDesiredProperties == that.forbidNonModeledDesiredProperties
                && forbidNonModeledInboxMessages == that.forbidNonModeledInboxMessages
                && forbidNonModeledOutboxMessages == that.forbidNonModeledOutboxMessages;
    }
} 