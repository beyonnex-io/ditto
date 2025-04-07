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
import org.eclipse.ditto.json.JsonFactory;
import org.eclipse.ditto.json.JsonFieldDefinition;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonObjectBuilder;

import javax.annotation.concurrent.Immutable;
import java.util.Objects;
import java.util.Optional;


@Immutable
public final class ImmutableFeatureForbidOverrides implements Jsonifiable<JsonObject> {

    private static final String FD_DELETION_FIELD = "feature-description-deletion";
    private static final String NON_MODELED_FEATURES_FIELD = "non-modeled-features";
    private static final String NON_MODELED_PROPERTIES_FIELD = "non-modeled-properties";
    private static final String NON_MODELED_DESIRED_PROPERTIES_FIELD = "non-modeled-desired-properties";
    private static final String NON_MODELED_INBOX_FIELD = "non-modeled-inbox-messages";
    private static final String NON_MODELED_OUTBOX_FIELD = "non-modeled-outbox-messages";

    private static final JsonFieldDefinition<Boolean> FD_DELETION_POINTER = JsonFactory.newBooleanFieldDefinition(FD_DELETION_FIELD);
    private static final JsonFieldDefinition<Boolean> NON_MODELED_FEATURES_POINTER = JsonFactory.newBooleanFieldDefinition(NON_MODELED_FEATURES_FIELD);
    private static final JsonFieldDefinition<Boolean> NON_MODELED_PROPERTIES_POINTER = JsonFactory.newBooleanFieldDefinition(NON_MODELED_PROPERTIES_FIELD);
    private static final JsonFieldDefinition<Boolean> NON_MODELED_DESIRED_PROPERTIES_POINTER = JsonFactory.newBooleanFieldDefinition(NON_MODELED_DESIRED_PROPERTIES_FIELD);
    private static final JsonFieldDefinition<Boolean> NON_MODELED_INBOX_POINTER = JsonFactory.newBooleanFieldDefinition(NON_MODELED_INBOX_FIELD);
    private static final JsonFieldDefinition<Boolean> NON_MODELED_OUTBOX_POINTER = JsonFactory.newBooleanFieldDefinition(NON_MODELED_OUTBOX_FIELD);

    private final Boolean featureDescriptionDeletion;
    private final Boolean nonModeledFeatures;
    private final Boolean nonModeledProperties;
    private final Boolean nonModeledDesiredProperties;
    private final Boolean nonModeledInboxMessages;
    private final Boolean nonModeledOutboxMessages;

    private ImmutableFeatureForbidOverrides(final Boolean featureDescriptionDeletion,
            final Boolean nonModeledFeatures,
            final Boolean nonModeledProperties,
            final Boolean nonModeledDesiredProperties,
            final Boolean nonModeledInboxMessages,
            final Boolean nonModeledOutboxMessages) {
        this.featureDescriptionDeletion = featureDescriptionDeletion;
        this.nonModeledFeatures = nonModeledFeatures;
        this.nonModeledProperties = nonModeledProperties;
        this.nonModeledDesiredProperties = nonModeledDesiredProperties;
        this.nonModeledInboxMessages = nonModeledInboxMessages;
        this.nonModeledOutboxMessages = nonModeledOutboxMessages;
    }

    public static ImmutableFeatureForbidOverrides of(final Boolean featureDescriptionDeletion,
            final Boolean nonModeledFeatures,
            final Boolean nonModeledProperties,
            final Boolean nonModeledDesiredProperties,
            final Boolean nonModeledInboxMessages,
            final Boolean nonModeledOutboxMessages) {
        return new ImmutableFeatureForbidOverrides(
                featureDescriptionDeletion,
                nonModeledFeatures,
                nonModeledProperties,
                nonModeledDesiredProperties,
                nonModeledInboxMessages,
                nonModeledOutboxMessages);
    }

    public Optional<Boolean> getFeatureDescriptionDeletion() {
        return Optional.ofNullable(featureDescriptionDeletion);
    }

    public Optional<Boolean> getNonModeledFeatures() {
        return Optional.ofNullable(nonModeledFeatures);
    }

    public Optional<Boolean> getNonModeledProperties() {
        return Optional.ofNullable(nonModeledProperties);
    }

    public Optional<Boolean> getNonModeledDesiredProperties() {
        return Optional.ofNullable(nonModeledDesiredProperties);
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
        if (featureDescriptionDeletion != null) {
            builder.set(FD_DELETION_FIELD, JsonFactory.newValue(featureDescriptionDeletion));
        }
        if (nonModeledFeatures != null) {
            builder.set(NON_MODELED_FEATURES_FIELD, JsonFactory.newValue(nonModeledFeatures));
        }
        if (nonModeledProperties != null) {
            builder.set(NON_MODELED_PROPERTIES_FIELD, JsonFactory.newValue(nonModeledProperties));
        }
        if (nonModeledDesiredProperties != null) {
            builder.set(NON_MODELED_DESIRED_PROPERTIES_FIELD, JsonFactory.newValue(nonModeledDesiredProperties));
        }
        if (nonModeledInboxMessages != null) {
            builder.set(NON_MODELED_INBOX_FIELD, JsonFactory.newValue(nonModeledInboxMessages));
        }
        if (nonModeledOutboxMessages != null) {
            builder.set(NON_MODELED_OUTBOX_FIELD, JsonFactory.newValue(nonModeledOutboxMessages));
        }
        return builder.build();
    }

    public static ImmutableFeatureForbidOverrides fromJson(final JsonObject json) {
        return of(
                json.getValue(FD_DELETION_POINTER).orElse(null),
                json.getValue(NON_MODELED_FEATURES_POINTER).orElse(null),
                json.getValue(NON_MODELED_PROPERTIES_POINTER).orElse(null),
                json.getValue(NON_MODELED_DESIRED_PROPERTIES_POINTER).orElse(null),
                json.getValue(NON_MODELED_INBOX_POINTER).orElse(null),
                json.getValue(NON_MODELED_OUTBOX_POINTER).orElse(null)
        );
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ImmutableFeatureForbidOverrides)) return false;
        final ImmutableFeatureForbidOverrides that = (ImmutableFeatureForbidOverrides) o;
        return Objects.equals(featureDescriptionDeletion, that.featureDescriptionDeletion)
                && Objects.equals(nonModeledFeatures, that.nonModeledFeatures)
                && Objects.equals(nonModeledProperties, that.nonModeledProperties)
                && Objects.equals(nonModeledDesiredProperties, that.nonModeledDesiredProperties)
                && Objects.equals(nonModeledInboxMessages, that.nonModeledInboxMessages)
                && Objects.equals(nonModeledOutboxMessages, that.nonModeledOutboxMessages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(featureDescriptionDeletion, nonModeledFeatures, nonModeledProperties,
                nonModeledDesiredProperties, nonModeledInboxMessages, nonModeledOutboxMessages);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" +
                "featureDescriptionDeletion=" + featureDescriptionDeletion +
                ", nonModeledFeatures=" + nonModeledFeatures +
                ", nonModeledProperties=" + nonModeledProperties +
                ", nonModeledDesiredProperties=" + nonModeledDesiredProperties +
                ", nonModeledInboxMessages=" + nonModeledInboxMessages +
                ", nonModeledOutboxMessages=" + nonModeledOutboxMessages +
                "]";
    }
}
