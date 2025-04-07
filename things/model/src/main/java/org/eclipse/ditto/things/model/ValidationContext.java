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
package org.eclipse.ditto.things.model;

import javax.annotation.Nullable;

import org.eclipse.ditto.base.model.headers.DittoHeaders;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.things.model.validation.ImmutableValidationContext;

/**
 * Represents a validation context for WoT validation.
 */
public interface ValidationContext {

    /**
     * Creates a new {@code ValidationContext} from a JSON object.
     *
     * @param jsonObject the JSON object.
     * @return the validation context.
     * @throws NullPointerException if {@code jsonObject} is {@code null}.
     */
    static ValidationContext fromJson(final JsonObject jsonObject) {
        return ImmutableValidationContext.fromJson(jsonObject);
    }

    /**
     * Returns this validation context as JSON object.
     *
     * @return the JSON object.
     */
    JsonObject toJson();

    /**
     * Returns the Ditto headers.
     *
     * @return the Ditto headers.
     */
    DittoHeaders dittoHeaders();

    /**
     * Returns the thing definition.
     *
     * @return the thing definition.
     */
    @Nullable
    ThingDefinition thingDefinition();

    /**
     * Returns the feature definition.
     *
     * @return the feature definition.
     */
    @Nullable
    FeatureDefinition featureDefinition();

    /**
     * Returns the thing ID.
     *
     * @return the thing ID.
     */
    @Nullable
    ThingId thingId();
} 