package me.itzisonn_.meazy_addon.parser.json_converter.expression.identifier;

import com.google.gson.*;
import me.itzisonn_.meazy_addon.parser.ast.expression.identifier.VariableIdentifier;
import me.itzisonn_.meazy.parser.json_converter.Converter;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

import java.lang.reflect.Type;

public class VariableIdentifierConverter extends Converter<VariableIdentifier> {
    public VariableIdentifierConverter() {
        super(RegistryIdentifier.ofDefault("variable_identifier"));
    }

    @Override
    public VariableIdentifier deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        return new VariableIdentifier(getElement(object, "id").getAsString());
    }

    @Override
    public JsonElement serialize(VariableIdentifier variableIdentifier, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        result.addProperty("id", variableIdentifier.getId());

        return result;
    }
}