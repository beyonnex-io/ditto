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

import java.util.concurrent.CompletionStage;

import org.eclipse.ditto.internal.utils.ddata.DistributedData;
import org.eclipse.ditto.internal.utils.ddata.DistributedDataConfig;
import org.eclipse.ditto.wot.validation.config.TmValidationConfig;

import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.actor.ExtendedActorSystem;
import org.apache.pekko.cluster.Cluster;
import org.apache.pekko.cluster.ddata.Key;
import org.apache.pekko.cluster.ddata.ORSet;
import org.apache.pekko.cluster.ddata.ORSetKey;
import org.apache.pekko.cluster.ddata.Replicator;
import org.apache.pekko.cluster.ddata.SelfUniqueAddress;

/**
 * Distributed data for WoT validation configuration.
 */
public final class WotValidationConfigDData extends DistributedData<ORSet<TmValidationConfig>> {
    
    /**
     * Role of cluster members to which this distributed data is replicated.
     */
    public static final String CLUSTER_ROLE = "wot-validation-config-aware";
    
    /**
     * Name of the replicator actor.
     */
    public static final String ACTOR_NAME = "wotValidationConfigReplicator";
    
    /**
     * Key of the distributed data. Should be unique among ORSets.
     */
    private static final Key<ORSet<TmValidationConfig>> KEY = 
        ORSetKey.create("WotValidationConfig");
    
    private final SelfUniqueAddress selfUniqueAddress;
    
    private WotValidationConfigDData(final DistributedDataConfig config, final ActorSystem system) {
        super(config, system, system.dispatcher());
        selfUniqueAddress = SelfUniqueAddress.apply(Cluster.get(system).selfUniqueAddress());
    }
    
    /**
     * Get an instance of this distributed data with the default configuration.
     *
     * @param system the actor system where the replicator actor will be created.
     * @return a new instance of the distributed data.
     */
    public static WotValidationConfigDData of(final ActorSystem system) {
        return Provider.INSTANCE.get(system);
    }
    
    /**
     * Create an instance of this distributed data with special configuration.
     *
     * @param config the overriding configuration.
     * @param system the actor system where the replicator actor will be created.
     * @return a new instance of the distributed data.
     */
    public static WotValidationConfigDData create(final DistributedDataConfig config, final ActorSystem system) {
        return new WotValidationConfigDData(config, system);
    }
    
    /**
     * Add a validation config to ALL replicas.
     *
     * @param config the validation config to add.
     * @return future that completes after the update propagates to all replicas.
     */
    public CompletionStage<Void> add(final TmValidationConfig config) {
        return update(getKey(0), writeAll(), orSet -> orSet.add(selfUniqueAddress, config));
    }
    
    /**
     * Remove a validation config from ALL replicas.
     *
     * @param config the validation config to remove.
     * @return future that completes after the removal propagates to all replicas.
     */
    public CompletionStage<Void> remove(final TmValidationConfig config) {
        return update(getKey(0), writeAll(), orSet -> orSet.remove(selfUniqueAddress, config));
    }
    
    /**
     * Get the current validation configs from the local replica.
     *
     * @return future that completes with the current validation configs.
     */
    public CompletionStage<ORSet<TmValidationConfig>> getConfigs() {
        return get(getKey(0), (Replicator.ReadConsistency) Replicator.readLocal())
                .thenApply(maybeORSet -> maybeORSet.orElse(ORSet.empty()));
    }
    
    @Override
    protected Key<ORSet<TmValidationConfig>> getKey(final int shardNumber) {
        return KEY;
    }
    
    @Override
    protected ORSet<TmValidationConfig> getInitialValue() {
        return ORSet.empty();
    }
    
    private Replicator.WriteConsistency writeAll() {
        return new Replicator.WriteAll(writeTimeout);
    }
    
    private static final class Provider
            extends DistributedData.AbstractDDataProvider<ORSet<TmValidationConfig>, WotValidationConfigDData> {
        
        private static final Provider INSTANCE = new Provider();
        
        private Provider() {}
        
        @Override
        public WotValidationConfigDData createExtension(final ExtendedActorSystem system) {
            return new WotValidationConfigDData(
                    DistributedData.createConfig(system, ACTOR_NAME, CLUSTER_ROLE),
                    system
            );
        }
    }
} 