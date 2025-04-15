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
package org.eclipse.ditto.things.model.devops.commands;

import java.util.Objects;

import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.base.model.entity.id.EntityId;
import org.eclipse.ditto.base.model.entity.type.EntityType;

/**
 * ID of a WoT validation config.
 */
@Immutable
public final class WotValidationConfigId implements EntityId {

    private static final String ID = "wot:validation:config";
    private static final EntityType ENTITY_TYPE = EntityType.of("wot.validation.config");

    private WotValidationConfigId() {
        // No-op
    }

    /**
     * Returns a new instance of {@code WotValidationConfigId}.
     *
     * @return the instance.
     */
    public static WotValidationConfigId getInstance() {
        return new WotValidationConfigId();
    }

    @Override
    public EntityType getEntityType() {
        return ENTITY_TYPE;
    }

    @Override
    public String toString() {
        return ID;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, ENTITY_TYPE);
    }
} 