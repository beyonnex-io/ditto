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

package org.eclipse.ditto.things.model.devops;

import org.eclipse.ditto.base.model.json.Jsonifiable;
import org.eclipse.ditto.json.*;

import javax.annotation.concurrent.Immutable;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents the override flags for enforcing Thing-level WoT validation.
 */
@Immutable
public final class ImmutableThingEnforceOverrides implements Jsonifiable<JsonObject> {

    private static final String TD_MODIFICATION_FIELD = "thing-description-modification";
    private static final String ATTRIBUTES_FIELD = "attributes";
    private static final String INBOX_MESSAGES_INPUT_FIELD = "inbox-messages-input";
    private static final String INBOX_MESSAGES_OUTPUT_FIELD = "inbox-messages-output";
    private static final String OUTBOX_MESSAGES_FIELD = "outbox-messages";

    private static final JsonFieldDefinition<Boolean> TD_MODIFICATION_POINTER =
            JsonFactory.newBooleanFieldDefinition(TD_MODIFICATION_FIELD);
    private static final JsonFieldDefinition<Boolean> ATTRIBUTES_POINTER =
            JsonFactory.newBooleanFieldDefinition(ATTRIBUTES_FIELD);
    private static final JsonFieldDefinition<Boolean> INBOX_INPUT_POINTER =
            JsonFactory.newBooleanFieldDefinition(INBOX_MESSAGES_INPUT_FIELD);
    private static final JsonFieldDefinition<Boolean> INBOX_OUTPUT_POINTER =
            JsonFactory.newBooleanFieldDefinition(INBOX_MESSAGES_OUTPUT_FIELD);
    private static final JsonFieldDefinition<Boolean> OUTBOX_POINTER =
            JsonFactory.newBooleanFieldDefinition(OUTBOX_MESSAGES_FIELD);

    private final Boolean thingDescriptionModification;
    private final Boolean attributes;
    private final Boolean inboxMessagesInput;
    private final Boolean inboxMessagesOutput;
    private final Boolean outboxMessages;

    private ImmutableThingEnforceOverrides(
            final Boolean thingDescriptionModification,
            final Boolean attributes,
            final Boolean inboxMessagesInput,
            final Boolean inboxMessagesOutput,
            final Boolean outboxMessages) {

        this.thingDescriptionModification = thingDescriptionModification;
        this.attributes = attributes;
        this.inboxMessagesInput = inboxMessagesInput;
        this.inboxMessagesOutput = inboxMessagesOutput;
        this.outboxMessages = outboxMessages;
    }

    /**
     * Creates a new instance.
     */
    public static ImmutableThingEnforceOverrides of(
            final Boolean thingDescriptionModification,
            final Boolean attributes,
            final Boolean inboxMessagesInput,
            final Boolean inboxMessagesOutput,
            final Boolean outboxMessages) {
        return new ImmutableThingEnforceOverrides(
                thingDescriptionModification,
                attributes,
                inboxMessagesInput,
                inboxMessagesOutput,
                outboxMessages);
    }

    /** @return whether TD modification is enforced. */
    public Optional<Boolean> getThingDescriptionModification() {
        return Optional.ofNullable(thingDescriptionModification);
    }

    /** @return whether Thing attributes are enforced. */
    public Optional<Boolean> getAttributes() {
        return Optional.ofNullable(attributes);
    }

    /** @return whether inbox "input" messages are enforced. */
    public Optional<Boolean> getInboxMessagesInput() {
        return Optional.ofNullable(inboxMessagesInput);
    }

    /** @return whether inbox "output" messages are enforced. */
    public Optional<Boolean> getInboxMessagesOutput() {
        return Optional.ofNullable(inboxMessagesOutput);
    }

    /** @return whether outbox messages are enforced. */
    public Optional<Boolean> getOutboxMessages() {
        return Optional.ofNullable(outboxMessages);
    }

    @Override
    public JsonObject toJson() {
        final JsonObjectBuilder builder = JsonObject.newBuilder();

        if (thingDescriptionModification != null) {
            builder.set(TD_MODIFICATION_FIELD, JsonFactory.newValue(thingDescriptionModification));
        }
        if (attributes != null) {
            builder.set(ATTRIBUTES_FIELD, JsonFactory.newValue(attributes));
        }
        if (inboxMessagesInput != null) {
            builder.set(INBOX_MESSAGES_INPUT_FIELD, JsonFactory.newValue(inboxMessagesInput));
        }
        if (inboxMessagesOutput != null) {
            builder.set(INBOX_MESSAGES_OUTPUT_FIELD, JsonFactory.newValue(inboxMessagesOutput));
        }
        if (outboxMessages != null) {
            builder.set(OUTBOX_MESSAGES_FIELD, JsonFactory.newValue(outboxMessages));
        }

        return builder.build();
    }

    /**
     * Creates an {@code ImmutableThingEnforceOverrides} from a {@link JsonObject}.
     *
     * @param json the JSON to parse.
     * @return the parsed override instance.
     */
    public static ImmutableThingEnforceOverrides fromJson(final JsonObject json) {
        final Boolean tdModification = json.getValue(TD_MODIFICATION_POINTER).orElse(null);
        final Boolean attributes = json.getValue(ATTRIBUTES_POINTER).orElse(null);
        final Boolean inboxInput = json.getValue(INBOX_INPUT_POINTER).orElse(null);
        final Boolean inboxOutput = json.getValue(INBOX_OUTPUT_POINTER).orElse(null);
        final Boolean outbox = json.getValue(OUTBOX_POINTER).orElse(null);

        return of(tdModification, attributes, inboxInput, inboxOutput, outbox);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ImmutableThingEnforceOverrides)) return false;
        final ImmutableThingEnforceOverrides that = (ImmutableThingEnforceOverrides) o;
        return Objects.equals(thingDescriptionModification, that.thingDescriptionModification)
                && Objects.equals(attributes, that.attributes)
                && Objects.equals(inboxMessagesInput, that.inboxMessagesInput)
                && Objects.equals(inboxMessagesOutput, that.inboxMessagesOutput)
                && Objects.equals(outboxMessages, that.outboxMessages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                thingDescriptionModification,
                attributes,
                inboxMessagesInput,
                inboxMessagesOutput,
                outboxMessages);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" +
                "thingDescriptionModification=" + thingDescriptionModification +
                ", attributes=" + attributes +
                ", inboxMessagesInput=" + inboxMessagesInput +
                ", inboxMessagesOutput=" + inboxMessagesOutput +
                ", outboxMessages=" + outboxMessages +
                "]";
    }
}
