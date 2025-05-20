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
package org.eclipse.ditto.things.service.persistence.actors;

import org.apache.pekko.actor.*;
import org.apache.pekko.japi.pf.FI;
import org.apache.pekko.japi.pf.ReceiveBuilder;
import org.eclipse.ditto.base.model.exceptions.DittoRuntimeExceptionBuilder;
import org.eclipse.ditto.base.model.signals.commands.streaming.SubscribeForPersistedEvents;
import org.eclipse.ditto.base.model.signals.events.Event;
import org.eclipse.ditto.things.model.devops.WotValidationConfigId;
import org.eclipse.ditto.things.model.devops.commands.WotValidationConfigCommand;
import org.eclipse.ditto.things.model.devops.exceptions.WotValidationConfigNotAccessibleException;
import org.eclipse.ditto.things.model.signals.commands.ThingErrorResponse;
import org.eclipse.ditto.internal.utils.persistence.mongo.streaming.MongoReadJournal;
import org.eclipse.ditto.internal.utils.persistentactors.AbstractPersistenceSupervisor;
import org.eclipse.ditto.internal.utils.namespaces.BlockedNamespaces;
import org.eclipse.ditto.base.service.config.supervision.ExponentialBackOffConfig;
import org.eclipse.ditto.base.service.config.supervision.LocalAskTimeoutConfig;
import org.eclipse.ditto.base.service.actors.ShutdownBehaviour;
import org.eclipse.ditto.internal.utils.cluster.StopShardedActor;
import org.eclipse.ditto.internal.utils.config.DefaultScopedConfig;
import org.eclipse.ditto.things.service.common.config.DittoThingsConfig;
import org.eclipse.ditto.internal.utils.pekko.logging.DittoLoggerFactory;
import org.apache.pekko.event.LoggingAdapter;
import org.apache.pekko.event.Logging;

import javax.annotation.Nullable;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;

/**
 * Supervisor for WotValidationConfigPersistenceActor in sharding.
 * <p>
 * If the child terminates, it will wait for the calculated exponential back-off time and restart it afterwards.
 * Between the termination of the child and the restart, this actor answers to all requests with a
 * {@link WotValidationConfigNotAccessibleException} as fail fast strategy.
 * </p>
 */
public final class WotValidationConfigSupervisorActor extends AbstractActorWithTimers {

    private final ActorRef pubSubMediator;
    private final MongoReadJournal mongoReadJournal;
    private final Duration shutdownTimeout;
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private ActorRef persistenceActorChild;
    private long opCounter = 0;

    @SuppressWarnings("unused")
    private WotValidationConfigSupervisorActor(final ActorRef pubSubMediator,
            final MongoReadJournal mongoReadJournal) {
        this.pubSubMediator = pubSubMediator;
        this.mongoReadJournal = mongoReadJournal;

        final var system = getContext().getSystem();
        final var scopedConfig = DefaultScopedConfig.dittoScoped(system.settings().config());
        final var thingsConfig = DittoThingsConfig.of(scopedConfig);
        shutdownTimeout = thingsConfig.getThingConfig().getShutdownTimeout();
    }

    /**
     * Props for creating a {@code WotValidationConfigSupervisorActor}.
     * <p>
     * Exceptions in the child are handled with a supervision strategy that stops the child
     * for {@link ActorKilledException}'s and escalates all others.
     * </p>
     *
     * @param pubSubMediator the pub/sub mediator ActorRef.
     * @param mongoReadJournal the ReadJournal used for gaining access to historical values.
     * @return the {@link Props} to create this actor.
     */
    public static Props props(final ActorRef pubSubMediator,
            final MongoReadJournal mongoReadJournal) {
        return Props.create(WotValidationConfigSupervisorActor.class, pubSubMediator, mongoReadJournal);
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(WotValidationConfigCommand.class, this::handleWotValidationConfigCommand)
                .match(StopShardedActor.class, this::stopShardedActor)
                .matchEquals(Control.SHUTDOWN_TIMEOUT, this::shutdownActor)
                .match(Terminated.class, this::handleTerminated)
                .matchAny(this::unhandled)
                .build();
    }

    private void handleWotValidationConfigCommand(final WotValidationConfigCommand<?> command) {
        if (null != persistenceActorChild) {
            persistenceActorChild.forward(command, getContext());
        } else {
            final WotValidationConfigNotAccessibleException exception = WotValidationConfigNotAccessibleException
                    .newBuilder(command.getEntityId())
                    .dittoHeaders(command.getDittoHeaders())
                    .build();
            getSender().tell(ThingErrorResponse.of(exception), getSelf());
        }
    }

