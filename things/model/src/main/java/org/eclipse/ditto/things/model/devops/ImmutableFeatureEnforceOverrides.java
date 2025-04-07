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

// Existing classes: ImmutableThingEnforceOverrides, ImmutableThingConfigOverrides...
// --- Add the missing Feature classes below ---

/**
 * Represents the override flags for enforcing Feature-level WoT validation.
 */
@Immutable
public final class ImmutableFeatureEnforceOverrides implements Jsonifiable<JsonObject> {

    private static final String FD_MODIFICATION_FIELD = "feature-description-modification";
    private static final String PRESENCE_OF_MODELED_FEATURES_FIELD = "presence-of-modeled-features";
    private static final String PROPERTIES_FIELD = "properties";
    private static final String DESIRED_PROPERTIES_FIELD = "desired-properties";
    private static final String INBOX_INPUT_FIELD = "inbox-messages-input";
    private static final String INBOX_OUTPUT_FIELD = "inbox-messages-output";
    private static final String OUTBOX_FIELD = "outbox-messages";

    private static final JsonFieldDefinition<Boolean> FD_MODIFICATION_POINTER = JsonFactory.newBooleanFieldDefinition(FD_MODIFICATION_FIELD);
    private static final JsonFieldDefinition<Boolean> PRESENCE_OF_MODELED_FEATURES_POINTER = JsonFactory.newBooleanFieldDefinition(PRESENCE_OF_MODELED_FEATURES_FIELD);
    private static final JsonFieldDefinition<Boolean> PROPERTIES_POINTER = JsonFactory.newBooleanFieldDefinition(PROPERTIES_FIELD);
    private static final JsonFieldDefinition<Boolean> DESIRED_PROPERTIES_POINTER = JsonFactory.newBooleanFieldDefinition(DESIRED_PROPERTIES_FIELD);
    private static final JsonFieldDefinition<Boolean> INBOX_INPUT_POINTER = JsonFactory.newBooleanFieldDefinition(INBOX_INPUT_FIELD);
    private static final JsonFieldDefinition<Boolean> INBOX_OUTPUT_POINTER = JsonFactory.newBooleanFieldDefinition(INBOX_OUTPUT_FIELD);
    private static final JsonFieldDefinition<Boolean> OUTBOX_POINTER = JsonFactory.newBooleanFieldDefinition(OUTBOX_FIELD);

    private final Boolean featureDescriptionModification;
    private final Boolean presenceOfModeledFeatures;
    private final Boolean properties;
    private final Boolean desiredProperties;
    private final Boolean inboxMessagesInput;
    private final Boolean inboxMessagesOutput;
    private final Boolean outboxMessages;

    private ImmutableFeatureEnforceOverrides(final Boolean featureDescriptionModification,
            final Boolean presenceOfModeledFeatures,
            final Boolean properties,
            final Boolean desiredProperties,
            final Boolean inboxMessagesInput,
            final Boolean inboxMessagesOutput,
            final Boolean outboxMessages) {
        this.featureDescriptionModification = featureDescriptionModification;
        this.presenceOfModeledFeatures = presenceOfModeledFeatures;
        this.properties = properties;
        this.desiredProperties = desiredProperties;
        this.inboxMessagesInput = inboxMessagesInput;
        this.inboxMessagesOutput = inboxMessagesOutput;
        this.outboxMessages = outboxMessages;
    }

    public static ImmutableFeatureEnforceOverrides of(final Boolean featureDescriptionModification,
            final Boolean presenceOfModeledFeatures,
            final Boolean properties,
            final Boolean desiredProperties,
            final Boolean inboxMessagesInput,
            final Boolean inboxMessagesOutput,
            final Boolean outboxMessages) {
        return new ImmutableFeatureEnforceOverrides(
                featureDescriptionModification,
                presenceOfModeledFeatures,
                properties,
                desiredProperties,
                inboxMessagesInput,
                inboxMessagesOutput,
                outboxMessages);
    }

