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
import org.eclipse.ditto.json.JsonObjectBuilder;
import org.eclipse.ditto.json.JsonValue;

import javax.annotation.concurrent.Immutable;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents the configuration overrides for thing-level WoT validation.
 */
@Immutable
public final class ImmutableThingConfigOverrides implements Jsonifiable<JsonObject> {

    private static final String ENFORCE_FIELD = "enforce";
    private static final String FORBID_FIELD = "forbid";

    private final ImmutableThingEnforceOverrides enforce;
    private final ImmutableThingForbidOverrides forbid;

    private ImmutableThingConfigOverrides(final ImmutableThingEnforceOverrides enforce,
            final ImmutableThingForbidOverrides forbid) {
        this.enforce = enforce;
        this.forbid = forbid;
    }

    /**
     * Creates a new {@code ImmutableThingConfigOverrides} instance.
     *
     * @param enforce the enforcement flags.
     * @param forbid  the forbidding flags.
     * @return the new instance.
     */
    public static ImmutableThingConfigOverrides of(final ImmutableThingEnforceOverrides enforce,
            final ImmutableThingForbidOverrides forbid) {
        return new ImmutableThingConfigOverrides(enforce, forbid);
    }

    /**
     * @return optional enforce overrides.
     */
    public Optional<ImmutableThingEnforceOverrides> getEnforce() {
        return Optional.ofNullable(enforce);
    }

    /**
     * @return optional forbid overrides.
     */
    public Optional<ImmutableThingForbidOverrides> getForbid() {
        return Optional.ofNullable(forbid);
    }

    @Override
    public JsonObject toJson() {
        final JsonObjectBuilder builder = JsonObject.newBuilder();
        if (enforce != null) {
            builder.set(ENFORCE_FIELD, enforce.toJson());
        }
        if (forbid != null) {
            builder.set(FORBID_FIELD, forbid.toJson());
        }
        return builder.build();
    }

    /**
     * Parses a {@code ImmutableThingConfigOverrides} from JSON.
     *
     * @param json the input JSON.
     * @return the parsed instance.
     */
    public static ImmutableThingConfigOverrides fromJson(final JsonObject json) {
        final ImmutableThingEnforceOverrides enforce = json.getValue(ENFORCE_FIELD)
                .filter(JsonValue::isObject)
                .map(JsonValue::asObject)
                .map(ImmutableThingEnforceOverrides::fromJson)
                .orElse(null);

        final ImmutableThingForbidOverrides forbid = json.getValue(FORBID_FIELD)
                .filter(JsonValue::isObject)
                .map(JsonValue::asObject)
                .map(ImmutableThingForbidOverrides::fromJson)
                .orElse(null);

        return of(enforce, forbid);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ImmutableThingConfigOverrides)) return false;
        final ImmutableThingConfigOverrides that = (ImmutableThingConfigOverrides) o;
        return Objects.equals(enforce, that.enforce)
                && Objects.equals(forbid, that.forbid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enforce, forbid);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" +
                "enforce=" + enforce +
                ", forbid=" + forbid +
                "]";
    }
}
