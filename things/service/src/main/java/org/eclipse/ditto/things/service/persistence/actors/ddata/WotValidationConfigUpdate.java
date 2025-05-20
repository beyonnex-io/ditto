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
package org.eclipse.ditto.things.service.persistence.actors.ddata;

import java.util.Set;

import org.eclipse.ditto.internal.utils.pubsub.ddata.DDataUpdate;

/**
 * Update for WoT validation config distributed data.
 */
public final class WotValidationConfigUpdate implements DDataUpdate<String> {

    private final Set<String> inserts;
    private final Set<String> deletes;

    public WotValidationConfigUpdate(Set<String> inserts, Set<String> deletes) {
        this.inserts = inserts;
        this.deletes = deletes;
    }

    @Override
    public Set<String> getInserts() {
        return inserts;
    }

    @Override
    public Set<String> getDeletes() {
        return deletes;
    }

    @Override
    public DDataUpdate<String> diff(DDataUpdate<String> previousState) {
        // Implement diff logic if needed
        return this;
    }

    @Override
    public boolean isEmpty() {
        return inserts.isEmpty() && deletes.isEmpty();
    }
} 