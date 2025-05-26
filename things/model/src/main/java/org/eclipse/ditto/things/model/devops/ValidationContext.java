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

import java.util.List;
import java.util.Map;

/**
 * Interface for validation context in WoT (Web of Things) validation configuration.
 * This represents the context in which validation rules should be applied.
 */
public interface ValidationContext extends Jsonifiable<JsonObject> {

    /**
     * Returns the list of Ditto header patterns to match.
     *
     * @return the list of header pattern maps
     */
    List<Map<String, String>> getDittoHeadersPatterns();

    /**
     * Returns the list of regex patterns for Thing definition URLs.
     *
     * @return the list of Thing definition patterns
     */
    List<String> getThingDefinitionPatterns();

    /**
     * Returns the list of regex patterns for Feature definition URLs.
     *
     * @return the list of Feature definition patterns
     */
    List<String> getFeatureDefinitionPatterns();

    /**
     * Returns the scope ID.
     *
     * @return the scope ID
     */
    String getScopeId();
} 