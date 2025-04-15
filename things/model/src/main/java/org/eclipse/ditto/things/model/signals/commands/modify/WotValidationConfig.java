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
package org.eclipse.ditto.things.model.signals.commands.modify;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.json.JsonFactory;
import org.eclipse.ditto.json.JsonField;
import org.eclipse.ditto.json.JsonFieldDefinition;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonObjectBuilder;
import org.eclipse.ditto.json.JsonPointer;
import org.eclipse.ditto.json.JsonValue;

/**
 * Configuration for Thing-specific WoT validation.
 */
@Immutable
public final class WotValidationConfig {

    private static final JsonFieldDefinition<Boolean> ENFORCE_THING_DESCRIPTION_MODIFICATION =
            JsonFactory.newBooleanFieldDefinition("enforceThingDescriptionModification");

    private static final JsonFieldDefinition<Boolean> ENFORCE_ATTRIBUTES =
            JsonFactory.newBooleanFieldDefinition("enforceAttributes");

    private static final JsonFieldDefinition<Boolean> ENFORCE_INBOX_MESSAGES_INPUT =
            JsonFactory.newBooleanFieldDefinition("enforceInboxMessagesInput");

    private static final JsonFieldDefinition<Boolean> ENFORCE_INBOX_MESSAGES_OUTPUT =
            JsonFactory.newBooleanFieldDefinition("enforceInboxMessagesOutput");

    private static final JsonFieldDefinition<Boolean> ENFORCE_OUTBOX_MESSAGES =
            JsonFactory.newBooleanFieldDefinition("enforceOutboxMessages");

    private static final JsonFieldDefinition<Boolean> FORBID_THING_DESCRIPTION_DELETION =
            JsonFactory.newBooleanFieldDefinition("forbidThingDescriptionDeletion");

    private static final JsonFieldDefinition<Boolean> FORBID_NON_MODELED_ATTRIBUTES =
            JsonFactory.newBooleanFieldDefinition("forbidNonModeledAttributes");

    private static final JsonFieldDefinition<Boolean> FORBID_NON_MODELED_INBOX_MESSAGES =
            JsonFactory.newBooleanFieldDefinition("forbidNonModeledInboxMessages");

    private static final JsonFieldDefinition<Boolean> FORBID_NON_MODELED_OUTBOX_MESSAGES =
            JsonFactory.newBooleanFieldDefinition("forbidNonModeledOutboxMessages");

    private final boolean enforceThingDescriptionModification;
    private final boolean enforceAttributes;
    private final boolean enforceInboxMessagesInput;
    private final boolean enforceInboxMessagesOutput;
    private final boolean enforceOutboxMessages;
    private final boolean forbidThingDescriptionDeletion;
    private final boolean forbidNonModeledAttributes;
    private final boolean forbidNonModeledInboxMessages;
    private final boolean forbidNonModeledOutboxMessages;

