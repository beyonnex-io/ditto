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
 * Immutable implementation of {@link ThingValidationConfig}.
 */
@Immutable
public final class ImmutableThingValidationConfig implements ThingValidationConfig {

    private final boolean enforceThingDescriptionModification;
    private final boolean enforceAttributes;
    private final boolean enforceInboxMessagesInput;
    private final boolean enforceInboxMessagesOutput;
    private final boolean enforceOutboxMessages;
    private final boolean forbidThingDescriptionDeletion;
    private final boolean forbidNonModeledAttributes;
    private final boolean forbidNonModeledInboxMessages;
    private final boolean forbidNonModeledOutboxMessages;

    private ImmutableThingValidationConfig(final boolean enforceThingDescriptionModification,
            final boolean enforceAttributes,
            final boolean enforceInboxMessagesInput,
            final boolean enforceInboxMessagesOutput,
            final boolean enforceOutboxMessages,
            final boolean forbidThingDescriptionDeletion,
            final boolean forbidNonModeledAttributes,
            final boolean forbidNonModeledInboxMessages,
            final boolean forbidNonModeledOutboxMessages) {
        this.enforceThingDescriptionModification = enforceThingDescriptionModification;
        this.enforceAttributes = enforceAttributes;
        this.enforceInboxMessagesInput = enforceInboxMessagesInput;
        this.enforceInboxMessagesOutput = enforceInboxMessagesOutput;
        this.enforceOutboxMessages = enforceOutboxMessages;
        this.forbidThingDescriptionDeletion = forbidThingDescriptionDeletion;
        this.forbidNonModeledAttributes = forbidNonModeledAttributes;
        this.forbidNonModeledInboxMessages = forbidNonModeledInboxMessages;
        this.forbidNonModeledOutboxMessages = forbidNonModeledOutboxMessages;
    }

    /**
     * Returns a new instance of {@code ImmutableThingValidationConfig}.
     *
     * @param enforceThingDescriptionModification whether to enforce/validate a thing whenever its description is modified.
     * @param enforceAttributes whether to enforce/validate attributes of a thing following the defined WoT properties.
     * @param enforceInboxMessagesInput whether to enforce/validate inbox messages to a thing following the defined WoT action "input".
     * @param enforceInboxMessagesOutput whether to enforce/validate inbox message responses to a thing following the defined WoT action "output".
     * @param enforceOutboxMessages whether to enforce/validate outbox messages from a thing following the defined WoT event "data".
     * @param forbidThingDescriptionDeletion whether to forbid deletion of a thing's description.
     * @param forbidNonModeledAttributes whether to forbid persisting attributes which are not defined as properties in the WoT model.
     * @param forbidNonModeledInboxMessages whether to forbid dispatching of inbox messages which are not defined as actions in the WoT model.
     * @param forbidNonModeledOutboxMessages whether to forbid dispatching of outbox messages which are not defined as events in the WoT model.
     * @return the new instance.
     */
    public static ImmutableThingValidationConfig of(final boolean enforceThingDescriptionModification,
            final boolean enforceAttributes,
            final boolean enforceInboxMessagesInput,
            final boolean enforceInboxMessagesOutput,
            final boolean enforceOutboxMessages,
            final boolean forbidThingDescriptionDeletion,
            final boolean forbidNonModeledAttributes,
            final boolean forbidNonModeledInboxMessages,
            final boolean forbidNonModeledOutboxMessages) {
        return new ImmutableThingValidationConfig(enforceThingDescriptionModification,
                enforceAttributes,
                enforceInboxMessagesInput,
                enforceInboxMessagesOutput,
                enforceOutboxMessages,
                forbidThingDescriptionDeletion,
                forbidNonModeledAttributes,
                forbidNonModeledInboxMessages,
                forbidNonModeledOutboxMessages);
    }

    @Override
    public boolean isEnforceThingDescriptionModification() {
        return enforceThingDescriptionModification;
    }

    @Override
    public boolean isEnforceAttributes() {
        return enforceAttributes;
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
    public boolean isForbidThingDescriptionDeletion() {
        return forbidThingDescriptionDeletion;
    }

    @Override
    public boolean isForbidNonModeledAttributes() {
        return forbidNonModeledAttributes;
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
    public JsonObject toJson() {
        return JsonObject.newBuilder()
                .set(ThingValidationConfig.JsonFields.ENFORCE_THING_DESCRIPTION_MODIFICATION, enforceThingDescriptionModification)
                .set(ThingValidationConfig.JsonFields.ENFORCE_ATTRIBUTES, enforceAttributes)
                .set(ThingValidationConfig.JsonFields.ENFORCE_INBOX_MESSAGES_INPUT, enforceInboxMessagesInput)
                .set(ThingValidationConfig.JsonFields.ENFORCE_INBOX_MESSAGES_OUTPUT, enforceInboxMessagesOutput)
                .set(ThingValidationConfig.JsonFields.ENFORCE_OUTBOX_MESSAGES, enforceOutboxMessages)
                .set(ThingValidationConfig.JsonFields.FORBID_THING_DESCRIPTION_DELETION, forbidThingDescriptionDeletion)
                .set(ThingValidationConfig.JsonFields.FORBID_NON_MODELED_ATTRIBUTES, forbidNonModeledAttributes)
                .set(ThingValidationConfig.JsonFields.FORBID_NON_MODELED_INBOX_MESSAGES, forbidNonModeledInboxMessages)
                .set(ThingValidationConfig.JsonFields.FORBID_NON_MODELED_OUTBOX_MESSAGES, forbidNonModeledOutboxMessages)
                .build();
    }
} 