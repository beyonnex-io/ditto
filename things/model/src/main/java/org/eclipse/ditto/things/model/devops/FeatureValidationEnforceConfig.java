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
 * Interface for Feature-level enforce configuration in WoT (Web of Things) validation.
 * This represents configuration settings for enforcing Feature-level validation rules.
 */
public interface FeatureValidationEnforceConfig extends Jsonifiable<JsonObject> {

    /**
     * Returns whether feature description modification is enforced.
     *
     * @return an optional containing whether feature description modification is enforced
     */
    Optional<Boolean> isFeatureDescriptionModification();

    /**
     * Returns whether presence of modeled features is enforced.
     *
     * @return an optional containing whether presence of modeled features is enforced
     */
    Optional<Boolean> isPresenceOfModeledFeatures();

    /**
     * Returns whether properties are enforced.
     *
     * @return an optional containing whether properties are enforced
     */
    Optional<Boolean> isProperties();

    /**
     * Returns whether desired properties are enforced.
     *
     * @return an optional containing whether desired properties are enforced
     */
    Optional<Boolean> isDesiredProperties();

    /**
     * Returns whether inbox messages input is enforced.
     *
     * @return an optional containing whether inbox messages input is enforced
     */
    Optional<Boolean> isInboxMessagesInput();

    /**
     * Returns whether inbox messages output is enforced.
     *
     * @return an optional containing whether inbox messages output is enforced
     */
    Optional<Boolean> isInboxMessagesOutput();

    /**
     * Returns whether outbox messages are enforced.
     *
     * @return an optional containing whether outbox messages are enforced
     */
    Optional<Boolean> isOutboxMessages();
} 