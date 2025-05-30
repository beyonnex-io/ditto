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
package org.eclipse.ditto.internal.utils.persistence.mongo.indices;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import javax.annotation.concurrent.Immutable;

import org.apache.pekko.Done;
import org.apache.pekko.NotUsed;
import org.apache.pekko.stream.Materializer;
import org.apache.pekko.stream.javadsl.Sink;
import org.apache.pekko.stream.javadsl.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.reactivestreams.client.MongoDatabase;

/**
 * Initializes indexes on a MongoDB collection.
 */
@Immutable
public final class IndexInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexInitializer.class);

    private final Materializer materializer;
    private final IndexOperations indexOperations;

    private IndexInitializer(final MongoDatabase db, final Materializer materializer) {
        this.materializer = materializer;
        this.indexOperations = IndexOperations.of(db);
    }

    /**
     * Returns a new {@code MongoIndexInitializer}.
     *
     * @param db the mongo client to use for creating indexes.
     * @param materializer the materializer for pekko streams.
     * @return the index initializer.
     */
    public static IndexInitializer of(final MongoDatabase db,
            final Materializer materializer) {
        requireNonNull(db);
        requireNonNull(materializer);
        return new IndexInitializer(db, materializer);
    }

    /**
     * Creates all given indexes on the specified collection and deletes all not defined indices (except of the default
     * "_id_" index).
     *
     * @param collectionName the collection on which the indexes will be initialized.
     * @param indices the indexes to be used for initialization.
     * @param activatedIndexNames the index names which should be active via configuration.
     * @return a completion stage that completes successfully if the indexes are initialized, otherwise it will raise an
     * exception.
     */
    public CompletionStage<Void> initialize(final String collectionName, final List<Index> indices,
            final Set<String> activatedIndexNames) {
        requireNonNull(collectionName);
        requireNonNull(indices);

        LOGGER.info("index-initialization is aware of defined indices: {}", indices);
        final List<Index> activatedIndices = indices.stream()
                    .filter(ind -> activatedIndexNames.contains(ind.getName()))
                    .toList();
        LOGGER.info("Starting index-initialization with activated indices: {}", activatedIndices);

        return createNonExistingIndices(collectionName, activatedIndices)
                .thenCompose(done -> dropUndefinedIndices(collectionName, activatedIndices, activatedIndexNames))
                .thenApply(unused -> {
                    LOGGER.info("Index-Initialization was successful.");
                    return null;
                });
    }

    /**
     * Create defined indices whose names are not among the indexes of the collection.
     *
     * @param collectionName name of the collection.
     * @param indices the defined indices.
     * @return a future that completes after index creation or fails when index creation fails.
     */
    public CompletionStage<Done> createNonExistingIndices(final String collectionName, final List<Index> indices) {
        if (indices.isEmpty()) {
            LOGGER.warn("No indices are defined, thus no indices are created.");
            return CompletableFuture.completedFuture(Done.getInstance());
        }
        return indexOperations.getIndicesExceptDefaultIndex(collectionName)
                .flatMapConcat(
                        existingIndices -> {
                            LOGGER.info("Create non-existing indices: Existing indices are: {}", existingIndices);
                            final List<Index> indicesToCreate = excludeIndices(indices, existingIndices, Set.of());
                            LOGGER.info("Indices to create are: {}", indicesToCreate);
                            return createIndices(collectionName, indicesToCreate);
                        })
                .runWith(Sink.ignore(), materializer);
    }

    private Source<Done, NotUsed> createIndices(final String collectionName, final List<Index> indices) {
        if (indices.isEmpty()) {
            return Source.empty();
        }

        return Source.from(indices)
                .flatMapConcat(index -> createIndex(collectionName, index));
    }

    private Source<Done, NotUsed> createIndex(final String collectionName, final Index index) {
        LOGGER.info("Creating index: {}", index);
        return indexOperations.createIndex(collectionName, index);
    }

    private CompletionStage<Done> dropUndefinedIndices(final String collectionName, final List<Index> activeIndices,
            final Set<String> allToleratedIndexNames) {
        return getIndicesExceptDefaultIndex(collectionName)
                .flatMapConcat(existingIndices -> {
                    LOGGER.info("Drop undefined indices - Existing indices are: {}", existingIndices);
                    final List<String> indicesToDrop = getUndefinedIndexNames(existingIndices, activeIndices,
                            allToleratedIndexNames);
                    LOGGER.info("Dropping undefined indices: {}", indicesToDrop);
                    return dropIndices(collectionName, indicesToDrop);
                })
                .runWith(Sink.ignore(), materializer);
    }

    private static List<String> getUndefinedIndexNames(final Collection<Index> allIndices,
            final Collection<Index> definedIndices, final Set<String> allToleratedIndexNames) {

        return excludeIndices(allIndices, definedIndices, allToleratedIndexNames).stream()
                .map(Index::getName)
                .toList();
    }

    private Source<Done, NotUsed> dropIndices(final String collectionName, final List<String> indices) {
        if (indices.isEmpty()) {
            return Source.empty();
        }

        return Source.from(indices)
                .flatMapConcat(index -> dropIndex(collectionName, index));
    }

    private Source<Done, NotUsed> dropIndex(final String collectionName, final String indexName) {
        LOGGER.info("Dropping index: {}", indexName);
        return indexOperations.dropIndex(collectionName, indexName);
    }

    private Source<List<Index>, NotUsed> getIndicesExceptDefaultIndex(final String collectionName) {
        return indexOperations.getIndicesExceptDefaultIndex(collectionName);
    }

    private static Set<String> extractIndexNames(final Collection<Index> indices) {
        return indices.stream()
                .map(Index::getName)
                .collect(Collectors.toSet());
    }

    private static List<Index> excludeIndices(final Collection<Index> allIndices, final Collection<Index>
            definedIndices, final Set<String> allToleratedIndexNames) {

        final Set<String> definedIndexNames = extractIndexNames(definedIndices);

        return allIndices.stream()
                .filter(indexModel -> !definedIndexNames.contains(indexModel.getName()) ||
                        !allToleratedIndexNames.contains(indexModel.getName())
                )
                .toList();
    }

}
