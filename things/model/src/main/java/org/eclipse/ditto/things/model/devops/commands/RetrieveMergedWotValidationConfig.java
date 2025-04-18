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

import java.util.Objects;
import java.util.function.Predicate;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.base.model.headers.DittoHeaders;
import org.eclipse.ditto.base.model.json.JsonParsableCommand;
import org.eclipse.ditto.base.model.json.JsonSchemaVersion;
import org.eclipse.ditto.base.model.signals.commands.Command;
import org.eclipse.ditto.json.JsonField;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonObjectBuilder;
import org.eclipse.ditto.json.JsonPointer;
import org.eclipse.ditto.things.model.devops.WotValidationConfigId;

/**
 * Command to retrieve a merged WoT validation config.
 */
@Immutable
@JsonParsableCommand(typePrefix = WotValidationConfigCommand.TYPE_PREFIX, name = RetrieveMergedWotValidationConfig.NAME)
public final class RetrieveMergedWotValidationConfig extends AbstractWotValidationConfigCommand<RetrieveMergedWotValidationConfig>
        implements WotValidationConfigCommand<RetrieveMergedWotValidationConfig> {

    /**
     * Name of the command.
     */
    public static final String NAME = "retrieveMergedWotValidationConfig";

    /**
     * Type of this command.
     */
    public static final String TYPE = WotValidationConfigCommand.TYPE_PREFIX + NAME;

    private RetrieveMergedWotValidationConfig(final WotValidationConfigId configId, final DittoHeaders dittoHeaders) {
        super(TYPE, configId, dittoHeaders);
    }

    /**
     * Creates a new instance of {@code RetrieveMergedWotValidationConfig}.
     *
     * @param configId the ID of the validation config.
     * @param dittoHeaders the headers of the command.
     * @return the new instance.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static RetrieveMergedWotValidationConfig of(final WotValidationConfigId configId, final DittoHeaders dittoHeaders) {
        Objects.requireNonNull(configId, "configId");
        Objects.requireNonNull(dittoHeaders, "dittoHeaders");
        return new RetrieveMergedWotValidationConfig(configId, dittoHeaders);
    }

    /**
     * Creates a new instance of {@code RetrieveMergedWotValidationConfig} from a JSON object.
     *
     * @param jsonObject the JSON object.
     * @param dittoHeaders the headers of the command.
     * @return the new instance.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws org.eclipse.ditto.json.JsonParseException if the passed in {@code jsonObject} was not in the expected format.
     */
    public static RetrieveMergedWotValidationConfig fromJson(final JsonObject jsonObject, final DittoHeaders dittoHeaders) {
        final String configIdString = jsonObject.getValueOrThrow(WotValidationConfigCommand.JsonFields.CONFIG_ID);
        final WotValidationConfigId configId = WotValidationConfigId.of(configIdString);
        return of(configId, dittoHeaders);
    }

    @Override
    public String getTypePrefix() {
        return WotValidationConfigCommand.TYPE_PREFIX;
    }

    @Override
    public JsonPointer getResourcePath() {
        return JsonPointer.of("/wot/validation/config/merged");
    }

    @Override
    public Command.Category getCategory() {
        return Command.Category.QUERY;
    }

    @Override
    public RetrieveMergedWotValidationConfig setDittoHeaders(final DittoHeaders dittoHeaders) {
        return of(getEntityId(), dittoHeaders);
    }

    @Override
    protected void appendPayload(final JsonObjectBuilder jsonObjectBuilder, final JsonSchemaVersion schemaVersion,
            final Predicate<JsonField> thePredicate) {
        // No additional payload to append
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final RetrieveMergedWotValidationConfig that = (RetrieveMergedWotValidationConfig) o;
        return Objects.equals(getEntityId(), that.getEntityId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEntityId());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" +
                "configId=" + getEntityId() +
                "]";
    }
}
