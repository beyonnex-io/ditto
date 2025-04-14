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
//package org.eclipse.ditto.things.model.signals.commands.modify;
//
//import static org.eclipse.ditto.base.model.common.ConditionChecker.checkNotNull;
//
//import java.util.Objects;
//import java.util.function.Predicate;
//
//import javax.annotation.Nullable;
//import javax.annotation.concurrent.Immutable;
//
//import org.eclipse.ditto.base.model.headers.DittoHeaders;
//import org.eclipse.ditto.base.model.json.FieldType;
//import org.eclipse.ditto.base.model.json.JsonParsableCommand;
//import org.eclipse.ditto.base.model.json.JsonSchemaVersion;
//import org.eclipse.ditto.base.model.signals.commands.AbstractCommand;
//import org.eclipse.ditto.base.model.signals.commands.CommandJsonDeserializer;
//import org.eclipse.ditto.json.JsonFactory;
//import org.eclipse.ditto.json.JsonField;
//import org.eclipse.ditto.json.JsonFieldDefinition;
//import org.eclipse.ditto.json.JsonObject;
//import org.eclipse.ditto.json.JsonObjectBuilder;
//import org.eclipse.ditto.json.JsonPointer;
//import org.eclipse.ditto.things.model.ThingId;
//import org.eclipse.ditto.things.model.devops.ImmutableWoTValidationConfig;
//import org.eclipse.ditto.things.model.signals.commands.ThingCommand;
//
///**
// * This command creates a WoT validation configuration.
// *
// * @since 3.0.0
// */
//@Immutable
//@JsonParsableCommand(typePrefix = ThingCommand.TYPE_PREFIX, name = CreateWotValidationConfig.NAME)
//public final class CreateWotValidationConfig extends AbstractCommand<CreateWotValidationConfig>
//        implements ThingModifyCommand<CreateWotValidationConfig> {
//
//    /**
//     * Name of this command.
//     */
//    public static final String NAME = "createWotValidationConfig";
//
//    /**
//     * Type of this command.
//     */
//    public static final String TYPE = ThingCommand.TYPE_PREFIX + NAME;
//
//    private final ThingId thingId;
//    private final ImmutableWoTValidationConfig config;
//
//    private CreateWotValidationConfig(final ThingId thingId,
//            final ImmutableWoTValidationConfig config,
//            final DittoHeaders dittoHeaders) {
//
//        super(TYPE, dittoHeaders);
//        this.thingId = checkNotNull(thingId, "thingId");
//        this.config = checkNotNull(config, "config");
//    }
//
//    /**
//     * Creates a new {@code CreateWotValidationConfig} command.
//     *
//     * @param thingId the identifier of the Thing to which the created configuration belongs
//     * @param config the configuration to create
//     * @param dittoHeaders the headers of the command
//     * @return the command
//     * @throws NullPointerException if any argument is {@code null}
//     */
//    public static CreateWotValidationConfig of(final ThingId thingId,
//            final ImmutableWoTValidationConfig config,
//            final DittoHeaders dittoHeaders) {
//        return new CreateWotValidationConfig(thingId, config, dittoHeaders);
//    }
//
//    /**
//     * Creates a new {@code CreateWotValidationConfig} from a JSON object.
//     *
//     * @param jsonObject the JSON object of which the command is to be created.
//     * @param dittoHeaders the headers of the command.
//     * @return the command.
//     * @throws NullPointerException if {@code jsonObject} is {@code null}.
//     * @throws org.eclipse.ditto.json.JsonParseException if the passed in {@code jsonObject} was not in the expected
//     * format.
//     */
//    public static CreateWotValidationConfig fromJson(final JsonObject jsonObject, final DittoHeaders dittoHeaders) {
//        return new CommandJsonDeserializer<CreateWotValidationConfig>(TYPE, jsonObject).deserialize(() -> {
//            final ThingId thingId = ThingId.of(jsonObject.getValueOrThrow(ThingCommand.JsonFields.JSON_THING_ID));
//            final JsonObject configJson = jsonObject.getValueOrThrow(JsonFields.JSON_CONFIG);
//            final ImmutableWoTValidationConfig config = ImmutableWoTValidationConfig.fromJson(configJson);
//
//            return new CreateWotValidationConfig(thingId, config, dittoHeaders);
//        });
//    }
//
//    @Override
//    public ThingId getEntityId() {
//        return thingId;
//    }
//
//    /**
//     * Returns the configuration to create.
//     *
//     * @return the configuration.
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
//    protected void appendPayload(final JsonObjectBuilder jsonObjectBuilder, final JsonSchemaVersion schemaVersion,
//            final Predicate<JsonField> thePredicate) {
//
//        final Predicate<JsonField> predicate = schemaVersion.and(thePredicate);
//        jsonObjectBuilder.set(ThingCommand.JsonFields.JSON_THING_ID, thingId.toString(), predicate);
//        jsonObjectBuilder.set(JsonFields.JSON_CONFIG, config.toJson(), predicate);
//    }
//
//    @Override
//    public Category getCategory() {
//        return Category.CREATE;
//    }
//
//    @Override
//    public CreateWotValidationConfig setDittoHeaders(final DittoHeaders dittoHeaders) {
//        return new CreateWotValidationConfig(thingId, config, dittoHeaders);
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
//        final CreateWotValidationConfig that = (CreateWotValidationConfig) o;
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