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

public interface FeatureValidationConfig extends Jsonifiable<JsonObject> {
    /**
     * Returns the enforce configuration.
     *
     * @return an optional containing the enforce configuration.
     */
    Optional<FeatureValidationEnforceConfig> getEnforce();

    /**
     * Returns the forbid configuration.
     *
     * @return an optional containing the forbid configuration.
     */
    Optional<FeatureValidationForbidConfig> getForbid();

    /**
     * Returns this configuration as JSON object.
     *
     * @return the JSON object representation of this configuration.
     */
    JsonObject toJson();
}