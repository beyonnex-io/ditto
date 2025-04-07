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
 * Represents the override flags for forbidding Thing-level WoT validation.
 */
@Immutable
public final class ImmutableThingForbidOverrides implements Jsonifiable<JsonObject> {

    private static final String TD_DELETION_FIELD = "thing-description-deletion";
    private static final String NON_MODELED_ATTRIBUTES_FIELD = "non-modeled-attributes";
    private static final String NON_MODELED_INBOX_FIELD = "non-modeled-inbox-messages";
    private static final String NON_MODELED_OUTBOX_FIELD = "non-modeled-outbox-messages";

    private static final JsonFieldDefinition<Boolean> TD_DELETION_POINTER =
            JsonFactory.newBooleanFieldDefinition(TD_DELETION_FIELD);
    private static final JsonFieldDefinition<Boolean> NON_MODELED_ATTRIBUTES_POINTER =
            JsonFactory.newBooleanFieldDefinition(NON_MODELED_ATTRIBUTES_FIELD);
    private static final JsonFieldDefinition<Boolean> NON_MODELED_INBOX_POINTER =
            JsonFactory.newBooleanFieldDefinition(NON_MODELED_INBOX_FIELD);
    private static final JsonFieldDefinition<Boolean> NON_MODELED_OUTBOX_POINTER =
            JsonFactory.newBooleanFieldDefinition(NON_MODELED_OUTBOX_FIELD);

    private final Boolean thingDescriptionDeletion;
    private final Boolean nonModeledAttributes;
    private final Boolean nonModeledInboxMessages;
    private final Boolean nonModeledOutboxMessages;

    private ImmutableThingForbidOverrides(
            final Boolean thingDescriptionDeletion,
            final Boolean nonModeledAttributes,
            final Boolean nonModeledInboxMessages,
            final Boolean nonModeledOutboxMessages) {
        this.thingDescriptionDeletion = thingDescriptionDeletion;
        this.nonModeledAttributes = nonModeledAttributes;
        this.nonModeledInboxMessages = nonModeledInboxMessages;
        this.nonModeledOutboxMessages = nonModeledOutboxMessages;
    }

    /**
     * Creates a new instance.
     */
    public static ImmutableThingForbidOverrides of(
            final Boolean thingDescriptionDeletion,
            final Boolean nonModeledAttributes,
            final Boolean nonModeledInboxMessages,
            final Boolean nonModeledOutboxMessages) {
        return new ImmutableThingForbidOverrides(
                thingDescriptionDeletion,
                nonModeledAttributes,
                nonModeledInboxMessages,
                nonModeledOutboxMessages);
    }

    public Optional<Boolean> getThingDescriptionDeletion() {
        return Optional.ofNullable(thingDescriptionDeletion);
    }

    public Optional<Boolean> getNonModeledAttributes() {
        return Optional.ofNullable(nonModeledAttributes);
    }

    public Optional<Boolean> getNonModeledInboxMessages() {
        return Optional.ofNullable(nonModeledInboxMessages);
    }

    public Optional<Boolean> getNonModeledOutboxMessages() {
        return Optional.ofNullable(nonModeledOutboxMessages);
    }

    @Override
    public JsonObject toJson() {
        final JsonObjectBuilder builder = JsonObject.newBuilder();
        if (thingDescriptionDeletion != null) {
            builder.set(TD_DELETION_FIELD, JsonFactory.newValue(thingDescriptionDeletion));
        }
        if (nonModeledAttributes != null) {
            builder.set(NON_MODELED_ATTRIBUTES_FIELD, JsonFactory.newValue(nonModeledAttributes));
        }
        if (nonModeledInboxMessages != null) {
            builder.set(NON_MODELED_INBOX_FIELD, JsonFactory.newValue(nonModeledInboxMessages));
        }
        if (nonModeledOutboxMessages != null) {
            builder.set(NON_MODELED_OUTBOX_FIELD, JsonFactory.newValue(nonModeledOutboxMessages));
        }
        return builder.build();
    }

    public static ImmutableThingForbidOverrides fromJson(final JsonObject json) {
        final Boolean tdDeletion = json.getValue(TD_DELETION_POINTER).orElse(null);
        final Boolean attrs = json.getValue(NON_MODELED_ATTRIBUTES_POINTER).orElse(null);
        final Boolean inbox = json.getValue(NON_MODELED_INBOX_POINTER).orElse(null);
        final Boolean outbox = json.getValue(NON_MODELED_OUTBOX_POINTER).orElse(null);

        return of(tdDeletion, attrs, inbox, outbox);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ImmutableThingForbidOverrides)) return false;
        final ImmutableThingForbidOverrides that = (ImmutableThingForbidOverrides) o;
        return Objects.equals(thingDescriptionDeletion, that.thingDescriptionDeletion)
                && Objects.equals(nonModeledAttributes, that.nonModeledAttributes)
                && Objects.equals(nonModeledInboxMessages, that.nonModeledInboxMessages)
                && Objects.equals(nonModeledOutboxMessages, that.nonModeledOutboxMessages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(thingDescriptionDeletion, nonModeledAttributes, nonModeledInboxMessages, nonModeledOutboxMessages);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" +
                "thingDescriptionDeletion=" + thingDescriptionDeletion +
                ", nonModeledAttributes=" + nonModeledAttributes +
                ", nonModeledInboxMessages=" + nonModeledInboxMessages +
                ", nonModeledOutboxMessages=" + nonModeledOutboxMessages +
                "]";
    }
}
