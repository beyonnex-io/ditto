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

import org.eclipse.ditto.json.JsonObject;
import java.util.Optional;

/**
 * Configuration for Feature-specific WoT validation.
 */
public interface FeatureValidationConfig {

    /**
     * Returns whether to enforce/validate a feature whenever its description is modified.
     *
     * @return whether to enforce/validate a feature whenever its description is modified.
     */
    Optional<Boolean> isEnforceFeatureDescriptionModification();

    /**
     * Returns whether to enforce that all modeled features (submodels referenced in the Thing's definition's WoT model) are present.
     *
     * @return whether to enforce that all modeled features are present.
     */
    Optional<Boolean> isEnforcePresenceOfModeledFeatures();

    /**
     * Returns whether to enforce/validate properties of a feature following the defined WoT properties.
     *
     * @return whether to enforce/validate properties of a feature following the defined WoT properties.
     */
    Optional<Boolean> isEnforceProperties();

    /**
     * Returns whether to enforce/validate desired properties of a feature following the defined WoT properties.
     *
     * @return whether to enforce/validate desired properties of a feature following the defined WoT properties.
     */
    Optional<Boolean> isEnforceDesiredProperties();

    /**
     * Returns whether to enforce/validate inbox messages to a feature following the defined WoT action "input".
     *
     * @return whether to enforce/validate inbox messages to a feature following the defined WoT action "input".
     */
    Optional<Boolean> isEnforceInboxMessagesInput();

    /**
     * Returns whether to enforce/validate inbox message responses to a feature following the defined WoT action "output".
     *
     * @return whether to enforce/validate inbox message responses to a feature following the defined WoT action "output".
     */
    Optional<Boolean> isEnforceInboxMessagesOutput();

    /**
     * Returns whether to enforce/validate outbox messages from a feature following the defined WoT events.
     *
     * @return whether to enforce/validate outbox messages from a feature following the defined WoT events.
     */
    Optional<Boolean> isEnforceOutboxMessages();

    /**
     * Returns whether to forbid deletion of a feature's description.
     *
     * @return whether to forbid deletion of a feature's description.
     */
    Optional<Boolean> isForbidFeatureDescriptionDeletion();

    /**
     * Returns whether to forbid adding features to a Thing which were not defined in its definition's WoT model.
     *
     * @return whether to forbid adding features not in the model.
     */
    Optional<Boolean> isForbidNonModeledFeatures();

    /**
     * Returns whether to forbid persisting properties which are not defined as properties in the WoT model.
     *
     * @return whether to forbid persisting properties not in the model.
     */
    Optional<Boolean> isForbidNonModeledProperties();

    /**
     * Returns whether to forbid persisting desired properties which are not defined as properties in the WoT model.
     *
     * @return whether to forbid persisting desired properties not in the model.
     */
    Optional<Boolean> isForbidNonModeledDesiredProperties();

    /**
     * Returns whether to forbid dispatching of inbox messages which are not defined as actions in the WoT model.
     *
     * @return whether to forbid dispatching of inbox messages not in the model.
     */
    Optional<Boolean> isForbidNonModeledInboxMessages();

    /**
     * Returns whether to forbid dispatching of outbox messages which are not defined as events in the WoT model.
     *
     * @return whether to forbid dispatching of outbox messages not in the model.
     */
    Optional<Boolean> isForbidNonModeledOutboxMessages();

    /**
     * Returns this configuration as JSON object.
     *
     * @return the JSON object representation of this configuration.
     */
    JsonObject toJson();
} 