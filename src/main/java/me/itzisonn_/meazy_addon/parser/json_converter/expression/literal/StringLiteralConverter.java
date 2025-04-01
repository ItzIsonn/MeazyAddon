package me.itzisonn_.meazy_addon.parser.json_converter.expression.literal;

import com.google.gson.*;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.parser.ast.expression.literal.StringLiteral;
import me.itzisonn_.meazy.parser.json_converter.Converter;

import java.lang.reflect.Type;

public class StringLiteralConverter extends Converter<StringLiteral> {
    public StringLiteralConverter() {
        super(AddonMain.getIdentifier("string_literal"));
    }

    @Override
    public StringLiteral deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        return new StringLiteral(getElement(object, "value").getAsString());
    }

    @Override
    public JsonElement serialize(StringLiteral stringLiteral, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        result.addProperty("value", stringLiteral.getValue());

        return result;
    }
}