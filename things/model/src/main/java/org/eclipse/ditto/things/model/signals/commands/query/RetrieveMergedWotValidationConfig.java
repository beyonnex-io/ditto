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
package org.eclipse.ditto.things.model.signals.commands.query;

import static org.eclipse.ditto.base.model.common.ConditionChecker.checkNotNull;

import java.util.Objects;
import java.util.function.Predicate;

import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.base.model.headers.DittoHeaders;
import org.eclipse.ditto.base.model.json.JsonParsableCommand;
import org.eclipse.ditto.base.model.json.JsonSchemaVersion;
import org.eclipse.ditto.base.model.signals.commands.AbstractCommand;
import org.eclipse.ditto.json.JsonField;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonObjectBuilder;
import org.eclipse.ditto.json.JsonPointer;
import org.eclipse.ditto.things.model.ThingId;
import org.eclipse.ditto.things.model.signals.commands.ThingCommand;
import org.eclipse.ditto.things.model.ValidationContext;

/**
 * Command to retrieve the merged WoT validation config.
 */
@Immutable
@JsonParsableCommand(typePrefix = ThingCommand.TYPE_PREFIX, name = RetrieveMergedWotValidationConfig.NAME)
public final class RetrieveMergedWotValidationConfig extends AbstractCommand<RetrieveMergedWotValidationConfig>
        implements ThingQueryCommand<RetrieveMergedWotValidationConfig> {

    /**
     * Name of the command.
     */
    public static final String NAME = "retrieveMergedWotValidationConfig";

    /**
     * Type of this command.
     */
    public static final String TYPE = ThingCommand.TYPE_PREFIX + NAME;

    private final ThingId thingId;
    private final ValidationContext validationContext;

    private RetrieveMergedWotValidationConfig(final ThingId thingId, 
            final ValidationContext validationContext,
            final DittoHeaders dittoHeaders) {
        super(TYPE, dittoHeaders);
        this.thingId = checkNotNull(thingId, "thingId");
        this.validationContext = validationContext;
    }

    /**
     * Creates a new {@code RetrieveMergedWotValidationConfig} command.
     *
     * @param thingId the ID of the thing.
     * @param validationContext the validation context.
     * @param dittoHeaders the headers of the command.
     * @return the command.
     * @throws NullPointerException if {@code thingId} is {@code null}.
     */
    public static RetrieveMergedWotValidationConfig of(final ThingId thingId,
            final ValidationContext validationContext,
            final DittoHeaders dittoHeaders) {
        return new RetrieveMergedWotValidationConfig(thingId, validationContext, dittoHeaders);
    }

    /**
     * Creates a new {@code RetrieveMergedWotValidationConfig} from a JSON object.
     *
     * @param jsonObject the JSON object of which the command is to be created.
     * @param dittoHeaders the headers of the command.
     * @return the command.
     * @throws NullPointerException if {@code jsonObject} is {@code null}.
     */
    public static RetrieveMergedWotValidationConfig fromJson(final JsonObject jsonObject,
            final DittoHeaders dittoHeaders) {
        final ThingId thingId = ThingId.of(jsonObject.getValueOrThrow(ThingCommand.JsonFields.JSON_THING_ID));
        final ValidationContext validationContext = ValidationContext.fromJson(jsonObject);
        return of(thingId, validationContext, dittoHeaders);
    }

    @Override
    public ThingId getEntityId() {
        return thingId;
    }

    /**
     * Returns the validation context for this command.
     *
     * @return the validation context.
     */
    public ValidationContext getValidationContext() {
        return validationContext;
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
        if (validationContext != null) {
            jsonObjectBuilder.setAll(validationContext.toJson());
        }
    }

    @Override
    public RetrieveMergedWotValidationConfig setDittoHeaders(final DittoHeaders dittoHeaders) {
        return of(thingId, validationContext, dittoHeaders);
    }

    @Override
    public Category getCategory() {
        return Category.QUERY;
    }

    @Override
    public String getTypePrefix() {
        return ThingCommand.TYPE_PREFIX;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), thingId, validationContext);
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
        final RetrieveMergedWotValidationConfig that = (RetrieveMergedWotValidationConfig) o;
        return Objects.equals(thingId, that.thingId) &&
                Objects.equals(validationContext, that.validationContext);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" +
                super.toString() +
                ", thingId=" + thingId +
                ", validationContext=" + validationContext +
                "]";
    }
} 