    private WotValidationConfig(final boolean enforceThingDescriptionModification,
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
     * Creates a new instance of {@code WotValidationConfig}.
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
    public static WotValidationConfig of(final boolean enforceThingDescriptionModification,
            final boolean enforceAttributes,
            final boolean enforceInboxMessagesInput,
            final boolean enforceInboxMessagesOutput,
            final boolean enforceOutboxMessages,
            final boolean forbidThingDescriptionDeletion,
            final boolean forbidNonModeledAttributes,
            final boolean forbidNonModeledInboxMessages,
            final boolean forbidNonModeledOutboxMessages) {
        return new WotValidationConfig(enforceThingDescriptionModification,
                enforceAttributes,
                enforceInboxMessagesInput,
                enforceInboxMessagesOutput,
                enforceOutboxMessages,
                forbidThingDescriptionDeletion,
                forbidNonModeledAttributes,
                forbidNonModeledInboxMessages,
                forbidNonModeledOutboxMessages);
    }

    /**
     * Creates a new instance of {@code WotValidationConfig} from a JSON object.
     *
     * @param jsonObject the JSON object.
     * @return the new instance.
     * @throws org.eclipse.ditto.json.JsonParseException if the passed in {@code jsonObject} was not in the expected format.
     */
    public static WotValidationConfig fromJson(final JsonObject jsonObject) {
        return of(
                jsonObject.getValueOrThrow(ENFORCE_THING_DESCRIPTION_MODIFICATION),
                jsonObject.getValueOrThrow(ENFORCE_ATTRIBUTES),
                jsonObject.getValueOrThrow(ENFORCE_INBOX_MESSAGES_INPUT),
                jsonObject.getValueOrThrow(ENFORCE_INBOX_MESSAGES_OUTPUT),
                jsonObject.getValueOrThrow(ENFORCE_OUTBOX_MESSAGES),
                jsonObject.getValueOrThrow(FORBID_THING_DESCRIPTION_DELETION),
                jsonObject.getValueOrThrow(FORBID_NON_MODELED_ATTRIBUTES),
                jsonObject.getValueOrThrow(FORBID_NON_MODELED_INBOX_MESSAGES),
                jsonObject.getValueOrThrow(FORBID_NON_MODELED_OUTBOX_MESSAGES));
    }

    /**
     * Returns whether to enforce/validate a thing whenever its description is modified.
     *
     * @return whether to enforce/validate a thing whenever its description is modified.
     */
    public boolean isEnforceThingDescriptionModification() {
        return enforceThingDescriptionModification;
    }

    /**
     * Returns whether to enforce/validate attributes of a thing following the defined WoT properties.
     *
     * @return whether to enforce/validate attributes of a thing following the defined WoT properties.
     */
    public boolean isEnforceAttributes() {
        return enforceAttributes;
    }

    /**
     * Returns whether to enforce/validate inbox messages to a thing following the defined WoT action "input".
     *
     * @return whether to enforce/validate inbox messages to a thing following the defined WoT action "input".
     */
    public boolean isEnforceInboxMessagesInput() {
        return enforceInboxMessagesInput;
    }

    /**
     * Returns whether to enforce/validate inbox message responses to a thing following the defined WoT action "output".
     *
     * @return whether to enforce/validate inbox message responses to a thing following the defined WoT action "output".
     */
    public boolean isEnforceInboxMessagesOutput() {
        return enforceInboxMessagesOutput;
    }

    /**
     * Returns whether to enforce/validate outbox messages from a thing following the defined WoT event "data".
     *
     * @return whether to enforce/validate outbox messages from a thing following the defined WoT event "data".
     */
    public boolean isEnforceOutboxMessages() {
        return enforceOutboxMessages;
    }

    /**
     * Returns whether to forbid deletion of a thing's description.
     *
     * @return whether to forbid deletion of a thing's description.
     */
    public boolean isForbidThingDescriptionDeletion() {
        return forbidThingDescriptionDeletion;
    }

    /**
     * Returns whether to forbid persisting attributes which are not defined as properties in the WoT model.
     *
     * @return whether to forbid persisting attributes which are not defined as properties in the WoT model.
     */
    public boolean isForbidNonModeledAttributes() {
        return forbidNonModeledAttributes;
    }

    /**
     * Returns whether to forbid dispatching of inbox messages which are not defined as actions in the WoT model.
     *
     * @return whether to forbid dispatching of inbox messages which are not defined as actions in the WoT model.
     */
    public boolean isForbidNonModeledInboxMessages() {
        return forbidNonModeledInboxMessages;
    }

    /**
     * Returns whether to forbid dispatching of outbox messages which are not defined as events in the WoT model.
     *
     * @return whether to forbid dispatching of outbox messages which are not defined as events in the WoT model.
     */
    public boolean isForbidNonModeledOutboxMessages() {
        return forbidNonModeledOutboxMessages;
    }

    public JsonObject toJson() {
        return JsonFactory.newObjectBuilder()
                .set(ENFORCE_THING_DESCRIPTION_MODIFICATION, enforceThingDescriptionModification)
                .set(ENFORCE_ATTRIBUTES, enforceAttributes)
                .set(ENFORCE_INBOX_MESSAGES_INPUT, enforceInboxMessagesInput)
                .set(ENFORCE_INBOX_MESSAGES_OUTPUT, enforceInboxMessagesOutput)
                .set(ENFORCE_OUTBOX_MESSAGES, enforceOutboxMessages)
                .set(FORBID_THING_DESCRIPTION_DELETION, forbidThingDescriptionDeletion)
                .set(FORBID_NON_MODELED_ATTRIBUTES, forbidNonModeledAttributes)
                .set(FORBID_NON_MODELED_INBOX_MESSAGES, forbidNonModeledInboxMessages)
                .set(FORBID_NON_MODELED_OUTBOX_MESSAGES, forbidNonModeledOutboxMessages)
                .build();
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final WotValidationConfig that = (WotValidationConfig) o;
        return enforceThingDescriptionModification == that.enforceThingDescriptionModification &&
                enforceAttributes == that.enforceAttributes &&
                enforceInboxMessagesInput == that.enforceInboxMessagesInput &&
                enforceInboxMessagesOutput == that.enforceInboxMessagesOutput &&
                enforceOutboxMessages == that.enforceOutboxMessages &&
                forbidThingDescriptionDeletion == that.forbidThingDescriptionDeletion &&
                forbidNonModeledAttributes == that.forbidNonModeledAttributes &&
                forbidNonModeledInboxMessages == that.forbidNonModeledInboxMessages &&
                forbidNonModeledOutboxMessages == that.forbidNonModeledOutboxMessages;
    }

    @Override
    public int hashCode() {
        return Objects.hash(enforceThingDescriptionModification, enforceAttributes, enforceInboxMessagesInput,
                enforceInboxMessagesOutput, enforceOutboxMessages, forbidThingDescriptionDeletion,
                forbidNonModeledAttributes, forbidNonModeledInboxMessages, forbidNonModeledOutboxMessages);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" +
                "enforceThingDescriptionModification=" + enforceThingDescriptionModification +
                ", enforceAttributes=" + enforceAttributes +
                ", enforceInboxMessagesInput=" + enforceInboxMessagesInput +
                ", enforceInboxMessagesOutput=" + enforceInboxMessagesOutput +
                ", enforceOutboxMessages=" + enforceOutboxMessages +
                ", forbidThingDescriptionDeletion=" + forbidThingDescriptionDeletion +
                ", forbidNonModeledAttributes=" + forbidNonModeledAttributes +
                ", forbidNonModeledInboxMessages=" + forbidNonModeledInboxMessages +
                ", forbidNonModeledOutboxMessages=" + forbidNonModeledOutboxMessages +
                "]";
    }
} 