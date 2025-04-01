package me.itzisonn_.meazy_addon.parser.json_converter.statement;

import com.google.gson.*;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.parser.ast.statement.ContinueStatement;
import me.itzisonn_.meazy.parser.json_converter.Converter;

import java.lang.reflect.Type;

public class ContinueStatementConverter extends Converter<ContinueStatement> {
    public ContinueStatementConverter() {
        super(AddonMain.getIdentifier("continue_statement"));
    }

    @Override
    public ContinueStatement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        return new ContinueStatement();
    }

    @Override
    public JsonElement serialize(ContinueStatement continueStatement, Type type, JsonSerializationContext jsonSerializationContext) {
        return getJsonObject();
    }
}