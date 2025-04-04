/*
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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

import static org.eclipse.ditto.things.model.TestConstants.Feature.FLUX_CAPACITOR;
import static org.eclipse.ditto.things.model.TestConstants.Feature.FLUX_CAPACITOR_ID;
import static org.eclipse.ditto.things.model.TestConstants.Thing.THING_V2;

import org.apache.pekko.actor.ActorSystem;
import org.eclipse.ditto.base.model.exceptions.DittoRuntimeException;
import org.eclipse.ditto.base.model.headers.DittoHeaderDefinition;
import org.eclipse.ditto.internal.utils.persistentactors.commands.CommandStrategy;
import org.eclipse.ditto.things.model.ThingId;
import org.eclipse.ditto.things.model.signals.commands.query.RetrieveFeature;
import org.eclipse.ditto.things.model.signals.commands.query.RetrieveFeatureResponse;
import org.eclipse.ditto.things.service.persistence.actors.ETagTestUtils;
import org.junit.Before;
import org.junit.Test;

import com.typesafe.config.ConfigFactory;

/**
 * Unit test for {@link RetrieveFeatureStrategy}.
 */
public final class RetrieveFeatureStrategyTest extends AbstractCommandStrategyTest {

    private RetrieveFeatureStrategy underTest;

    @Before
    public void setUp() {
        final ActorSystem system = ActorSystem.create("test", ConfigFactory.load("test"));
        underTest = new RetrieveFeatureStrategy(system);
    }

    @Test
    public void retrieveExistingFeature() {
        final CommandStrategy.Context<ThingId> context = getDefaultContext();
        final RetrieveFeature command =
                RetrieveFeature.of(context.getState(), FLUX_CAPACITOR_ID, provideHeaders(context));
        final RetrieveFeatureResponse expectedResponse =
                ETagTestUtils.retrieveFeatureResponse(command.getEntityId(), FLUX_CAPACITOR, FLUX_CAPACITOR.toJson(),
                        command.getDittoHeaders().toBuilder()
                                .putHeader(DittoHeaderDefinition.ENTITY_REVISION.getKey(), "41")
                                .build());

        assertQueryResult(underTest, THING_V2, command, expectedResponse);
    }

    @Test
    public void retrieveFeatureFromThingWithoutFeatures() {
        final CommandStrategy.Context<ThingId> context = getDefaultContext();
        final RetrieveFeature command =
                RetrieveFeature.of(context.getState(), FLUX_CAPACITOR_ID, provideHeaders(context));
        final DittoRuntimeException expectedException =
                ExceptionFactory.featureNotFound(command.getEntityId(), command.getFeatureId(),
                        command.getDittoHeaders());

        assertErrorResult(underTest, THING_V2.removeFeatures(), command, expectedException);
    }

    @Test
    public void retrieveNonExistingFeature() {
        final CommandStrategy.Context<ThingId> context = getDefaultContext();
        final RetrieveFeature command =
                RetrieveFeature.of(context.getState(), FLUX_CAPACITOR_ID, provideHeaders(context));
        final DittoRuntimeException expectedException =
                ExceptionFactory.featureNotFound(command.getEntityId(), command.getFeatureId(),
                        command.getDittoHeaders());

        assertErrorResult(underTest, THING_V2.removeFeature(FLUX_CAPACITOR_ID), command, expectedException);
    }

}
