package me.itzisonn_.meazy_addon.parser.json_converter.expression.literal;

import com.google.gson.*;
import me.itzisonn_.meazy_addon.parser.ast.expression.literal.ThisLiteral;
import me.itzisonn_.meazy.parser.json_converter.Converter;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

import java.lang.reflect.Type;

public class ThisLiteralConverter extends Converter<ThisLiteral> {
    public ThisLiteralConverter() {
        super(RegistryIdentifier.ofDefault("this_literal"));
    }

    @Override
    public ThisLiteral deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        return new ThisLiteral();
    }

    @Override
    public JsonElement serialize(ThisLiteral thisLiteral, Type type, JsonSerializationContext jsonSerializationContext) {
        return getJsonObject();
    }
}