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
package org.eclipse.ditto.things.service.persistence.mongo;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.annotation.concurrent.Immutable;

import org.bson.Document;
import org.eclipse.ditto.base.model.exceptions.DittoRuntimeException;
import org.eclipse.ditto.base.model.exceptions.DittoInternalErrorException;
import org.eclipse.ditto.wot.validation.config.TmValidationConfig;
import org.eclipse.ditto.wot.validation.config.ImmutableTmValidationConfig;

import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.client.result.DeleteResult;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * MongoDB repository for WoT validation configurations.
 */
@Immutable
public final class MongoWotValidationConfigRepository {

    private static final String COLLECTION_NAME = "wot_validation_configs";
    private static final String ID_FIELD = "_id";
    private static final String CONFIG_FIELD = "config";

    private final MongoCollection<Document> collection;

    public MongoWotValidationConfigRepository(final MongoCollection<Document> collection) {
        this.collection = collection;
    }

    public CompletionStage<Void> create(final TmValidationConfig config) {
        final Document document = new Document()
                .append(ID_FIELD, config.getId())
                .append(CONFIG_FIELD, config.toJson());

        final Publisher<InsertOneResult> publisher = collection.insertOne(document);
        return toCompletionStage(publisher);
    }

    public CompletionStage<Void> update(final TmValidationConfig config) {
        final Document document = new Document()
                .append(ID_FIELD, config.getId())
                .append(CONFIG_FIELD, config.toJson());

        final Publisher<UpdateResult> publisher = collection.replaceOne(
                Filters.eq(ID_FIELD, config.getId()),
                document,
                new ReplaceOptions().upsert(true)
        );
        return toCompletionStage(publisher);
    }

    public CompletionStage<Void> delete(final TmValidationConfig config) {
        final Publisher<DeleteResult> publisher = collection.deleteOne(Filters.eq(ID_FIELD, config.getId()));
        return toCompletionStage(publisher);
    }

    private <T> CompletionStage<Void> toCompletionStage(final Publisher<T> publisher) {
        final CompletableFuture<Void> future = new CompletableFuture<>();
        
        publisher.subscribe(new Subscriber<T>() {
            @Override
            public void onSubscribe(final Subscription s) {
                s.request(1);
            }

            @Override
            public void onNext(final T t) {
                // Do nothing
            }

            @Override
            public void onError(final Throwable t) {
                future.completeExceptionally(DittoRuntimeException.asDittoRuntimeException(t,
                        cause -> DittoInternalErrorException.newBuilder()
                                .message("Failed to perform MongoDB operation: " + cause.getMessage())
                                .cause(cause)
                                .build()));
            }

            @Override
            public void onComplete() {
                future.complete(null);
            }
        });

        return future;
    }
} 