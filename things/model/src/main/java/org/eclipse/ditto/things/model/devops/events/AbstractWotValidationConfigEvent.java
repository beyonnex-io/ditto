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

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.base.model.entity.metadata.Metadata;
import org.eclipse.ditto.base.model.headers.DittoHeaders;
import org.eclipse.ditto.base.model.json.JsonSchemaVersion;
import org.eclipse.ditto.base.model.signals.events.AbstractEvent;
import org.eclipse.ditto.json.JsonFactory;
import org.eclipse.ditto.json.JsonField;
import org.eclipse.ditto.json.JsonFieldDefinition;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonObjectBuilder;
import org.eclipse.ditto.json.JsonPointer;
import org.eclipse.ditto.json.JsonValue;
import org.eclipse.ditto.things.model.devops.ImmutableWoTValidationConfig;

/**
 * Abstract base class for WoT validation config events.
 *
 * @param <T> the type of the implementing class.
 */
@Immutable
public abstract class AbstractWotValidationConfigEvent<T extends AbstractWotValidationConfigEvent<T>> 
        extends AbstractEvent<T> {

    private final ImmutableWoTValidationConfig config;

    protected AbstractWotValidationConfigEvent(final String type,
            final ImmutableWoTValidationConfig config,
            @Nullable final Instant timestamp,
            final DittoHeaders dittoHeaders,
            @Nullable final Metadata metadata) {
        super(type, timestamp, dittoHeaders, metadata);
        this.config = config;
    }

    public ImmutableWoTValidationConfig getConfig() {
        return config;
    }

    @Override
    public JsonPointer getResourcePath() {
        return JsonPointer.empty();
    }

    @Override
    public String getResourceType() {
        return "wot-validation-config";
    }

    @Override
    protected void appendPayload(final JsonObjectBuilder jsonObjectBuilder,
            final JsonSchemaVersion schemaVersion,
            final Predicate<JsonField> thePredicate) {
        final Predicate<JsonField> predicate = schemaVersion.and(thePredicate);
        jsonObjectBuilder.set(JsonFields.CONFIG, config.toJson());
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AbstractWotValidationConfigEvent<?> that = (AbstractWotValidationConfigEvent<?>) o;
        return that.canEqual(this) &&
                Objects.equals(config, that.config) &&
                Objects.equals(getTimestamp(), that.getTimestamp()) &&
                Objects.equals(getDittoHeaders(), that.getDittoHeaders()) &&
                Objects.equals(getMetadata(), that.getMetadata());
    }

    @Override
    protected boolean canEqual(@Nullable final Object other) {
        return other instanceof AbstractWotValidationConfigEvent;
    }

    @Override
    public int hashCode() {
        return Objects.hash(config, getTimestamp(), getDittoHeaders(), getMetadata());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" +
                "config=" + config +
                ", timestamp=" + getTimestamp() +
                ", dittoHeaders=" + getDittoHeaders() +
                ", metadata=" + getMetadata() +
                "]";
    }

    protected static final class JsonFields {
        static final JsonFieldDefinition<JsonObject> CONFIG =
                JsonFieldDefinition.ofJsonObject("config");
    }
} 