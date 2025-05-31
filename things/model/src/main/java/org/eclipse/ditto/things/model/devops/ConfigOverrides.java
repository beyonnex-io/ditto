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
 * Interface for configuration overrides in WoT validation.
 * This represents overrides for configuration settings.
 */
public interface ConfigOverrides extends Jsonifiable<JsonObject> {

    /**
     * Returns the override for the enabled flag.
     *
     * @return an optional containing the override value
     */
    Optional<Boolean> getEnabled();

    /**
     * Returns the override for the log warning flag.
     *
     * @return an optional containing the override value
     */
    Optional<Boolean> getLogWarningInsteadOfFailingApiCalls();

    /**
     * Returns the Thing-level config overrides.
     *
     * @return an optional containing the Thing-level config overrides
     */
    Optional<ThingValidationConfig> getThingConfig();

    /**
     * Returns the Feature-level config overrides.
     *
     * @return an optional containing the Feature-level config overrides
     */
    Optional<FeatureValidationConfig> getFeatureConfig();

} 