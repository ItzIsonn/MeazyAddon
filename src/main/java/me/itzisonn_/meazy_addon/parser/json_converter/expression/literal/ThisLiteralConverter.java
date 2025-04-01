package me.itzisonn_.meazy_addon.parser.json_converter.expression.literal;

import com.google.gson.*;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.parser.ast.expression.literal.ThisLiteral;
import me.itzisonn_.meazy.parser.json_converter.Converter;

import java.lang.reflect.Type;

public class ThisLiteralConverter extends Converter<ThisLiteral> {
    public ThisLiteralConverter() {
        super(AddonMain.getIdentifier("this_literal"));
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