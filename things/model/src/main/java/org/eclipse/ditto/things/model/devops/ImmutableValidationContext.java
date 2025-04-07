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

package org.eclipse.ditto.things.model.devops;

import org.eclipse.ditto.base.model.json.Jsonifiable;
import org.eclipse.ditto.json.*;

import javax.annotation.concurrent.Immutable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents the context used to match dynamic validation overrides.
 * All fields are optional but must match if specified.
 */
@Immutable
public final class ImmutableValidationContext implements Jsonifiable<JsonObject> {

    private static final String DITTO_HEADERS_PATTERNS_FIELD = "dittoHeadersPatterns";
    private static final String THING_DEF_PATTERNS_FIELD = "thingDefinitionPatterns";
    private static final String FEATURE_DEF_PATTERNS_FIELD = "featureDefinitionPatterns";

    private static final JsonFieldDefinition<JsonArray> DITTO_HEADERS_PATTERNS_POINTER =
            JsonFactory.newJsonArrayFieldDefinition("dittoHeadersPatterns");
    private static final JsonFieldDefinition<JsonArray> THING_DEF_PATTERNS_POINTER =
            JsonFactory.newJsonArrayFieldDefinition("thingDefinitionPatterns");
    private static final JsonFieldDefinition<JsonArray> FEATURE_DEF_PATTERNS_POINTER =
            JsonFactory.newJsonArrayFieldDefinition("featureDefinitionPatterns");


    private final List<Map<String, String>> dittoHeadersPatterns;
    private final List<String> thingDefinitionPatterns;
    private final List<String> featureDefinitionPatterns;

    private ImmutableValidationContext(
            final List<Map<String, String>> dittoHeadersPatterns,
            final List<String> thingDefinitionPatterns,
            final List<String> featureDefinitionPatterns) {
        this.dittoHeadersPatterns = Collections.unmodifiableList(dittoHeadersPatterns);
        this.thingDefinitionPatterns = Collections.unmodifiableList(thingDefinitionPatterns);
        this.featureDefinitionPatterns = Collections.unmodifiableList(featureDefinitionPatterns);
    }

    /**
     * Creates a new {@code ImmutableValidationContext} instance.
     *
     * @param dittoHeadersPatterns list of header pattern maps.
     * @param thingDefinitionPatterns regex strings to match Thing definitions.
     * @param featureDefinitionPatterns regex strings to match Feature definitions.
     * @return the new {@code ImmutableValidationContext}.
     */
    public static ImmutableValidationContext of(
            final List<Map<String, String>> dittoHeadersPatterns,
            final List<String> thingDefinitionPatterns,
            final List<String> featureDefinitionPatterns) {
        return new ImmutableValidationContext(dittoHeadersPatterns, thingDefinitionPatterns, featureDefinitionPatterns);
    }

    /**
     * @return list of header pattern maps to match.
     */
    public List<Map<String, String>> getDittoHeadersPatterns() {
        return dittoHeadersPatterns;
    }

    /**
     * @return list of regex patterns for Thing definition URLs.
     */
    public List<String> getThingDefinitionPatterns() {
        return thingDefinitionPatterns;
    }

    /**
     * @return list of regex patterns for Feature definition URLs.
     */
    public List<String> getFeatureDefinitionPatterns() {
        return featureDefinitionPatterns;
    }

    /**
     * Converts the {@code ImmutableValidationContext} object to a {@link JsonObject}.
     *
     * @return the JSON representation of the permission check.
     */
    @Override
    public JsonObject toJson() {
        final JsonArrayBuilder dittoHeaders = JsonFactory.newArrayBuilder();
        for (final Map<String, String> headerPattern : dittoHeadersPatterns) {
            final JsonObject headerObject = JsonFactory.newObjectBuilder()
                    .setAll(
                            headerPattern.entrySet().stream()
                                    .map(entry -> JsonFactory.newField(
                                            JsonFactory.newKey(entry.getKey()),
                                            JsonFactory.newValue(entry.getValue())
                                    ))
                                    .collect(Collectors.toList())
                    )
                    .build();
            dittoHeaders.add(headerObject);
        }

        return JsonObject.newBuilder()
                .set(DITTO_HEADERS_PATTERNS_FIELD, dittoHeaders.build())
                .set(THING_DEF_PATTERNS_FIELD, JsonFactory.newArrayBuilder()
                        .addAll(thingDefinitionPatterns.stream()
                                .map(JsonFactory::newValue)
                                .collect(Collectors.toList()))
                        .build())
                .set(FEATURE_DEF_PATTERNS_FIELD, JsonFactory.newArrayBuilder()
                        .addAll(featureDefinitionPatterns.stream()
                                .map(JsonFactory::newValue)
                                .collect(Collectors.toList()))
                        .build())
                .build();
    }


    /**
     * Creates an {@code ImmutableValidationContext} from a {@link JsonObject}.
     *
     * @param jsonObject the JSON object to parse.
     * @return the parsed {@link ImmutableValidationContext}.
     */
    public static ImmutableValidationContext fromJson(final JsonObject jsonObject) {
        final JsonArray headerArray = jsonObject.getValueOrThrow(DITTO_HEADERS_PATTERNS_POINTER).asArray();
        final List<Map<String, String>> headers = headerArray.stream()
                .map(JsonValue::asObject)
                .map(jsonObj -> {
                    final Map<String, String> map = new HashMap<>();
                    jsonObj.forEach(field -> map.put(field.getKey().toString(), field.getValue().asString()));
                    return map;
                })
                .collect(Collectors.toList());

        final List<String> thingDefs = jsonObject.getValueOrThrow(THING_DEF_PATTERNS_POINTER)
                .asArray().stream()
                .map(JsonValue::asString)
                .collect(Collectors.toList());

        final List<String> featureDefs = jsonObject.getValueOrThrow(FEATURE_DEF_PATTERNS_POINTER)
                .asArray().stream()
                .map(JsonValue::asString)
                .collect(Collectors.toList());

        return of(headers, thingDefs, featureDefs);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ImmutableValidationContext that = (ImmutableValidationContext) o;
        return Objects.equals(dittoHeadersPatterns, that.dittoHeadersPatterns)
                && Objects.equals(thingDefinitionPatterns, that.thingDefinitionPatterns)
                && Objects.equals(featureDefinitionPatterns, that.featureDefinitionPatterns);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dittoHeadersPatterns, thingDefinitionPatterns, featureDefinitionPatterns);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" +
                "dittoHeadersPatterns=" + dittoHeadersPatterns +
                ", thingDefinitionPatterns=" + thingDefinitionPatterns +
                ", featureDefinitionPatterns=" + featureDefinitionPatterns +
                "]";
    }
}
