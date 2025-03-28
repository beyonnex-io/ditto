/*
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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
package org.eclipse.ditto.things.service.persistence.actors.strategies.commands;

import static org.eclipse.ditto.things.model.TestConstants.Thing.DEFINITION;
import static org.eclipse.ditto.things.model.TestConstants.Thing.THING_V2;

import org.apache.pekko.actor.ActorSystem;
import org.eclipse.ditto.base.model.headers.DittoHeaderDefinition;
import org.eclipse.ditto.internal.utils.persistentactors.commands.CommandStrategy;
import org.eclipse.ditto.things.model.ThingId;
import org.eclipse.ditto.things.model.signals.commands.exceptions.ThingDefinitionNotAccessibleException;
import org.eclipse.ditto.things.model.signals.commands.query.RetrieveThingDefinition;
import org.eclipse.ditto.things.model.signals.commands.query.RetrieveThingDefinitionResponse;
import org.eclipse.ditto.things.service.persistence.actors.ETagTestUtils;
import org.junit.Before;
import org.junit.Test;

import com.typesafe.config.ConfigFactory;

/**
 * Unit test for {@link RetrieveThingDefinitionStrategy}.
 */
public final class RetrieveThingDefinitionStrategyTest extends AbstractCommandStrategyTest {

    private RetrieveThingDefinitionStrategy underTest;

    @Before
    public void setUp() {
        final ActorSystem system = ActorSystem.create("test", ConfigFactory.load("test"));
        underTest = new RetrieveThingDefinitionStrategy(system);
    }

    @Test
    public void retrieveExistingDefinition() {
        final CommandStrategy.Context<ThingId> context = getDefaultContext();
        final RetrieveThingDefinition command = RetrieveThingDefinition.of(context.getState(), provideHeaders(context));
        final RetrieveThingDefinitionResponse expectedResponse =
                ETagTestUtils.retrieveDefinitionResponse(command.getEntityId(), DEFINITION, provideHeaders(context)
                        .toBuilder()
                        .putHeader(DittoHeaderDefinition.ENTITY_REVISION.getKey(), "41")
                        .build());

        assertQueryResult(underTest, THING_V2, command, expectedResponse);
    }

    @Test
    public void retrieveNonExistingDefinition() {
        final CommandStrategy.Context<ThingId> context = getDefaultContext();
        final RetrieveThingDefinition command = RetrieveThingDefinition.of(context.getState(), provideHeaders(context));
        final ThingDefinitionNotAccessibleException expectedException =
                ThingDefinitionNotAccessibleException.newBuilder(command.getEntityId())
                        .dittoHeaders(command.getDittoHeaders())
                        .build();

        assertErrorResult(underTest, THING_V2.toBuilder().removeDefinition().build(), command, expectedException);
    }

}
