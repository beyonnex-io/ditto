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

import static org.eclipse.ditto.things.model.TestConstants.Thing.THING_V2;

import org.apache.pekko.actor.ActorSystem;
import org.eclipse.ditto.base.model.exceptions.DittoRuntimeException;
import org.eclipse.ditto.internal.utils.persistentactors.commands.CommandStrategy;
import org.eclipse.ditto.things.model.TestConstants;
import org.eclipse.ditto.things.model.ThingId;
import org.eclipse.ditto.things.model.signals.commands.modify.DeleteFeature;
import org.eclipse.ditto.things.model.signals.commands.modify.DeleteFeatureResponse;
import org.eclipse.ditto.things.model.signals.events.FeatureDeleted;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.typesafe.config.ConfigFactory;

/**
 * Unit test for {@link DeleteFeatureStrategy}.
 */
public final class DeleteFeatureStrategyTest extends AbstractCommandStrategyTest {

    private static String featureId;

    private DeleteFeatureStrategy underTest;

    @BeforeClass
    public static void initTestFixture() {
        featureId = TestConstants.Feature.FLUX_CAPACITOR_ID;
    }

    @Before
    public void setUp() {
        final ActorSystem system = ActorSystem.create("test", ConfigFactory.load("test"));
        underTest = new DeleteFeatureStrategy(system);
    }

    @Test
    public void successfullyDeleteFeatureFromThing() {
        final CommandStrategy.Context<ThingId> context = getDefaultContext();
        final DeleteFeature command = DeleteFeature.of(context.getState(), featureId, provideHeaders(context));

        assertStagedModificationResult(underTest, THING_V2, command, FeatureDeleted.class,
                DeleteFeatureResponse.of(context.getState(), command.getFeatureId(), command.getDittoHeaders()));
    }

    @Test
    public void deleteFeatureFromThingWithoutFeatures() {
        final CommandStrategy.Context<ThingId> context = getDefaultContext();
        final DeleteFeature command = DeleteFeature.of(context.getState(), featureId, provideHeaders(context));
        final DittoRuntimeException expectedException =
                ExceptionFactory.featureNotFound(context.getState(), command.getFeatureId(),
                        command.getDittoHeaders());

        assertErrorResult(underTest, THING_V2.removeFeatures(), command, expectedException);
    }

    @Test
    public void deleteFeatureFromThingWithoutThatFeature() {
        final CommandStrategy.Context<ThingId> context = getDefaultContext();
        final DeleteFeature command = DeleteFeature.of(context.getState(), "myFeature", provideHeaders(context));
        final DittoRuntimeException expectedException =
                ExceptionFactory.featureNotFound(context.getState(), command.getFeatureId(),
                        command.getDittoHeaders());

        assertErrorResult(underTest, THING_V2, command, expectedException);
    }

}
