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
package org.eclipse.ditto.things.service.persistence.actors.strategies.events;

import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.internal.utils.persistentactors.events.EventStrategy;
import org.eclipse.ditto.things.model.devops.ImmutableWotValidationConfig;
import org.eclipse.ditto.things.model.devops.events.WotValidationConfigCreated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This strategy handles the {@link WotValidationConfigCreated} event.
 */
@Immutable
final class WotValidationConfigCreatedStrategy implements EventStrategy<WotValidationConfigCreated, ImmutableWotValidationConfig> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WotValidationConfigCreatedStrategy.class);

    @Override
    public ImmutableWotValidationConfig handle(final WotValidationConfigCreated event, final ImmutableWotValidationConfig config, final long revision) {
        LOGGER.info("Handling WotValidationConfigCreated event: {} (revision: {})", event, revision);
        final ImmutableWotValidationConfig result = event.getConfig();
        LOGGER.info("Returning config from event: {}", result);
        return result;
    }
} 