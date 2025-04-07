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

import static org.eclipse.ditto.base.model.common.ConditionChecker.checkNotNull;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Optional;

import org.eclipse.ditto.base.model.json.FieldType;
import org.eclipse.ditto.base.model.json.JsonSchemaVersion;
import org.eclipse.ditto.base.model.json.Jsonifiable;
import org.eclipse.ditto.internal.utils.persistence.mongo.AbstractMongoSnapshotAdapter;
import org.eclipse.ditto.json.JsonFactory;
import org.eclipse.ditto.json.JsonField;
import org.eclipse.ditto.json.JsonFieldDefinition;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonObjectBuilder;
import org.eclipse.ditto.json.JsonValue;
import org.eclipse.ditto.things.model.devops.ImmutableWoTValidationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SnapshotAdapter for {@link ImmutableWoTValidationConfig}.
 */
@Immutable
public final class WotValidationConfigMongoSnapshotAdapter extends AbstractMongoSnapshotAdapter<ImmutableWoTValidationConfig> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WotValidationConfigMongoSnapshotAdapter.class);

    private static final JsonFieldDefinition<JsonValue> JSON_CONFIG = JsonFactory.newJsonValueFieldDefinition("config",
            FieldType.REGULAR,
            JsonSchemaVersion.V_2);

    /**
     * Constructs a new {@code WotValidationConfigMongoSnapshotAdapter}.
     */
    public WotValidationConfigMongoSnapshotAdapter() {
        super(LOGGER);
    }

    @Override
    protected boolean isDeleted(@Nullable final ImmutableWoTValidationConfig snapshotEntity) {
        return null == snapshotEntity;
    }

    @Override
    protected JsonField getDeletedLifecycleJsonField() {
        return JsonField.newInstance(JSON_CONFIG.getPointer().getRoot().orElseThrow(),
                JsonValue.nullLiteral(), JSON_CONFIG);
    }

    @Override
    protected Optional<JsonField> getRevisionJsonField(final ImmutableWoTValidationConfig entity) {
        return Optional.empty();
    }

    @Override
    protected ImmutableWoTValidationConfig createJsonifiableFrom(final JsonObject jsonObject) {
        return ImmutableWoTValidationConfig.fromJson(jsonObject);
    }

    @Override
    protected JsonObject convertToJson(final ImmutableWoTValidationConfig snapshotEntity) {
        checkNotNull(snapshotEntity, "snapshotEntity");
        return JsonObject.newBuilder()
                .set(JSON_CONFIG, snapshotEntity.toJson(JsonSchemaVersion.V_2, FieldType.notHidden()))
                .build();
    }
} 