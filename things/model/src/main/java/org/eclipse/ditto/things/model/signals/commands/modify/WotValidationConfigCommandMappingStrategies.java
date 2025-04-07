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
package org.eclipse.ditto.things.model.signals.commands.modify;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.base.model.headers.DittoHeaders;
import org.eclipse.ditto.base.model.signals.AbstractJsonParsableRegistry;
import org.eclipse.ditto.base.model.signals.JsonParsable;
import org.eclipse.ditto.json.JsonFactory;
import org.eclipse.ditto.json.JsonFieldDefinition;
import org.eclipse.ditto.json.JsonObject;

/**
 * Mapping strategies for WoT validation config commands.
 */
@Immutable
public final class WotValidationConfigCommandMappingStrategies extends AbstractJsonParsableRegistry<WotValidationConfigCommand<?>> {

    private static final WotValidationConfigCommandMappingStrategies INSTANCE = new WotValidationConfigCommandMappingStrategies();

    private static final JsonFieldDefinition<String> TYPE = JsonFactory.newStringFieldDefinition("type");

    private WotValidationConfigCommandMappingStrategies() {
        super(initMappingStrategies());
    }

    private static Map<String, JsonParsable<WotValidationConfigCommand<?>>> initMappingStrategies() {
        final Map<String, JsonParsable<WotValidationConfigCommand<?>>> strategies = new HashMap<>();

        strategies.put(CreateWotValidationConfig.TYPE, CreateWotValidationConfig::fromJson);
        strategies.put(RetrieveWotValidationConfig.TYPE, RetrieveWotValidationConfig::fromJson);

        return strategies;
    }

    /**
     * Returns an instance of WotValidationConfigCommandMappingStrategies.
     *
     * @return the instance.
     */
    public static WotValidationConfigCommandMappingStrategies getInstance() {
        return INSTANCE;
    }

    @Override
    protected String resolveType(final JsonObject jsonObject) {
        return jsonObject.getValueOrThrow(TYPE);
    }
} 