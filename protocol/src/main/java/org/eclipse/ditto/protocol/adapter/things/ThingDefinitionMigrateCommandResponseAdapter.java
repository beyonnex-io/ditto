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
package org.eclipse.ditto.protocol.adapter.things;

import static java.util.Objects.requireNonNull;

import org.eclipse.ditto.base.model.headers.translator.HeaderTranslator;
import org.eclipse.ditto.protocol.Adaptable;
import org.eclipse.ditto.protocol.TopicPath;
import org.eclipse.ditto.protocol.adapter.MigrateThingDefinitionCommandResponseAdapter;
import org.eclipse.ditto.protocol.mapper.SignalMapperFactory;
import org.eclipse.ditto.protocol.mappingstrategies.MappingStrategiesFactory;
import org.eclipse.ditto.things.model.signals.commands.modify.MigrateThingDefinitionResponse;

/**
 * Adapter for mapping a {@link org.eclipse.ditto.things.model.signals.commands.modify.MigrateThingDefinition}
 * to and from an {@link org.eclipse.ditto.protocol.Adaptable}.
 */
final class ThingDefinitionMigrateCommandResponseAdapter extends AbstractThingAdapter<MigrateThingDefinitionResponse>
        implements MigrateThingDefinitionCommandResponseAdapter {

    private ThingDefinitionMigrateCommandResponseAdapter(final HeaderTranslator headerTranslator) {
        super(MappingStrategiesFactory.getThingDefinitionMigrateCommandResponseMappingStrategies(),
                SignalMapperFactory.newThingDefinitionMigrateResponseSignalMapper(),
                headerTranslator);
    }

    /**
     * Returns a new ThingDefinitionMigrateCommandResponseAdapter.
     *
     * @param headerTranslator translator between external and Ditto headers.
     * @return the adapter.
     */
    public static ThingDefinitionMigrateCommandResponseAdapter of(final HeaderTranslator headerTranslator) {
        return new ThingDefinitionMigrateCommandResponseAdapter(requireNonNull(headerTranslator));
    }

    @Override
    protected String getType(final Adaptable adaptable) {
       return MigrateThingDefinitionResponse.TYPE;
    }

    @Override
    protected String getTypeCriterionAsString(final TopicPath topicPath) {
        return RESPONSES_CRITERION;
    }
}
