package me.itzisonn_.meazy_addon.parser.json_converter.statement;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.json_converter.Converter;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.parser.ast.statement.UsingStatement;

import java.lang.reflect.Type;

public class UsingStatementConverter extends Converter<UsingStatement> {
    public UsingStatementConverter() {
        super(AddonMain.getIdentifier("using_statement"));
    }

    @Override
    public UsingStatement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        return new UsingStatement(getElement(object, "class_name").getAsString());
    }

    @Override
    public JsonElement serialize(UsingStatement usingStatement, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();
        result.addProperty("class_name", usingStatement.getClassName());
        return result;
    }
}