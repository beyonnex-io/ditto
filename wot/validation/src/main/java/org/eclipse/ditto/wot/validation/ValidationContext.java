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

import org.eclipse.ditto.base.model.headers.DittoHeaders;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.things.model.ThingDefinition;
import org.eclipse.ditto.things.model.ThingId;
import org.eclipse.ditto.things.model.FeatureDefinition;

/**
 * Interface for WoT validation context.
 */
public interface ValidationContext {

    /**
     * Returns the thing ID.
     *
     * @return the thing ID.
     */
    ThingId getThingId();

    /**
     * Returns the thing definition.
     *
     * @return the thing definition.
     */
    ThingDefinition getThingDefinition();

    /**
     * Returns the feature definition.
     *
     * @return the feature definition.
     */
    FeatureDefinition getFeatureDefinition();

    /**
     * Returns the Ditto headers.
     *
     * @return the Ditto headers.
     */
    DittoHeaders getDittoHeaders();

    /**
     * Returns the validation context as a JSON object.
     *
     * @return the validation context.
     */
    JsonObject toJson();

    /**
     * Creates a new validation context from a JSON object.
     *
     * @param jsonObject the JSON object.
     * @param dittoHeaders the Ditto headers.
     * @return the validation context.
     */
    static ValidationContext fromJson(final JsonObject jsonObject, final DittoHeaders dittoHeaders) {
        return ImmutableValidationContext.fromJson(jsonObject, dittoHeaders);
    }

    /**
     * Creates a new validation context with the given parameters.
     *
     * @param dittoHeaders the headers.
     * @param thingDefinition the thing definition.
     * @param featureDefinition the feature definition.
     * @return the validation context.
     */
    static ValidationContext buildValidationContext(final DittoHeaders dittoHeaders,
            final ThingDefinition thingDefinition,
            final FeatureDefinition featureDefinition) {
        return ImmutableValidationContext.of(null, thingDefinition, featureDefinition, dittoHeaders);
    }

    /**
     * Creates a new validation context with the given parameters.
     *
     * @param dittoHeaders the headers.
     * @param thingDefinition the thing definition.
     * @return the validation context.
     */
    static ValidationContext buildValidationContext(final DittoHeaders dittoHeaders,
            final ThingDefinition thingDefinition) {
        return ImmutableValidationContext.of(null, thingDefinition, null, dittoHeaders);
    }

    /**
     * Creates a new validation context with the given parameters.
     *
     * @param dittoHeaders the headers.
     * @param thingDefinition the thing definition.
     * @param featureDefinition the feature definition.
     * @param thingId the thing ID.
     * @return the validation context.
     */
    static ValidationContext buildValidationContext(final DittoHeaders dittoHeaders,
            final ThingDefinition thingDefinition,
            final FeatureDefinition featureDefinition,
            final ThingId thingId) {
        return ImmutableValidationContext.of(thingId, thingDefinition, featureDefinition, dittoHeaders);
    }

    /**
     * Returns the thing ID.
     *
     * @return the thing ID.
     */
    default ThingId thingId() {
        return getThingId();
    }

    /**
     * Returns the thing definition.
     *
     * @return the thing definition.
     */
    default ThingDefinition thingDefinition() {
        return getThingDefinition();
    }

    /**
     * Returns the feature definition.
     *
     * @return the feature definition.
     */
    default FeatureDefinition featureDefinition() {
        return getFeatureDefinition();
    }

    /**
     * Returns the Ditto headers.
     *
     * @return the Ditto headers.
     */
    default DittoHeaders dittoHeaders() {
        return getDittoHeaders();
    }
} 