/*
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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

import org.eclipse.ditto.things.model.Thing;
import org.eclipse.ditto.things.model.ThingBuilder;
import org.eclipse.ditto.things.model.signals.events.WotValidationConfigDeleted;

/**
 * This strategy handles the {@link WotValidationConfigDeleted} event.
 */
@Immutable
final class WotValidationConfigDeletedStrategy extends AbstractThingEventStrategy<WotValidationConfigDeleted> {

    @Override
    protected ThingBuilder.FromCopy applyEvent(final WotValidationConfigDeleted event,
            final ThingBuilder.FromCopy thingBuilder) {
        // Since we're not storing the validation config in the Thing itself,
        // we just return the builder without modifications
        return thingBuilder;
    }
} 