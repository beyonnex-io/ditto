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
////package org.eclipse.ditto.things.model.signals.commands.modify;
//
//import static org.eclipse.ditto.base.model.common.ConditionChecker.checkNotNull;
//
//import java.util.Objects;
//import java.util.function.Predicate;
//
//import javax.annotation.Nullable;
//import javax.annotation.concurrent.Immutable;
//
//import org.eclipse.ditto.base.model.common.HttpStatus;
//import org.eclipse.ditto.base.model.headers.DittoHeaders;
//import org.eclipse.ditto.base.model.json.FieldType;
//import org.eclipse.ditto.base.model.json.JsonParsableCommandResponse;
//import org.eclipse.ditto.base.model.json.JsonSchemaVersion;
//import org.eclipse.ditto.base.model.signals.commands.AbstractCommandResponse;
//import org.eclipse.ditto.base.model.signals.commands.CommandResponseHttpStatusValidator;
//import org.eclipse.ditto.base.model.signals.commands.CommandResponseJsonDeserializer;
//import org.eclipse.ditto.json.JsonField;
//import org.eclipse.ditto.json.JsonFieldDefinition;
//import org.eclipse.ditto.json.JsonObject;
//import org.eclipse.ditto.json.JsonObjectBuilder;
//import org.eclipse.ditto.json.JsonPointer;
//import org.eclipse.ditto.json.JsonValue;
//import org.eclipse.ditto.things.model.ThingId;
//import org.eclipse.ditto.things.model.devops.ImmutableWoTValidationConfig;
//import org.eclipse.ditto.things.model.signals.commands.ThingCommandResponse;
//import java.util.Collections;
//import org.eclipse.ditto.base.model.signals.WithOptionalEntity;
//import org.eclipse.ditto.json.JsonFactory;
//
///**
// * Response to a {@link CreateWotValidationConfig} command.
// *
// * @since 3.0.0
// */
//@Immutable
//@JsonParsableCommandResponse(type = CreateWotValidationConfigResponse.TYPE)
//public final class CreateWotValidationConfigResponse extends AbstractCommandResponse<CreateWotValidationConfigResponse>
//        implements ThingModifyCommandResponse<CreateWotValidationConfigResponse> {
//
//    /**
//     * Type of this response.
//     */
//    public static final String TYPE = TYPE_PREFIX + CreateWotValidationConfig.NAME;
//
//    private static final HttpStatus HTTP_STATUS = HttpStatus.CREATED;
//
//    private static final CommandResponseJsonDeserializer<CreateWotValidationConfigResponse> JSON_DESERIALIZER =
//            CommandResponseJsonDeserializer.newInstance(TYPE,
//                    context -> {
//                        final JsonObject jsonObject = context.getJsonObject();
//                        return newInstance(
//                                ThingId.of(jsonObject.getValueOrThrow(ThingCommandResponse.JsonFields.JSON_THING_ID)),
//                                ImmutableWoTValidationConfig.fromJson(jsonObject.getValueOrThrow(JsonFields.JSON_CONFIG)),
//                                context.getDeserializedHttpStatus(),
//                                context.getDittoHeaders()
//                        );
//                    });
//
//    private final ThingId thingId;
//    private final ImmutableWoTValidationConfig config;
//
//    private CreateWotValidationConfigResponse(final ThingId thingId,
//            final ImmutableWoTValidationConfig config,
//            final HttpStatus httpStatus,
//            final DittoHeaders dittoHeaders) {
//
//        super(TYPE, httpStatus, dittoHeaders);
//        this.thingId = checkNotNull(thingId, "thingId");
//        this.config = checkNotNull(config, "config");
//    }
//
//    /**
//     * Creates a response to a {@code CreateWotValidationConfig} command.
//     *
//     * @param thingId the Thing ID of the created WoT validation config.
//     * @param config the created WoT validation config.
//     * @param dittoHeaders the headers of the command which resulted in this response.
//     * @return the response.
//     * @throws NullPointerException if any argument is {@code null}.
//     */
//    public static CreateWotValidationConfigResponse of(final ThingId thingId,
//            final ImmutableWoTValidationConfig config,
//            final DittoHeaders dittoHeaders) {
//
//        return newInstance(thingId, config, HTTP_STATUS, dittoHeaders);
//    }
//
//    /**
//     * Creates a response to a {@code CreateWotValidationConfig} command.
//     *
//     * @param thingId the Thing ID of the created WoT validation config.
//     * @param config the created WoT validation config.
//     * @param httpStatus the status of the response.
//     * @param dittoHeaders the headers of the command which resulted in this response.
//     * @return the response.
//     * @throws NullPointerException if any argument is {@code null}.
//     */
//    public static CreateWotValidationConfigResponse newInstance(final ThingId thingId,
//            final ImmutableWoTValidationConfig config,
//            final HttpStatus httpStatus,
//            final DittoHeaders dittoHeaders) {
//
//        return new CreateWotValidationConfigResponse(thingId,
//                config,
//                CommandResponseHttpStatusValidator.validateHttpStatus(httpStatus,
//                        Collections.singleton(HTTP_STATUS),
//                        CreateWotValidationConfigResponse.class),
//                dittoHeaders);
//    }
//
//    /**
//     * Creates a response to a {@code CreateWotValidationConfig} command from a JSON object.
//     *
//     * @param jsonObject the JSON object of which the response is to be created.
//     * @param dittoHeaders the headers of the preceding command.
//     * @return the response.
//     * @throws NullPointerException if {@code jsonObject} is {@code null}.
//     * @throws org.eclipse.ditto.json.JsonParseException if the passed in {@code jsonObject} was not in the expected format.
//     */
//    public static CreateWotValidationConfigResponse fromJson(final JsonObject jsonObject,
//            final DittoHeaders dittoHeaders) {
//        return JSON_DESERIALIZER.deserialize(jsonObject, dittoHeaders);
//    }
//
//    @Override
//    public ThingId getEntityId() {
//        return thingId;
//    }
//
//    /**
//     * Returns the created WoT validation config.
//     *
//     * @return the created WoT validation config.
//     */
//    public ImmutableWoTValidationConfig getConfig() {
//        return config;
//    }
//
//    @Override
//    public JsonPointer getResourcePath() {
//        return JsonPointer.of("/wot/validation/config");
//    }
//
//    @Override
//    protected void appendPayload(final JsonObjectBuilder jsonObjectBuilder,
//            final JsonSchemaVersion schemaVersion,
//            final Predicate<JsonField> thePredicate) {
//
//        final Predicate<JsonField> predicate = schemaVersion.and(thePredicate);
//        jsonObjectBuilder.set(ThingCommandResponse.JsonFields.JSON_THING_ID, thingId.toString(), predicate);
//        jsonObjectBuilder.set(JsonFields.JSON_CONFIG, config.toJson(), predicate);
//    }
//
//    @Override
//    public CreateWotValidationConfigResponse setDittoHeaders(final DittoHeaders dittoHeaders) {
//        return newInstance(thingId, config, getHttpStatus(), dittoHeaders);
//    }
//
//    @Override
//    public boolean equals(@Nullable final Object o) {
//        if (this == o) {
//            return true;
//        }
//        if (o == null || getClass() != o.getClass()) {
//            return false;
//        }
//        if (!super.equals(o)) {
//            return false;
//        }
//        final CreateWotValidationConfigResponse that = (CreateWotValidationConfigResponse) o;
//        return Objects.equals(thingId, that.thingId) &&
//                Objects.equals(config, that.config);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(super.hashCode(), thingId, config);
//    }
//
//    @Override
//    public String toString() {
//        return getClass().getSimpleName() + " [" +
//                super.toString() +
//                ", thingId=" + thingId +
//                ", config=" + config +
//                "]";
//    }
//
//    @Override
//    public CreateWotValidationConfigResponse setEntity(final JsonValue entity) {
//        return this;
//    }
//
//    /**
//     * This class contains definitions for all specific fields of this command's JSON representation.
//     */
//    @Immutable
//    static final class JsonFields {
//
//        static final JsonFieldDefinition<JsonObject> JSON_CONFIG =
//                JsonFactory.newJsonObjectFieldDefinition("config", FieldType.REGULAR,
//                        JsonSchemaVersion.V_2);
//
//        private JsonFields() {
//            throw new AssertionError();
//        }
//    }
//}