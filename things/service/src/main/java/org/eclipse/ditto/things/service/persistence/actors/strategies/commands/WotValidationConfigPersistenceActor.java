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
package org.eclipse.ditto.things.service.persistence.actors.strategies.commands;

import java.util.Optional;
import javax.annotation.concurrent.Immutable;

import org.apache.pekko.persistence.AbstractPersistentActor;
import org.eclipse.ditto.base.model.entity.id.EntityId;
import org.eclipse.ditto.base.model.entity.type.EntityType;
import org.eclipse.ditto.base.model.entity.metadata.Metadata;
import org.eclipse.ditto.base.model.headers.DittoHeaders;
import org.eclipse.ditto.base.model.signals.commands.Command;
import org.eclipse.ditto.base.model.signals.events.Event;
import org.eclipse.ditto.things.model.ThingId;
import org.eclipse.ditto.things.model.devops.ImmutableWoTValidationConfig;
import org.eclipse.ditto.things.model.devops.commands.DeleteWotValidationConfig;
import org.eclipse.ditto.things.model.devops.commands.ModifyWotValidationConfig;
import org.eclipse.ditto.things.model.devops.events.WotValidationConfigCreated;
import org.eclipse.ditto.things.model.devops.events.WotValidationConfigDeleted;
import org.eclipse.ditto.internal.utils.persistence.mongo.streaming.MongoReadJournal;
import org.eclipse.ditto.internal.utils.pekko.SimpleCommandResponse;
import org.eclipse.ditto.json.JsonValue;
import org.eclipse.ditto.wot.validation.config.TmValidationConfig;
import org.eclipse.ditto.wot.validation.config.ImmutableTmValidationConfig;
import org.eclipse.ditto.things.service.persistence.mongo.MongoWotValidationConfigRepository;
import org.eclipse.ditto.json.JsonObject;

/**
 * Actor which handles persistence operations for WoT validation configs.
 */
@Immutable
public final class WotValidationConfigPersistenceActor extends AbstractPersistentActor {

    private static final String JOURNAL_TAG = "wot-validation-config";
    private static final String RESOURCE_TYPE = "wot-validation-config";
    private final String persistenceId;
    private final MongoReadJournal mongoReadJournal;
    private final WotValidationConfigDData ddata;
    private final MongoWotValidationConfigRepository mongoRepository;
    private ImmutableWoTValidationConfig entity;

    public WotValidationConfigPersistenceActor(
            final String persistenceId,
            final MongoReadJournal mongoReadJournal,
            final WotValidationConfigDData ddata,
            final MongoWotValidationConfigRepository mongoRepository) {
        this.persistenceId = persistenceId;
        this.mongoReadJournal = mongoReadJournal;
        this.ddata = ddata;
        this.mongoRepository = mongoRepository;
        this.entity = null;
    }

    @Override
    public String persistenceId() {
        return persistenceId;
    }

    @Override
    public String journalPluginId() {
        return "pekko-contrib-mongodb-persistence-things-journal";
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Command.class, this::handleCommand)
                .match(Event.class, this::handleEvent)
                .build();
    }

    @Override
    public Receive createReceiveRecover() {
        return receiveBuilder()
                .match(Event.class, this::handleEvent)
                .build();
    }

    private void handleCommand(final Command<?> command) {
        if (command instanceof ModifyWotValidationConfig modifyCommand) {
            final JsonObject configJson = modifyCommand.getConfig();
            final ImmutableWoTValidationConfig config = ImmutableWoTValidationConfig.fromJson(configJson);
            final EntityId entityId = EntityId.of(EntityType.of(RESOURCE_TYPE), modifyCommand.getEntityId().toString());
            
            // Create event
            final WotValidationConfigCreated event = WotValidationConfigCreated.of(
                    entityId,
                    config,
                    null,
                    DittoHeaders.empty(),
                    null
            );
            
            // Persist event
            persist(event, persistedEvent -> {
                entity = config;
                getSender().tell(SimpleCommandResponse.of(
                        command.getDittoHeaders().getCorrelationId().orElse(null),
                        JsonValue.nullLiteral()
                ), getSelf());
            });
        } else if (command instanceof DeleteWotValidationConfig deleteCommand) {
            final EntityId entityId = EntityId.of(EntityType.of(RESOURCE_TYPE), deleteCommand.getEntityId().toString());
            final WotValidationConfigDeleted event = WotValidationConfigDeleted.of(
                    entityId,
                    entity,
                    null,
                    DittoHeaders.empty(),
                    null
            );
            
            // Persist event
            persist(event, persistedEvent -> {
                entity = null;
                getSender().tell(SimpleCommandResponse.of(
                        command.getDittoHeaders().getCorrelationId().orElse(null),
                        JsonValue.nullLiteral()
                ), getSelf());
            });
        } else {
            unhandled(command);
        }
    }

    private void handleEvent(final Event<?> event) {
        if (event instanceof WotValidationConfigCreated created) {
            entity = created.getConfig();
        } else if (event instanceof WotValidationConfigDeleted) {
            entity = null;
        } else {
            unhandled(event);
        }
    }

    private String getJournalTag() {
        return JOURNAL_TAG;
    }

    private Optional<Metadata> calculateRelativeMetadata(final ThingId entityId, final Command<?> command) {
        return Optional.empty();
    }
} 