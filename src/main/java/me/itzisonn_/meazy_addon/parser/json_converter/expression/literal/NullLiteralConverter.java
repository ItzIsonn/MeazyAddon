package me.itzisonn_.meazy_addon.parser.json_converter.expression.literal;

import com.google.gson.*;
import me.itzisonn_.meazy_addon.parser.ast.expression.literal.NullLiteral;
import me.itzisonn_.meazy.parser.json_converter.Converter;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

import java.lang.reflect.Type;

public class NullLiteralConverter extends Converter<NullLiteral> {
    public NullLiteralConverter() {
        super(RegistryIdentifier.ofDefault("null_literal"));
    }

    @Override
    public NullLiteral deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        return new NullLiteral();
    }

    @Override
    public JsonElement serialize(NullLiteral nullLiteral, Type type, JsonSerializationContext jsonSerializationContext) {
        return getJsonObject();
    }
}