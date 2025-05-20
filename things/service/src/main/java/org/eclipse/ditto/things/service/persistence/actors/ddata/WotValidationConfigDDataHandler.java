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
package org.eclipse.ditto.things.service.persistence.actors.ddata;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;

import org.apache.pekko.actor.ActorRefFactory;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.actor.Address;
import org.apache.pekko.cluster.ddata.Key;
import org.apache.pekko.cluster.ddata.ORMultiMap;
import org.apache.pekko.cluster.ddata.ORMultiMapKey;
import org.apache.pekko.cluster.ddata.ORSet;
import org.apache.pekko.cluster.ddata.Replicator;
import org.apache.pekko.cluster.ddata.SelfUniqueAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.ditto.internal.utils.ddata.DistributedDataConfig;
import org.eclipse.ditto.internal.utils.pubsub.ddata.AbstractDDataHandler;
import org.eclipse.ditto.things.model.devops.WotValidationConfigId;

/**
 * A distributed collection of WoT validation configs.
 */
public final class WotValidationConfigDDataHandler extends AbstractDDataHandler<WotValidationConfigId, String, WotValidationConfigUpdate> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WotValidationConfigDDataHandler.class);
    private static final String TOPIC_TYPE = "wot-validation-config";
    private static final String KEY_PREFIX = "wot-validation-config:";

    private WotValidationConfigDDataHandler(final DistributedDataConfig config,
            final ActorRefFactory actorRefFactory,
            final ActorSystem actorSystem,
            final Executor ddataExecutor) {
        super(config, actorRefFactory, actorSystem, ddataExecutor, TOPIC_TYPE);
    }

    /**
     * Start distributed-data replicator for WoT validation configs under an actor system's user guardian using the default
     * dispatcher.
     *
     * @param system the actor system.
     * @param ddataConfig the distributed data config.
     * @return access to the distributed data.
     */
    public static WotValidationConfigDDataHandler create(final ActorSystem system,
            final DistributedDataConfig ddataConfig) {
        return new WotValidationConfigDDataHandler(ddataConfig, system, system, system.dispatcher());
    }

    @Override
    public long approximate(final String topic) {
        return topic.hashCode();
    }

    @Override
    public CompletionStage<Void> removeAddress(final Address address,
            final Replicator.WriteConsistency writeConsistency) {
        return update(getKey(address), writeConsistency, mmap -> {
            ORMultiMap<WotValidationConfigId, String> result = mmap;
            for (final WotValidationConfigId configId : mmap.getEntries().keySet()) {
                if (configId.toString().contains(address.toString())) {
                    result = result.remove(selfUniqueAddress, configId);
                }
            }
            return result;
        });
    }

    private static <K, V> Key<ORMultiMap<K, V>> createTypedORMultiMapKey(String id) {
        @SuppressWarnings("unchecked")
        Key<ORMultiMap<K, V>> key = (Key<ORMultiMap<K, V>>) (Key<?>) ORMultiMapKey.create(id);
        return key;
    }

    @Override
    public Key<ORMultiMap<WotValidationConfigId, String>> getKey(final int shardNumber) {
        return createTypedORMultiMapKey(KEY_PREFIX + shardNumber);
    }

    @Override
    public Key<ORMultiMap<WotValidationConfigId, String>> getKey(final Address address) {
        // Use the address's hashCode to determine the shard number
        final int shardNumber = Math.abs(address.hashCode() % getNumberOfShards());
        return getKey(shardNumber);
    }

    @Override
    public CompletionStage<Void> put(WotValidationConfigId key, WotValidationConfigUpdate update, Replicator.WriteConsistency consistency) {
        LOGGER.info("Updating DData for key {}: inserts={}, deletes={}", key, update.getInserts(), update.getDeletes());
        update.getInserts().forEach(insert -> LOGGER.debug("DData insert payload for key {}: {}", key, insert));
        update.getDeletes().forEach(delete -> LOGGER.debug("DData delete payload for key {}: {}", key, delete));
        try {
            return super.put(key, update, consistency);
        } catch (Exception e) {
            LOGGER.error("Exception during DData put for key {}: {}", key, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public CompletionStage<Void> removeSubscriber(WotValidationConfigId subscriber, Replicator.WriteConsistency consistency) {
        LOGGER.info("Removing DData subscriber for key {}", subscriber);
        return super.removeSubscriber(subscriber, consistency);
    }

    /**
     * Get the current validation configs from the local replica.
     *
     * @return future that completes with the current validation configs.
     */
    public CompletionStage<ORMultiMap<WotValidationConfigId, String>> getConfigs() {
        return get(getKey(0), (Replicator.ReadConsistency) Replicator.readLocal())
                .thenApply(maybeORSet -> maybeORSet.orElse(ORMultiMap.empty()));
    }
} 