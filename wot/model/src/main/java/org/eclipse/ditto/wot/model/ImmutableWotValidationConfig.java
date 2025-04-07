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
package org.eclipse.ditto.wot.model;

import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonField;
import org.eclipse.ditto.json.JsonFactory;

import java.util.Objects;

/**
 * An immutable implementation of {@link WotValidationConfig}.
 */
public final class ImmutableWotValidationConfig implements WotValidationConfig {

    private final JsonObject config;

    private ImmutableWotValidationConfig(final JsonObject config) {
        this.config = Objects.requireNonNull(config, "config");
    }

    /**
     * Creates a new {@code ImmutableWotValidationConfig} from the given JSON object.
     *
     * @param jsonObject the JSON object of which the config is to be created.
     * @return the config.
     * @throws NullPointerException if {@code jsonObject} is {@code null}.
     */
    public static ImmutableWotValidationConfig fromJson(final JsonObject jsonObject) {
        return new ImmutableWotValidationConfig(jsonObject);
    }

    @Override
    public JsonObject toJson() {
        return config;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ImmutableWotValidationConfig that = (ImmutableWotValidationConfig) o;
        return Objects.equals(config, that.config);
    }

    @Override
    public int hashCode() {
        return Objects.hash(config);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" +
                "config=" + config +
                "]";
    }
} 