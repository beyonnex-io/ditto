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
package org.eclipse.ditto.things.service.persistence.serializer;

import org.apache.pekko.actor.ExtendedActorSystem;
import org.apache.pekko.persistence.journal.Tagged;
import org.bson.BsonDocument;
import org.eclipse.ditto.base.model.json.FieldType;
import org.eclipse.ditto.base.model.json.JsonSchemaVersion;
import org.eclipse.ditto.base.model.signals.events.Event;
import org.eclipse.ditto.base.model.signals.events.EventsourcedEvent;
import org.eclipse.ditto.base.model.signals.events.GlobalEventRegistry;
import org.eclipse.ditto.base.service.config.DittoServiceConfig;
import org.eclipse.ditto.internal.utils.config.DefaultScopedConfig;
import org.eclipse.ditto.internal.utils.persistence.mongo.AbstractMongoEventAdapter;
import org.eclipse.ditto.internal.utils.persistence.mongo.DittoBsonJson;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonObjectBuilder;
import org.eclipse.ditto.things.model.devops.events.WotValidationConfigEvent;
import org.eclipse.ditto.things.model.devops.events.JsonFields;
import org.eclipse.ditto.things.service.common.config.DefaultThingConfig;

import java.util.Collections;

/**
 * EventAdapter for {@link WotValidationConfigEvent}s persisted into pekko-persistence event-journal.
 * Converts Event to MongoDB BSON objects and vice versa.
 */
public final class WotValidationConfigMongoEventAdapter extends AbstractMongoEventAdapter<Event<?>> {

    private static final String JOURNAL_TAG = "wot-validation-config";

    public WotValidationConfigMongoEventAdapter(final ExtendedActorSystem system) {
        super(system, GlobalEventRegistry.getInstance(), DefaultThingConfig.of(
                DittoServiceConfig.of(DefaultScopedConfig.dittoScoped(system.settings().config()), "things")
        ).getEventConfig());
    }

    @Override
    public Object toJournal(final Object event) {
        if (event instanceof Event<?> theEvent) {
            final JsonSchemaVersion schemaVersion = theEvent.getImplementedSchemaVersion();
            final JsonObject jsonObject = performToJournalMigration(theEvent,
                    theEvent.toJson(schemaVersion, FieldType.regularOrSpecial())
            ).build();
            final BsonDocument bson = DittoBsonJson.getInstance().parse(jsonObject);
            return new Tagged(bson, Collections.singleton(JOURNAL_TAG));
        } else {
            throw new IllegalArgumentException("Unable to toJournal a non-'Event' object! Was: " + event.getClass());
        }
    }

    @Override
    protected JsonObjectBuilder performToJournalMigration(final Event<?> event, final JsonObject jsonObject) {
        final JsonObjectBuilder builder = super.performToJournalMigration(event, jsonObject);
        if (event instanceof EventsourcedEvent) {
            builder.set(JsonFields.SCOPE_ID.getPointer().toString(), ((EventsourcedEvent<?>) event).getEntityId().toString());
        }
        return builder;
    }
} 