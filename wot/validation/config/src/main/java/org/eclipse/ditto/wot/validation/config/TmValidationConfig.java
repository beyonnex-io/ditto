package org.eclipse.ditto.wot.validation.config;

import org.eclipse.ditto.base.model.json.FieldType;
import org.eclipse.ditto.base.model.json.JsonSchemaVersion;
import org.eclipse.ditto.base.model.json.Jsonifiable;
import org.eclipse.ditto.json.JsonField;
import org.eclipse.ditto.json.JsonObject;

import java.util.function.Predicate;

public class TmValidationConfig implements Jsonifiable.WithFieldSelectorAndPredicate<JsonField> {

    @Override
    public JsonObject toJson() {
        return toJson(FieldType.notHidden());
    }

    @Override
    public JsonObject toJson(final JsonSchemaVersion schemaVersion, final Predicate<JsonField> predicate) {
        // TODO: Implement proper JSON serialization
        return JsonObject.empty();
    }
} 