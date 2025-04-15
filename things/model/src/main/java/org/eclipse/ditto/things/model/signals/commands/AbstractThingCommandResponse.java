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
package org.eclipse.ditto.things.model.signals.commands;

import java.util.Objects;
import java.util.function.Predicate;

import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.base.model.common.HttpStatus;
import org.eclipse.ditto.base.model.headers.DittoHeaders;
import org.eclipse.ditto.base.model.json.JsonSchemaVersion;
import org.eclipse.ditto.base.model.signals.WithType;
import org.eclipse.ditto.base.model.signals.commands.AbstractCommandResponse;
import org.eclipse.ditto.json.JsonField;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonObjectBuilder;
import org.eclipse.ditto.json.JsonPointer;
import org.eclipse.ditto.things.model.ThingConstants;
import org.eclipse.ditto.things.model.ThingId;

/**
 * Abstract base class for thing command responses.
 *
 * @param <T> the type of the implementing class.
 */
@Immutable
public abstract class AbstractThingCommandResponse<T extends AbstractThingCommandResponse<T>>
        extends AbstractCommandResponse<T> implements ThingCommandResponse<T> {

    private final ThingId thingId;

    /**
     * Constructs a new {@code AbstractThingCommandResponse} object.
     *
     * @param type the name of this response.
     * @param status the status of this response.
     * @param thingId the ID of the thing.
     * @param dittoHeaders the headers of the response.
     * @throws NullPointerException if any argument is {@code null}.
     */
    protected AbstractThingCommandResponse(final String type, final HttpStatus status, final ThingId thingId,
            final DittoHeaders dittoHeaders) {
        super(type, status, dittoHeaders);
        this.thingId = Objects.requireNonNull(thingId, "thingId");
    }

    @Override
    public ThingId getEntityId() {
        return thingId;
    }

    @Override
    public String getResourceType() {
        return ThingConstants.ENTITY_TYPE.toString();
    }

    @Override
    public JsonPointer getResourcePath() {
        return JsonPointer.empty();
    }

    @Override
    protected void appendPayload(final JsonObjectBuilder jsonBuilder, final JsonSchemaVersion schemaVersion,
            final Predicate<JsonField> predicate) {
        jsonBuilder.set(ThingCommandResponse.JsonFields.JSON_THING_ID, thingId.toString(), predicate);
    }

    @Override
    public String getType() {
        return ThingCommandResponse.TYPE_PREFIX + getName();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), thingId);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final AbstractThingCommandResponse<?> that = (AbstractThingCommandResponse<?>) o;
        return Objects.equals(thingId, that.thingId);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" +
                super.toString() +
                ", thingId=" + thingId +
                "]";
    }
}