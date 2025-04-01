package me.itzisonn_.meazy_addon.parser.json_converter.statement;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.Expression;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.parser.ast.statement.BaseCallStatement;
import me.itzisonn_.meazy.parser.json_converter.Converter;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

public class BaseCallStatementConverter extends Converter<BaseCallStatement> {
    public BaseCallStatementConverter() {
        super(AddonMain.getIdentifier("base_call_statement"));
    }

    @Override
    public BaseCallStatement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        String id = getElement(object, "id").getAsString();

        List<Expression> args = getElement(object, "args").getAsJsonArray().asList().stream().map(arg ->
                (Expression) jsonDeserializationContext.deserialize(arg, Expression.class)).collect(Collectors.toList());

        return new BaseCallStatement(id, args);
    }

    @Override
    public JsonElement serialize(BaseCallStatement baseCallStatement, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        result.addProperty("id", baseCallStatement.getId());

        JsonArray args = new JsonArray();
        for (Expression arg : baseCallStatement.getArgs()) {
            args.add(jsonSerializationContext.serialize(arg));
        }
        result.add("args", args);

        return result;
    }
}