    @Override
    public void preStart() {
        try {
            final WotValidationConfigId entityId = getEntityId();
            persistenceActorChild = getContext().actorOf(getPersistenceActorProps(entityId), "persistence");
            getContext().watch(persistenceActorChild);
        } catch (final Exception e) {
            log.error(e, "Failed to start persistence actor");
            getContext().stop(getSelf());
        }
    }

    @Override
    public void postStop() {
        if (persistenceActorChild != null) {
            getContext().unwatch(persistenceActorChild);
            getContext().stop(persistenceActorChild);
        }
    }

    protected long getOpCounter() {
        return opCounter;
    }

    protected void incrementOpCounter() {
        opCounter++;
    }

    protected void decrementOpCounter() {
        opCounter--;
    }

    protected WotValidationConfigId getEntityId() throws Exception {
        return WotValidationConfigId.of(URLDecoder.decode(getSelf().path().name(), StandardCharsets.UTF_8));
    }

    protected Props getPersistenceActorProps(final WotValidationConfigId entityId) {
        return WotValidationConfigPersistenceActor.props(entityId, mongoReadJournal, pubSubMediator);
    }

    protected Props getPersistenceEnforcerProps(final WotValidationConfigId entityId) {
        // No persistence enforcer needed for WoT validation config
        return Props.empty();
    }

    protected ShutdownBehaviour getShutdownBehaviour(final WotValidationConfigId entityId) {
        return ShutdownBehaviour.fromId(entityId, pubSubMediator, getSelf());
    }

    protected DittoRuntimeExceptionBuilder<?> getUnavailableExceptionBuilder(@Nullable final WotValidationConfigId entityId) {
        return WotValidationConfigNotAccessibleException.newBuilder(
                Objects.requireNonNullElseGet(entityId, () -> WotValidationConfigId.of("UNKNOWN:ID")));
    }

    protected ExponentialBackOffConfig getExponentialBackOffConfig() {
        final var system = getContext().getSystem();
        final var scopedConfig = DefaultScopedConfig.dittoScoped(system.settings().config());
        final var thingsConfig = DittoThingsConfig.of(scopedConfig);
        return thingsConfig.getThingConfig().getSupervisorConfig().getExponentialBackOffConfig();
    }

    protected LocalAskTimeoutConfig getLocalAskTimeoutConfig() {
        final var system = getContext().getSystem();
        final var scopedConfig = DefaultScopedConfig.dittoScoped(system.settings().config());
        final var thingsConfig = DittoThingsConfig.of(scopedConfig);
        return thingsConfig.getThingConfig().getSupervisorConfig().getLocalAskTimeoutConfig();
    }

    protected boolean applyPersistedEventFilter(final Event<?> event, final SubscribeForPersistedEvents subscribeForPersistedEvents) {
        // No event filtering needed for WoT validation config
        return true;
    }

    protected void stopShardedActor(final StopShardedActor trigger) {
        if (getOpCounter() > 0) {
            getTimers().startSingleTimer(Control.SHUTDOWN_TIMEOUT, Control.SHUTDOWN_TIMEOUT, shutdownTimeout);
        }
    }

    private void shutdownActor(final Control shutdown) {
        log.warning("Shutdown timeout <{}> reached; aborting <{}> ops and stopping myself", shutdownTimeout,
                getOpCounter());
        getContext().stop(getSelf());
    }

    private void handleTerminated(final Terminated terminated) {
        if (terminated.getActor().equals(persistenceActorChild)) {
            log.warning("Persistence actor terminated, will restart after backoff");
            persistenceActorChild = null;
            // Schedule restart after backoff
            final ExponentialBackOffConfig backOffConfig = getExponentialBackOffConfig();
            final Duration backOff = Duration.ofMillis(backOffConfig.getMin().toMillis());
            getTimers().startSingleTimer("restart-persistence", Control.RESTART_PERSISTENCE, backOff);
        }
    }

    private void restartPersistenceActor() {
        try {
            final WotValidationConfigId entityId = getEntityId();
            persistenceActorChild = getContext().actorOf(getPersistenceActorProps(entityId), "persistence");
            getContext().watch(persistenceActorChild);
            log.info("Restarted persistence actor for entity {}", entityId);
        } catch (final Exception e) {
            log.error(e, "Failed to restart persistence actor");
            getContext().stop(getSelf());
        }
    }

    private enum Control {
        SHUTDOWN_TIMEOUT,
        RESTART_PERSISTENCE
    }
} 