/*
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
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
package org.eclipse.ditto.policies.enforcement.config;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.internal.utils.config.KnownConfigValue;

/**
 * Provides configuration settings for an entry of restricting the creation of entities.
 */
@Immutable
public interface CreationRestrictionConfig {

    /**
     * The list of resource types this entry applies to.
     * An empty list would match any.
     *
     * @return the set of resource types
     */
    Set<String> getResourceTypes();

    /**
     * The list of namespace {@link Pattern}s this entry applies to.
     * An empty list would match any. The pattern must match the full string.
     *
     * @return the list of namespace patterns
     */
    List<Pattern> getNamespace();

    /**
     * The list of authentication subject {@link Pattern}s this entry applies to.
     * An empty list would match any.
     *
     * @return the list of auth subject patterns
     */
    List<Pattern> getAuthSubject();

    /**
     * The list of thing definition {@link Pattern}s this entry applies to.
     * This only applies to creation of "thing" resources.
     * An empty list would match any.
     * A list containing a {@code null} entry allows creation of things even if no definition was present.
     *
     * @return the list of thing definition patterns
     */
    List<Pattern> getThingDefinitions();

    /**
     * An enumeration of the known config path expressions and their associated default values for
     * {@code CreationRestrictionConfig}.
     */
    enum CreationRestrictionConfigValues implements KnownConfigValue {
        /**
         * Matching resource types.
         */
        RESOURCE_TYPES("resource-types", Set.of()),

        /**
         * Matching namespaces, supports wildcards.
         */
        NAMESPACES("namespaces", Set.of()),

        /**
         * Matching auth subjects.
         */
        AUTH_SUBJECTS("auth-subjects", Set.of()),

        /**
         * Matching thing definitions - only applicable for {@code resource-type} "thing".
         */
        THING_DEFINITIONS("thing-definitions", Set.of());

        private final String path;
        private final Object defaultValue;

        CreationRestrictionConfigValues(final String thePath, final Object theDefaultValue) {
            path = thePath;
            defaultValue = theDefaultValue;
        }

        @Override
        public String getConfigPath() {
            return path;
        }

        @Override
        public Object getDefaultValue() {
            return defaultValue;
        }

    }
}
