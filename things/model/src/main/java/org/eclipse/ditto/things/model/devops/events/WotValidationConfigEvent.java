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

import org.eclipse.ditto.base.model.signals.events.Event;
import org.eclipse.ditto.things.model.devops.ImmutableWoTValidationConfig;

/**
 * Interface for all WoT validation config related events.
 */
public interface WotValidationConfigEvent extends Event<WotValidationConfigEvent> {

    /**
     * Returns the WoT validation config of this event.
     *
     * @return the WoT validation config.
     */
    ImmutableWoTValidationConfig getConfig();
} 