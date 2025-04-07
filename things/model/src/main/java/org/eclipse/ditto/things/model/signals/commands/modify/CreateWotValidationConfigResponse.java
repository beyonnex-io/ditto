/*
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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
package org.eclipse.ditto.things.model.signals.commands.modify;

import static org.eclipse.ditto.base.model.common.ConditionChecker.checkNotNull;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.base.model.common.HttpStatus;
import org.eclipse.ditto.base.model.headers.DittoHeaders;
import org.eclipse.ditto.base.model.json.FieldType;
import org.eclipse.ditto.base.model.json.JsonParsableCommandResponse;
import org.eclipse.ditto.base.model.json.JsonSchemaVersion;
import org.eclipse.ditto.base.model.signals.WithOptionalEntity;
import org.eclipse.ditto.base.model.signals.commands.AbstractCommandResponse;
import org.eclipse.ditto.base.model.signals.commands.CommandResponse;
import org.eclipse.ditto.base.model.signals.commands.CommandResponseHttpStatusValidator;
import org.eclipse.ditto.base.model.signals.commands.CommandResponseJsonDeserializer;
import org.eclipse.ditto.json.JsonField;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonObjectBuilder;
import org.eclipse.ditto.json.JsonPointer;
import org.eclipse.ditto.json.JsonValue;
import org.eclipse.ditto.things.model.ThingId;
import org.eclipse.ditto.things.model.signals.commands.ThingCommandResponse;

/**
 * Response to a {@link CreateWotValidationConfig} command.
 *
 * @since 3.0.0
 */
@Immutable
@JsonParsableCommandResponse(type = CreateWotValidationConfigResponse.TYPE)
public final class CreateWotValidationConfigResponse extends AbstractCommandResponse<CreateWotValidationConfigResponse>
        implements ThingModifyCommandResponse<CreateWotValidationConfigResponse>, WithOptionalEntity<CreateWotValidationConfigResponse> {

    /**
     * Type prefix of WoT validation config commands.
     */
    public static final String TYPE_PREFIX = "devops:";

    /**
     * Type of this response.
     */
    public static final String TYPE = TYPE_PREFIX + CreateWotValidationConfig.NAME;

    private static final HttpStatus HTTP_STATUS = HttpStatus.CREATED;

    private static final CommandResponseJsonDeserializer<CreateWotValidationConfigResponse> JSON_DESERIALIZER =
            CommandResponseJsonDeserializer.newInstance(TYPE,
                    context -> {
                        final JsonObject jsonObject = context.getJsonObject();
                        return new CreateWotValidationConfigResponse(
                                ThingId.of(jsonObject.getValueOrThrow(ThingCommandResponse.JsonFields.JSON_THING_ID)),
                                context.getDittoHeaders()
                        );
                    });

    private final ThingId thingId;

    private CreateWotValidationConfigResponse(final ThingId thingId,
            final DittoHeaders dittoHeaders) {

        super(TYPE, CommandResponseHttpStatusValidator.validateHttpStatus(HTTP_STATUS,
                Collections.singleton(HTTP_STATUS),
                CreateWotValidationConfigResponse.class),
                dittoHeaders);
        this.thingId = checkNotNull(thingId, "Thing ID");
    }

    /**
     * Creates a response to a {@code CreateWotValidationConfig} command.
     *
     * @param thingId the Thing ID of the created WoT validation configuration.
     * @param dittoHeaders the headers of the preceding command.
     * @return the response.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static CreateWotValidationConfigResponse of(final ThingId thingId,
            final DittoHeaders dittoHeaders) {

        return new CreateWotValidationConfigResponse(thingId, dittoHeaders);
    }

    /**
     * Creates a response to a {@code CreateWotValidationConfig} command from a JSON object.
     *
     * @param jsonObject the JSON object of which the response is to be created.
     * @param dittoHeaders the headers of the preceding command.
     * @return the response.
     * @throws NullPointerException if {@code jsonObject} is {@code null}.
     * @throws org.eclipse.ditto.json.JsonParseException if the passed in {@code jsonObject} was not in the expected
     * format.
     */
    public static CreateWotValidationConfigResponse fromJson(final JsonObject jsonObject,
            final DittoHeaders dittoHeaders) {

        return JSON_DESERIALIZER.deserialize(jsonObject, dittoHeaders);
    }

    @Override
    public ThingId getEntityId() {
        return thingId;
    }

    @Override
    public JsonPointer getResourcePath() {
        return JsonPointer.of("/wotValidationConfig");
    }

    @Override
    protected void appendPayload(final JsonObjectBuilder jsonObjectBuilder,
            final JsonSchemaVersion schemaVersion,
            final Predicate<JsonField> thePredicate) {

        final Predicate<JsonField> predicate = schemaVersion.and(thePredicate);
        jsonObjectBuilder.set(ThingCommandResponse.JsonFields.JSON_THING_ID, thingId.toString(), predicate);
    }

    @Override
    public CreateWotValidationConfigResponse setDittoHeaders(final DittoHeaders dittoHeaders) {
        return new CreateWotValidationConfigResponse(thingId, dittoHeaders);
    }

    @Override
    public Optional<JsonValue> getEntity(final JsonSchemaVersion schemaVersion) {
        return Optional.empty();
    }

    @Override
    public CreateWotValidationConfigResponse setEntity(final JsonValue entity) {
        return this;
    }

    @Override
    protected boolean canEqual(@Nullable final Object other) {
        return other instanceof CreateWotValidationConfigResponse;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CreateWotValidationConfigResponse that = (CreateWotValidationConfigResponse) o;
        return that.canEqual(this) &&
                Objects.equals(thingId, that.thingId) &&
                super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), thingId);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" +
                super.toString() +
                ", thingId=" + thingId +
                "]";
    }
} 