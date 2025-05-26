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
import org.eclipse.ditto.json.JsonObject;

import java.util.Optional;

/**
 * Interface for Feature-level forbid configuration in WoT (Web of Things) validation.
 * This represents configuration settings for forbidding certain Feature-level operations.
 */
public interface FeatureValidationForbidConfig extends Jsonifiable<JsonObject> {

    /**
     * Returns whether feature description deletion is forbidden.
     *
     * @return an optional containing whether feature description deletion is forbidden
     */
    Optional<Boolean> isFeatureDescriptionDeletion();

    /**
     * Returns whether non-modeled features are forbidden.
     *
     * @return an optional containing whether non-modeled features are forbidden
     */
    Optional<Boolean> isNonModeledFeatures();

    /**
     * Returns whether non-modeled properties are forbidden.
     *
     * @return an optional containing whether non-modeled properties are forbidden
     */
    Optional<Boolean> isNonModeledProperties();

    /**
     * Returns whether non-modeled desired properties are forbidden.
     *
     * @return an optional containing whether non-modeled desired properties are forbidden
     */
    Optional<Boolean> isNonModeledDesiredProperties();

    /**
     * Returns whether non-modeled inbox messages are forbidden.
     *
     * @return an optional containing whether non-modeled inbox messages are forbidden
     */
    Optional<Boolean> isNonModeledInboxMessages();

    /**
     * Returns whether non-modeled outbox messages are forbidden.
     *
     * @return an optional containing whether non-modeled outbox messages are forbidden
     */
    Optional<Boolean> isNonModeledOutboxMessages();

} 