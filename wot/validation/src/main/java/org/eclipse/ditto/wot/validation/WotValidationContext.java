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

import org.eclipse.ditto.things.model.ThingId;
import org.eclipse.ditto.things.model.devops.ImmutableWoTValidationConfig;

import java.util.Optional;

/**
 * Represents the context for WoT validation, including the validation configuration and Thing ID.
 */
public final class WotValidationContext {

    private final ThingId thingId;
    private final ImmutableWoTValidationConfig config;

    private WotValidationContext(final ThingId thingId, final ImmutableWoTValidationConfig config) {
        this.thingId = thingId;
        this.config = config;
    }

    /**
     * Creates a new WoT validation context.
     *
     * @param thingId the ID of the Thing being validated
     * @param config the validation configuration
     * @return the new validation context
     */
    public static WotValidationContext of(final ThingId thingId, final ImmutableWoTValidationConfig config) {
        return new WotValidationContext(thingId, config);
    }

    /**
     * Gets the Thing ID.
     *
     * @return the Thing ID
     */
    public ThingId getThingId() {
        return thingId;
    }

    /**
     * Gets the validation configuration.
     *
     * @return the validation configuration
     */
    public Optional<ImmutableWoTValidationConfig> getConfig() {
        return Optional.ofNullable(config);
    }
} 