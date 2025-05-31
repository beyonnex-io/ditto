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

import java.util.concurrent.CompletionStage;

import org.eclipse.ditto.internal.utils.ddata.DistributedData;
import org.eclipse.ditto.internal.utils.ddata.DistributedDataConfig;
import org.eclipse.ditto.things.model.devops.ImmutableWotValidationConfig;

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
public final class WotValidationConfigDData extends DistributedData<ORSet<ImmutableWotValidationConfig>> {

    /**
     * Role of cluster members to which this distributed data is replicated.
     * This role is exclusive to the Things service.
     */
    public static final String CLUSTER_ROLE = "wot-validation-config-aware";

    /**
     * Name of the replicator actor.
     */
    public static final String ACTOR_NAME = "wotValidationConfigReplicator";

    /**
     * Key of the distributed data. Should be unique among ORSets.
     */
    private static final Key<ORSet<ImmutableWotValidationConfig>> KEY = ORSetKey.create("WotValidationConfig");

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
     * Add a TmValidationConfig to ALL replicas.
     * Replaces any existing configs with the new one in a single atomic operation.
     *
     * @param config the validation config to add.
     * @return future that completes after the update propagates to all replicas.
     */
    public CompletionStage<Void> add(final ImmutableWotValidationConfig config) {
        final ORSet<ImmutableWotValidationConfig> newSet =
                ORSet.<ImmutableWotValidationConfig>empty().add(selfUniqueAddress, config);
        return update(getKey(0), writeAll(), orSet -> newSet);
    }

    /**
     * Remove validation config(s) from ALL replicas.
     *
     * @return future that completes after the removal propagates to all replicas.
     */

    public CompletionStage<Void> clear() {
        return getConfigs().thenCompose(configSet -> {
            ORSet<ImmutableWotValidationConfig> temp = configSet;
            for (ImmutableWotValidationConfig config : configSet.getElements()) {
                temp = temp.remove(selfUniqueAddress, config);
            }
            final ORSet<ImmutableWotValidationConfig> cleared = temp;

            return update(getKey(0), writeAll(), orSet -> cleared);
        });
    }
    /**
     * Get the current validation configs from the local replica.
     *
     * @return future that completes with the current validation configs.
     */
    public CompletionStage<ORSet<ImmutableWotValidationConfig>> getConfigs() {
        return get(getKey(0), (Replicator.ReadConsistency) Replicator.readLocal())
                .thenApply(maybeORSet -> maybeORSet.orElse(ORSet.empty()));
    }

    @Override
    protected Key<ORSet<ImmutableWotValidationConfig>> getKey(final int shardNumber) {
        return KEY;
    }

    @Override
    protected ORSet<ImmutableWotValidationConfig> getInitialValue() {
        return ORSet.empty();
    }

    private Replicator.WriteConsistency writeAll() {
        return new Replicator.WriteAll(writeTimeout);
    }

    private static final class Provider
            extends DistributedData.AbstractDDataProvider<ORSet<ImmutableWotValidationConfig>, WotValidationConfigDData> {

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