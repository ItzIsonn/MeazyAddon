package me.itzisonn_.meazy_addon.parser.json_converter.statement;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.parser.ast.statement.ReturnStatement;
import me.itzisonn_.meazy.parser.json_converter.Converter;

import java.lang.reflect.Type;

public class ReturnStatementConverter extends Converter<ReturnStatement> {
    public ReturnStatementConverter() {
        super(AddonMain.getIdentifier("return_statement"));
    }

    @Override
    public ReturnStatement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        Expression value = null;
        if (object.get("value") != null) {
            value = jsonDeserializationContext.deserialize(object.get("value"), Expression.class);
        }

        return new ReturnStatement(value);
    }

    @Override
    public JsonElement serialize(ReturnStatement returnStatement, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        if (returnStatement.getValue() != null) result.add("value", jsonSerializationContext.serialize(returnStatement.getValue()));

        return result;
    }
}