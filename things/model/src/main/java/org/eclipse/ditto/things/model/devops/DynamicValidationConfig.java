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

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.base.model.json.Jsonifiable;
import org.eclipse.ditto.json.JsonObject;

import java.util.Optional;

/**
 * Interface for dynamic validation configuration.
 * This represents a configuration that can be applied dynamically based on certain conditions.
 */
@Immutable
public interface DynamicValidationConfig extends Jsonifiable<JsonObject> {

    /**
     * Returns the scope identifier for this configuration.
     *
     * @return the scope identifier
     */
    String getScopeId();

    /**
     * Returns the validation context that determines when this configuration should be applied.
     *
     * @return an {@code Optional} containing the validation context, or empty if not set
     */
    Optional<ValidationContext> getValidationContext();

    /**
     * Returns the configuration overrides to apply when the validation context matches.
     *
     * @return an {@code Optional} containing the configuration overrides, or empty if not set
     */
    Optional<ConfigOverrides> getConfigOverrides();

    /**
     * Returns this configuration as a JSON object.
     *
     * @return the JSON representation of this configuration
     */
    JsonObject toJson();

} 