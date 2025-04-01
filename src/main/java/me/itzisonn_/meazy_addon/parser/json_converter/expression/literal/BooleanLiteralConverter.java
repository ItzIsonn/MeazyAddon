package me.itzisonn_.meazy_addon.parser.json_converter.expression.literal;

import com.google.gson.*;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.parser.ast.expression.literal.BooleanLiteral;
import me.itzisonn_.meazy.parser.json_converter.Converter;

import java.lang.reflect.Type;

public class BooleanLiteralConverter extends Converter<BooleanLiteral> {
    public BooleanLiteralConverter() {
        super(AddonMain.getIdentifier("boolean_literal"));
    }

    @Override
    public BooleanLiteral deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        return new BooleanLiteral(getElement(object, "value").getAsBoolean());
    }

    @Override
    public JsonElement serialize(BooleanLiteral booleanLiteral, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        result.addProperty("value", booleanLiteral.isValue());

        return result;
    }
}