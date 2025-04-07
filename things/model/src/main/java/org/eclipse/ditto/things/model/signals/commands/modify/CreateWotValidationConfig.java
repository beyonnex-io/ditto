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
package org.eclipse.ditto.things.model.signals.commands.modify;

import static org.eclipse.ditto.base.model.common.ConditionChecker.checkNotNull;

import java.util.Objects;
import java.util.function.Predicate;

import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.base.model.headers.DittoHeaders;
import org.eclipse.ditto.base.model.json.JsonParsableCommand;
import org.eclipse.ditto.base.model.json.JsonSchemaVersion;
import org.eclipse.ditto.base.model.signals.commands.AbstractCommand;
import org.eclipse.ditto.json.JsonFactory;
import org.eclipse.ditto.json.JsonField;
import org.eclipse.ditto.json.JsonFieldDefinition;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonObjectBuilder;
import org.eclipse.ditto.json.JsonPointer;
import org.eclipse.ditto.things.model.ThingId;
import org.eclipse.ditto.things.model.signals.commands.ThingCommand;

/**
 * Command which creates a new WoT validation configuration.
 */
@Immutable
@JsonParsableCommand(typePrefix = ThingCommand.TYPE_PREFIX, name = CreateWotValidationConfig.NAME)
public final class CreateWotValidationConfig extends AbstractCommand<CreateWotValidationConfig>
        implements WotValidationConfigCommand<CreateWotValidationConfig> {

    /**
     * Name of this command.
     */
    public static final String NAME = "createWotValidationConfig";

    /**
     * Type of this command.
     */
    public static final String TYPE = ThingCommand.TYPE_PREFIX + NAME;

    private final ThingId thingId;
    private final WotValidationConfig config;

    private CreateWotValidationConfig(final ThingId thingId,
            final WotValidationConfig config,
            final DittoHeaders dittoHeaders) {
        super(TYPE, dittoHeaders);
        this.thingId = checkNotNull(thingId, "thingId");
        this.config = checkNotNull(config, "config");
    }

    /**
     * Creates a new {@code CreateWotValidationConfig} command.
     *
     * @param thingId the ID of the thing.
     * @param config the WoT validation configuration.
     * @param dittoHeaders the headers of the command.
     * @return the command.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static CreateWotValidationConfig of(final ThingId thingId,
            final WotValidationConfig config,
            final DittoHeaders dittoHeaders) {
        return new CreateWotValidationConfig(thingId, config, dittoHeaders);
    }

    /**
     * Creates a new {@code CreateWotValidationConfig} from a JSON object.
     *
     * @param jsonObject the JSON object of which the command is to be created.
     * @param dittoHeaders the headers of the command.
     * @return the command.
     * @throws NullPointerException if {@code jsonObject} is {@code null}.
     * @throws org.eclipse.ditto.json.JsonParseException if the passed in {@code jsonObject} was not in the expected format.
     */
    public static CreateWotValidationConfig fromJson(final JsonObject jsonObject, final DittoHeaders dittoHeaders) {
        final ThingId thingId = ThingId.of(jsonObject.getValueOrThrow(ThingCommand.JsonFields.JSON_THING_ID));
        final WotValidationConfig config = WotValidationConfig.fromJson(jsonObject);
        return of(thingId, config, dittoHeaders);
    }

    @Override
    public ThingId getEntityId() {
        return thingId;
    }

    /**
     * Returns the WoT validation configuration.
     *
     * @return the configuration.
     */
    public WotValidationConfig getConfig() {
        return config;
    }

    @Override
    public JsonPointer getResourcePath() {
        return JsonPointer.empty();
    }

    @Override
    public String getResourceType() {
        return ThingCommand.RESOURCE_TYPE;
    }

    @Override
    protected void appendPayload(final JsonObjectBuilder jsonObjectBuilder,
            final JsonSchemaVersion schemaVersion,
            final Predicate<JsonField> predicate) {
        jsonObjectBuilder.set(ThingCommand.JsonFields.JSON_THING_ID, thingId.toString());
        if (config != null) {
            jsonObjectBuilder.setAll(config.toJson());
        }
    }

    @Override
    public Category getCategory() {
        return Category.MODIFY;
    }

    @Override
    public CreateWotValidationConfig setDittoHeaders(final DittoHeaders dittoHeaders) {
        return of(thingId, config, dittoHeaders);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), thingId, config);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final CreateWotValidationConfig that = (CreateWotValidationConfig) o;
        return Objects.equals(thingId, that.thingId) &&
                Objects.equals(config, that.config);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" +
                super.toString() +
                ", thingId=" + thingId +
                ", config=" + config +
                "]";
    }
} 