    public Optional<Boolean> getFeatureDescriptionModification() {
        return Optional.ofNullable(featureDescriptionModification);
    }

    public Optional<Boolean> getPresenceOfModeledFeatures() {
        return Optional.ofNullable(presenceOfModeledFeatures);
    }

    public Optional<Boolean> getProperties() {
        return Optional.ofNullable(properties);
    }

    public Optional<Boolean> getDesiredProperties() {
        return Optional.ofNullable(desiredProperties);
    }

    public Optional<Boolean> getInboxMessagesInput() {
        return Optional.ofNullable(inboxMessagesInput);
    }

    public Optional<Boolean> getInboxMessagesOutput() {
        return Optional.ofNullable(inboxMessagesOutput);
    }

    public Optional<Boolean> getOutboxMessages() {
        return Optional.ofNullable(outboxMessages);
    }

    @Override
    public JsonObject toJson() {
        final JsonObjectBuilder builder = JsonObject.newBuilder();
        if (featureDescriptionModification != null) {
            builder.set(FD_MODIFICATION_FIELD, JsonFactory.newValue(featureDescriptionModification));
        }
        if (presenceOfModeledFeatures != null) {
            builder.set(PRESENCE_OF_MODELED_FEATURES_FIELD, JsonFactory.newValue(presenceOfModeledFeatures));
        }
        if (properties != null) {
            builder.set(PROPERTIES_FIELD, JsonFactory.newValue(properties));
        }
        if (desiredProperties != null) {
            builder.set(DESIRED_PROPERTIES_FIELD, JsonFactory.newValue(desiredProperties));
        }
        if (inboxMessagesInput != null) {
            builder.set(INBOX_INPUT_FIELD, JsonFactory.newValue(inboxMessagesInput));
        }
        if (inboxMessagesOutput != null) {
            builder.set(INBOX_OUTPUT_FIELD, JsonFactory.newValue(inboxMessagesOutput));
        }
        if (outboxMessages != null) {
            builder.set(OUTBOX_FIELD, JsonFactory.newValue(outboxMessages));
        }
        return builder.build();
    }

    public static ImmutableFeatureEnforceOverrides fromJson(final JsonObject json) {
        return of(
                json.getValue(FD_MODIFICATION_POINTER).orElse(null),
                json.getValue(PRESENCE_OF_MODELED_FEATURES_POINTER).orElse(null),
                json.getValue(PROPERTIES_POINTER).orElse(null),
                json.getValue(DESIRED_PROPERTIES_POINTER).orElse(null),
                json.getValue(INBOX_INPUT_POINTER).orElse(null),
                json.getValue(INBOX_OUTPUT_POINTER).orElse(null),
                json.getValue(OUTBOX_POINTER).orElse(null)
        );
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ImmutableFeatureEnforceOverrides)) return false;
        final ImmutableFeatureEnforceOverrides that = (ImmutableFeatureEnforceOverrides) o;
        return Objects.equals(featureDescriptionModification, that.featureDescriptionModification)
                && Objects.equals(presenceOfModeledFeatures, that.presenceOfModeledFeatures)
                && Objects.equals(properties, that.properties)
                && Objects.equals(desiredProperties, that.desiredProperties)
                && Objects.equals(inboxMessagesInput, that.inboxMessagesInput)
                && Objects.equals(inboxMessagesOutput, that.inboxMessagesOutput)
                && Objects.equals(outboxMessages, that.outboxMessages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(featureDescriptionModification, presenceOfModeledFeatures, properties,
                desiredProperties, inboxMessagesInput, inboxMessagesOutput, outboxMessages);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" +
                "featureDescriptionModification=" + featureDescriptionModification +
                ", presenceOfModeledFeatures=" + presenceOfModeledFeatures +
                ", properties=" + properties +
                ", desiredProperties=" + desiredProperties +
                ", inboxMessagesInput=" + inboxMessagesInput +
                ", inboxMessagesOutput=" + inboxMessagesOutput +
                ", outboxMessages=" + outboxMessages +
                "]";
    }
}
