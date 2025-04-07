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
package org.eclipse.ditto.wot.validation;

import static org.eclipse.ditto.base.model.common.ConditionChecker.checkNotNull;

import java.util.Objects;

import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.base.model.headers.DittoHeaders;
import org.eclipse.ditto.json.JsonFieldDefinition;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonObjectBuilder;
import org.eclipse.ditto.json.JsonFactory;
import org.eclipse.ditto.things.model.ThingDefinition;
import org.eclipse.ditto.things.model.ThingId;
import org.eclipse.ditto.things.model.FeatureDefinition;
import org.eclipse.ditto.things.model.ThingsModelFactory;

/**
 * An immutable implementation of {@link ValidationContext}.
 */
@Immutable
public final class ImmutableValidationContext implements ValidationContext {

    private final ThingId thingId;
    private final ThingDefinition thingDefinition;
    private final FeatureDefinition featureDefinition;
    private final DittoHeaders dittoHeaders;

    private ImmutableValidationContext(final ThingId thingId,
            final ThingDefinition thingDefinition,
            final FeatureDefinition featureDefinition,
            final DittoHeaders dittoHeaders) {
        this.thingId = thingId;
        this.thingDefinition = checkNotNull(thingDefinition, "thingDefinition");
        this.featureDefinition = featureDefinition;
        this.dittoHeaders = checkNotNull(dittoHeaders, "dittoHeaders");
    }

    /**
     * Creates a new {@code ImmutableValidationContext} from the given parameters.
     *
     * @param thingId the thing ID.
     * @param thingDefinition the thing definition.
     * @param featureDefinition the feature definition.
     * @param dittoHeaders the Ditto headers.
     * @return the validation context.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static ImmutableValidationContext of(final ThingId thingId,
            final ThingDefinition thingDefinition,
            final FeatureDefinition featureDefinition,
            final DittoHeaders dittoHeaders) {
        return new ImmutableValidationContext(thingId, thingDefinition, featureDefinition, dittoHeaders);
    }

    /**
     * Creates a new {@code ImmutableValidationContext} from a JSON object.
     *
     * @param jsonObject the JSON object of which the validation context is to be created.
     * @param dittoHeaders the Ditto headers.
     * @return the validation context.
     * @throws NullPointerException if {@code jsonObject} is {@code null}.
     */
    public static ImmutableValidationContext fromJson(final JsonObject jsonObject, final DittoHeaders dittoHeaders) {
        final ThingId thingId = jsonObject.getValue(JsonFields.THING_ID)
                .map(ThingId::of)
                .orElse(null);
        final ThingDefinition thingDefinition = jsonObject.getValue(JsonFields.THING_DEFINITION)
                .map(ThingsModelFactory::newDefinition)
                .orElse(null);
        final FeatureDefinition featureDefinition = jsonObject.getValue(JsonFields.FEATURE_DEFINITION)
                .map(JsonFactory::newArray)
                .map(ThingsModelFactory::newFeatureDefinition)
                .orElse(null);
        return of(thingId, thingDefinition, featureDefinition, dittoHeaders);
    }

    @Override
    public ThingId getThingId() {
        return thingId;
    }

    @Override
    public ThingDefinition getThingDefinition() {
        return thingDefinition;
    }

    @Override
    public FeatureDefinition getFeatureDefinition() {
        return featureDefinition;
    }

    @Override
    public DittoHeaders getDittoHeaders() {
        return dittoHeaders;
    }

    @Override
    public JsonObject toJson() {
        final JsonObjectBuilder jsonObjectBuilder = JsonObject.newBuilder();
        if (thingId != null) {
            jsonObjectBuilder.set(JsonFields.THING_ID, thingId.toString());
        }
        if (thingDefinition != null) {
            jsonObjectBuilder.set(JsonFields.THING_DEFINITION, thingDefinition.toString());
        }
        if (featureDefinition != null) {
            jsonObjectBuilder.set(JsonFields.FEATURE_DEFINITION, featureDefinition.toString());
        }
        return jsonObjectBuilder.build();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ImmutableValidationContext that = (ImmutableValidationContext) o;
        return Objects.equals(thingId, that.thingId) &&
                Objects.equals(thingDefinition, that.thingDefinition) &&
                Objects.equals(featureDefinition, that.featureDefinition) &&
                Objects.equals(dittoHeaders, that.dittoHeaders);
    }

    @Override
    public int hashCode() {
        return Objects.hash(thingId, thingDefinition, featureDefinition, dittoHeaders);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" +
                "thingId=" + thingId +
                ", thingDefinition=" + thingDefinition +
                ", featureDefinition=" + featureDefinition +
                ", dittoHeaders=" + dittoHeaders +
                "]";
    }

    /**
     * JSON field definitions.
     */
    static final class JsonFields {
        static final JsonFieldDefinition<String> THING_ID = 
            JsonFactory.newStringFieldDefinition("thingId");
        static final JsonFieldDefinition<String> THING_DEFINITION = 
            JsonFactory.newStringFieldDefinition("thingDefinition");
        static final JsonFieldDefinition<String> FEATURE_DEFINITION = 
            JsonFactory.newStringFieldDefinition("featureDefinition");
    }
} 