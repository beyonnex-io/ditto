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
package org.eclipse.ditto.things.model.devops.commands;

import static org.eclipse.ditto.base.model.common.ConditionChecker.checkNotNull;

import java.util.Objects;
import java.util.function.Predicate;

import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.base.model.headers.DittoHeaders;
import org.eclipse.ditto.base.model.json.JsonParsableCommand;
import org.eclipse.ditto.base.model.json.JsonSchemaVersion;
import org.eclipse.ditto.base.model.signals.commands.AbstractCommand;
import org.eclipse.ditto.base.model.signals.commands.Command;
import org.eclipse.ditto.json.JsonField;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonObjectBuilder;
import org.eclipse.ditto.json.JsonPointer;
import org.eclipse.ditto.things.model.ThingId;
import org.eclipse.ditto.things.model.WithThingId;
import org.eclipse.ditto.things.model.signals.commands.ThingCommand;

/**
 * Command which deletes the WoT validation configuration.
 */
@Immutable
@JsonParsableCommand(typePrefix = ThingCommand.TYPE_PREFIX, name = DeleteWotValidationConfig.NAME)
public final class DeleteWotValidationConfig extends AbstractCommand<DeleteWotValidationConfig>
        implements Command<DeleteWotValidationConfig>, WithThingId {

    /**
     * Name of the command.
     */
    public static final String NAME = "deleteWotValidationConfig";

    /**
     * Type of this command.
     */
    public static final String TYPE = ThingCommand.TYPE_PREFIX + NAME;

    private final ThingId thingId;

    private DeleteWotValidationConfig(final ThingId thingId,
            final DittoHeaders dittoHeaders) {
        super(TYPE, dittoHeaders);
        this.thingId = checkNotNull(thingId, "thingId");
    }

    /**
     * Creates a new {@code DeleteWotValidationConfig} command.
     *
     * @param thingId the ID of the thing.
     * @param dittoHeaders the headers of the command.
     * @return the command.
     * @throws NullPointerException if {@code thingId} is {@code null}.
     */
    public static DeleteWotValidationConfig of(final ThingId thingId,
            final DittoHeaders dittoHeaders) {
        return new DeleteWotValidationConfig(thingId, dittoHeaders);
    }

    /**
     * Creates a new {@code DeleteWotValidationConfig} from a JSON object.
     *
     * @param jsonObject the JSON object of which the command is to be created.
     * @param dittoHeaders the headers of the command.
     * @return the command.
     * @throws NullPointerException if {@code jsonObject} is {@code null}.
     */
    public static DeleteWotValidationConfig fromJson(final JsonObject jsonObject,
            final DittoHeaders dittoHeaders) {
        final ThingId thingId = ThingId.of(jsonObject.getValueOrThrow(ThingCommand.JsonFields.JSON_THING_ID));
        return of(thingId, dittoHeaders);
    }

    @Override
    public ThingId getEntityId() {
        return thingId;
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
    }

    @Override
    public Category getCategory() {
        return Category.DELETE;
    }

    @Override
    public String getTypePrefix() {
        return ThingCommand.TYPE_PREFIX;
    }

    @Override
    public DeleteWotValidationConfig setDittoHeaders(final DittoHeaders dittoHeaders) {
        return of(thingId, dittoHeaders);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), thingId);
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
        final DeleteWotValidationConfig that = (DeleteWotValidationConfig) o;
        return Objects.equals(thingId, that.thingId);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" +
                super.toString() +
                ", thingId=" + thingId +
                "]";
    }
}
