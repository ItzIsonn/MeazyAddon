package me.itzisonn_.meazy_addon.parser.json_converter.expression.literal;

import com.google.gson.*;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.parser.ast.expression.literal.NumberLiteral;
import me.itzisonn_.meazy.parser.json_converter.Converter;
import me.itzisonn_.meazy.parser.json_converter.InvalidCompiledFileException;

import java.lang.reflect.Type;

public class NumberLiteralConverter extends Converter<NumberLiteral> {
    public NumberLiteralConverter() {
        super(AddonMain.getIdentifier("number_literal"));
    }

    @Override
    public NumberLiteral deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        if (object.get("value") == null) throw new InvalidCompiledFileException(getIdentifier(), "value");
        String value = object.get("value").getAsString();

        return new NumberLiteral(value);
    }

    @Override
    public JsonElement serialize(NumberLiteral numberLiteral, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        result.addProperty("value", numberLiteral.getValue());

        return result;
    }
}