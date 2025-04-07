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
package org.eclipse.ditto.things.model.validation;

import static org.eclipse.ditto.base.model.common.ConditionChecker.checkNotNull;

import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.base.model.headers.DittoHeaders;
import org.eclipse.ditto.json.JsonFactory;
import org.eclipse.ditto.json.JsonFieldDefinition;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonObjectBuilder;
import org.eclipse.ditto.things.model.FeatureDefinition;
import org.eclipse.ditto.things.model.ThingDefinition;
import org.eclipse.ditto.things.model.ThingId;
import org.eclipse.ditto.things.model.ThingsModelFactory;
import org.eclipse.ditto.things.model.ValidationContext;

/**
 * Immutable implementation of {@link ValidationContext}.
 */
@Immutable
public final class ImmutableValidationContext implements ValidationContext {

    /**
     * JSON field containing the thing definition.
     */
    public static final JsonFieldDefinition<String> JSON_THING_DEFINITION =
            JsonFactory.newStringFieldDefinition("thingDefinition");

    /**
     * JSON field containing the feature definition.
     */
    public static final JsonFieldDefinition<String> JSON_FEATURE_DEFINITION =
            JsonFactory.newStringFieldDefinition("featureDefinition");

    /**
     * JSON field containing the thing ID.
     */
    public static final JsonFieldDefinition<String> JSON_THING_ID =
            JsonFactory.newStringFieldDefinition("thingId");

    private final DittoHeaders dittoHeaders;
    @Nullable private final ThingDefinition thingDefinition;
    @Nullable private final FeatureDefinition featureDefinition;
    @Nullable private final ThingId thingId;

    private ImmutableValidationContext(final DittoHeaders dittoHeaders,
            @Nullable final ThingDefinition thingDefinition,
            @Nullable final FeatureDefinition featureDefinition,
            @Nullable final ThingId thingId) {
        this.dittoHeaders = checkNotNull(dittoHeaders, "dittoHeaders");
        this.thingDefinition = thingDefinition;
        this.featureDefinition = featureDefinition;
        this.thingId = thingId;
    }

    /**
     * Creates a new {@code ValidationContext} from a JSON object.
     *
     * @param jsonObject the JSON object.
     * @return the validation context.
     * @throws NullPointerException if {@code jsonObject} is {@code null}.
     */
    public static ValidationContext fromJson(final JsonObject jsonObject) {
        checkNotNull(jsonObject, "jsonObject");

        final DittoHeaders dittoHeaders = DittoHeaders.empty();
        final ThingDefinition thingDefinition = jsonObject.getValue(JSON_THING_DEFINITION)
                .map(ThingsModelFactory::newDefinition)
                .orElse(null);
        final FeatureDefinition featureDefinition = jsonObject.getValue(JSON_FEATURE_DEFINITION)
                .map(JsonFactory::newArray)
                .map(ThingsModelFactory::newFeatureDefinition)
                .orElse(null);
        final ThingId thingId = jsonObject.getValue(JSON_THING_ID)
                .map(ThingId::of)
                .orElse(null);
        return new ImmutableValidationContext(dittoHeaders, thingDefinition, featureDefinition, thingId);
    }

    /**
     * Creates a new {@code ValidationContext} with the given parameters.
     *
     * @param dittoHeaders the headers.
     * @param thingDefinition the thing definition.
     * @param featureDefinition the feature definition.
     * @return the validation context.
     * @throws NullPointerException if {@code dittoHeaders} is {@code null}.
     */
    public static ValidationContext buildValidationContext(final DittoHeaders dittoHeaders,
            @Nullable final ThingDefinition thingDefinition,
            @Nullable final FeatureDefinition featureDefinition) {
        return new ImmutableValidationContext(dittoHeaders, thingDefinition, featureDefinition,
                extractThingId(dittoHeaders).orElse(null));
    }

    /**
     * Creates a new {@code ValidationContext} with the given parameters.
     *
     * @param dittoHeaders the headers.
     * @param thingDefinition the thing definition.
     * @return the validation context.
     * @throws NullPointerException if {@code dittoHeaders} is {@code null}.
     */
    public static ValidationContext buildValidationContext(final DittoHeaders dittoHeaders,
            @Nullable final ThingDefinition thingDefinition) {
        return new ImmutableValidationContext(dittoHeaders, thingDefinition, null,
                extractThingId(dittoHeaders).orElse(null));
    }

    private static Optional<ThingId> extractThingId(final DittoHeaders dittoHeaders) {
        return Optional.ofNullable(dittoHeaders.get("thing-id")).map(ThingId::of);
    }

    @Override
    public DittoHeaders dittoHeaders() {
        return dittoHeaders;
    }

    @Nullable
    @Override
    public ThingDefinition thingDefinition() {
        return thingDefinition;
    }

    @Nullable
    @Override
    public FeatureDefinition featureDefinition() {
        return featureDefinition;
    }

    @Nullable
    @Override
    public ThingId thingId() {
        return thingId;
    }

    @Override
    public JsonObject toJson() {
        final JsonObjectBuilder jsonObjectBuilder = JsonObject.newBuilder();
        if (thingDefinition != null) {
            jsonObjectBuilder.set(JSON_THING_DEFINITION, thingDefinition.toString());
        }
        if (featureDefinition != null) {
            jsonObjectBuilder.set(JSON_FEATURE_DEFINITION, featureDefinition.toString());
        }
        if (thingId != null) {
            jsonObjectBuilder.set(JSON_THING_ID, thingId.toString());
        }
        return jsonObjectBuilder.build();
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ImmutableValidationContext that = (ImmutableValidationContext) o;
        return Objects.equals(dittoHeaders, that.dittoHeaders) &&
                Objects.equals(thingDefinition, that.thingDefinition) &&
                Objects.equals(featureDefinition, that.featureDefinition) &&
                Objects.equals(thingId, that.thingId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dittoHeaders, thingDefinition, featureDefinition, thingId);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" +
                "dittoHeaders=" + dittoHeaders +
                ", thingDefinition=" + thingDefinition +
                ", featureDefinition=" + featureDefinition +
                ", thingId=" + thingId +
                "]";
    }
} 