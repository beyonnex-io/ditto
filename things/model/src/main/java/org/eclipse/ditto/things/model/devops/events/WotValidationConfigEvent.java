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
package org.eclipse.ditto.things.model.devops.events;

import org.eclipse.ditto.base.model.headers.DittoHeaders;
import org.eclipse.ditto.base.model.json.FieldType;
import org.eclipse.ditto.base.model.json.JsonSchemaVersion;
import org.eclipse.ditto.base.model.signals.events.Event;
import org.eclipse.ditto.base.model.signals.events.EventsourcedEvent;
import org.eclipse.ditto.json.JsonField;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.things.model.devops.ImmutableWoTValidationConfig;
import org.eclipse.ditto.things.model.devops.WotValidationConfigId;

import java.util.function.Predicate;

/**
 * Base interface for all WoT validation config events.
 */
public interface WotValidationConfigEvent
        extends Event<WotValidationConfigEvent>, EventsourcedEvent<WotValidationConfigEvent> {

    /**
     * The type prefix of WoT validation config events.
     */
    String TYPE_PREFIX = "wot.validation.config.";

    /**
     * Returns the ID of the WoT validation config.
     *
     * @return the ID.
     */
    WotValidationConfigId getEntityId();

    /**
     * Returns the type of this event.
     *
     * @return the type.
     */
    @Override
    default String getType() {
        return TYPE_PREFIX + getClass().getSimpleName();
    }


    /**
     * Returns a copy of this WotValidationConfigEvent with the given DittoHeaders.
     *
     * @param dittoHeaders the new headers.
     * @return the copy.
     */
    @Override
    WotValidationConfigEvent setDittoHeaders(DittoHeaders dittoHeaders);

    /**
     * Returns the WoT validation config of this event.
     *
     * @return the WoT validation config.
     */
    ImmutableWoTValidationConfig getConfig();

    /**
     * Returns all non hidden marked fields of this event.
     *
     * @return a JSON object representation of this event including only non hidden marked fields.
     */
    @Override
    default JsonObject toJson() {
        return toJson(FieldType.notHidden());
    }

    /**
     * Returns all fields of this event matching the given predicate.
     *
     * @param predicate the predicate to apply to each field when building the JSON object.
     * @return a JSON object representation of this event including all fields matching the predicate.
     */
    @Override
    default JsonObject toJson(final Predicate<JsonField> predicate) {
        return toJson(JsonSchemaVersion.LATEST, predicate);
    }
} 