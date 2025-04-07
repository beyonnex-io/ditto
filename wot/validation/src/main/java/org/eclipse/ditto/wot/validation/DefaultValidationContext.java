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
package org.eclipse.ditto.wot.validation;

import java.util.Optional;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.base.model.headers.DittoHeaderDefinition;
import org.eclipse.ditto.base.model.headers.DittoHeaders;
import org.eclipse.ditto.things.model.FeatureDefinition;
import org.eclipse.ditto.things.model.ThingDefinition;
import org.eclipse.ditto.things.model.ThingId;

/**
 * Default implementation of {@link ValidationContext}.
 */
@Immutable
public record DefaultValidationContext(
        DittoHeaders dittoHeaders,
        @Nullable ThingDefinition thingDefinition,
        @Nullable FeatureDefinition featureDefinition,
        @Nullable ThingId thingId
) {

    /**
     * Creates a new instance of {@code DefaultValidationContext}.
     *
     * @param dittoHeaders the DittoHeaders.
     * @param thingDefinition the ThingDefinition.
     * @param featureDefinition the FeatureDefinition.
     * @return the new instance.
     */
    public static DefaultValidationContext of(final DittoHeaders dittoHeaders,
            @Nullable final ThingDefinition thingDefinition,
            @Nullable final FeatureDefinition featureDefinition) {
        return new DefaultValidationContext(dittoHeaders, thingDefinition, featureDefinition,
                extractThingId(dittoHeaders).orElse(null));
    }

    /**
     * Creates a new instance of {@code DefaultValidationContext}.
     *
     * @param dittoHeaders the DittoHeaders.
     * @param thingDefinition the ThingDefinition.
     * @param featureDefinition the FeatureDefinition.
     * @param thingId the ThingId.
     * @return the new instance.
     */
    public static DefaultValidationContext of(final DittoHeaders dittoHeaders,
            @Nullable final ThingDefinition thingDefinition,
            @Nullable final FeatureDefinition featureDefinition,
            @Nullable final ThingId thingId) {
        return new DefaultValidationContext(dittoHeaders, thingDefinition, featureDefinition, thingId);
    }

    private static Optional<ThingId> extractThingId(final DittoHeaders dittoHeaders) {
        final String nullableEntityId = dittoHeaders.get(DittoHeaderDefinition.ENTITY_ID.getKey());
        return Optional.ofNullable(nullableEntityId)
                .map(entityId -> entityId.substring(entityId.indexOf(":") + 1)) // starts with "thing:" - cut that off!
                .map(ThingId::of);
    }
} 