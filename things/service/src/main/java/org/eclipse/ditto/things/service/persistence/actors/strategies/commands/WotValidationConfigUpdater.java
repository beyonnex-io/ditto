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
package org.eclipse.ditto.things.service.persistence.actors.strategies.commands;

import org.eclipse.ditto.base.model.entity.id.EntityId;
import org.eclipse.ditto.base.model.exceptions.DittoInternalErrorException;
import org.eclipse.ditto.base.model.exceptions.DittoRuntimeException;
import org.eclipse.ditto.base.model.signals.commands.Command;
import org.eclipse.ditto.internal.utils.pekko.logging.DittoLoggerFactory;
import org.eclipse.ditto.internal.utils.cluster.DistPubSubAccess;
import org.eclipse.ditto.things.model.devops.ImmutableWoTValidationConfig;
import org.eclipse.ditto.things.model.devops.events.WotValidationConfigCreated;
import org.eclipse.ditto.things.model.devops.events.WotValidationConfigDeleted;
import org.eclipse.ditto.things.model.devops.events.WotValidationConfigEvent;
import org.eclipse.ditto.things.service.persistence.mongo.MongoWotValidationConfigRepository;
import org.eclipse.ditto.internal.utils.pekko.SimpleCommandResponse;
import org.eclipse.ditto.json.JsonFactory;
import org.eclipse.ditto.wot.validation.config.ImmutableTmValidationConfig;
import org.eclipse.ditto.wot.validation.config.TmValidationConfig;

import org.apache.pekko.actor.AbstractActor;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.Props;
import org.apache.pekko.cluster.pubsub.DistributedPubSubMediator;
import org.apache.pekko.event.LoggingAdapter;
import org.apache.pekko.event.Logging;
import org.apache.pekko.japi.pf.ReceiveBuilder;

import java.util.concurrent.CompletionStage;

/**
 * Actor which updates the distributed data cache for WoT validation configurations.
 */
public final class WotValidationConfigUpdater extends AbstractActor {

    /**
     * The name of this actor.
     */
    public static final String ACTOR_NAME = "wotValidationConfigUpdater";

    private final LoggingAdapter log = Logging.apply(this);
    private final WotValidationConfigDData ddata;
    private final MongoWotValidationConfigRepository mongoRepository;
    private final ActorRef pubSubMediator;

    private WotValidationConfigUpdater(final WotValidationConfigDData ddata,
            final MongoWotValidationConfigRepository mongoRepository,
            final ActorRef pubSubMediator) {
        this.ddata = ddata;
        this.mongoRepository = mongoRepository;
        this.pubSubMediator = pubSubMediator;
    }

    /**
     * Creates Pekko configuration object Props for this WotValidationConfigUpdater.
     *
     * @param ddata the distributed data handler for WoT validation configs.
     * @param mongoRepository the MongoDB repository for WoT validation configs.
     * @param pubSubMediator the pub/sub mediator Actor ref.
     * @return the Pekko configuration Props object.
     */
    public static Props props(final WotValidationConfigDData ddata,
            final MongoWotValidationConfigRepository mongoRepository,
            final ActorRef pubSubMediator) {
        return Props.create(WotValidationConfigUpdater.class, ddata, mongoRepository, pubSubMediator);
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(WotValidationConfigCreated.class, this::handleCreated)
                .match(WotValidationConfigDeleted.class, this::handleDeleted)
                .match(DistributedPubSubMediator.SubscribeAck.class, subscribeAck ->
                        log.info("Got SubscribeAck <{}>", subscribeAck))
                .matchAny(m -> {
                    log.warning("Got unknown message: {}", m);
                    unhandled(m);
                })
                .build();
    }

    private void handleCreated(final WotValidationConfigCreated event) {
        final ImmutableWoTValidationConfig config = event.getConfig();
        final TmValidationConfig tmConfig = ImmutableTmValidationConfig.of(
                event.getEntityId().toString(),
                config.isEnabled(),
                config.isLogWarningInsteadOfFailing(),
                null,
                null
        );
        final CompletionStage<Void> mongoFuture = mongoRepository.create(tmConfig);
        final CompletionStage<Void> ddataFuture = ddata.add(tmConfig);
        
        mongoFuture.thenCompose(v -> ddataFuture)
                .whenComplete((result, error) -> {
                    if (error != null) {
                        log.error(error, "Failed to handle WotValidationConfigCreated event");
                        getSender().tell(DittoRuntimeException.asDittoRuntimeException(error, t ->
                                DittoInternalErrorException.newBuilder()
                                        .message("Failed to handle WotValidationConfigCreated event")
                                        .cause(t)
                                        .build()), getSelf());
                    } else {
                        getSender().tell(SimpleCommandResponse.of(null, JsonFactory.newValue(true)), getSelf());
                    }
                });
    }

    private void handleDeleted(final WotValidationConfigDeleted event) {
        final EntityId entityId = event.getEntityId();
        final TmValidationConfig tmConfig = ImmutableTmValidationConfig.of(
                entityId.toString(),
                false,
                false,
                null,
                null
        );
        final CompletionStage<Void> mongoFuture = mongoRepository.delete(tmConfig);
        final CompletionStage<Void> ddataFuture = ddata.remove(tmConfig);
        
        mongoFuture.thenCompose(v -> ddataFuture)
                .whenComplete((result, error) -> {
                    if (error != null) {
                        log.error(error, "Failed to handle WotValidationConfigDeleted event");
                        getSender().tell(DittoRuntimeException.asDittoRuntimeException(error, t ->
                                DittoInternalErrorException.newBuilder()
                                        .message("Failed to handle WotValidationConfigDeleted event")
                                        .cause(t)
                                        .build()), getSelf());
                    } else {
                        getSender().tell(SimpleCommandResponse.of(null, JsonFactory.newValue(true)), getSelf());
                    }
                });
    }
} 