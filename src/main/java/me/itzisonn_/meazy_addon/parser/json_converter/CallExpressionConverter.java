package me.itzisonn_.meazy_addon.parser.json_converter;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.expression.Identifier;
import me.itzisonn_.meazy.parser.json_converter.Converter;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.parser.ast.expression.CallExpression;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

public class CallExpressionConverter extends Converter<CallExpression> {
    public CallExpressionConverter() {
        super(AddonMain.getIdentifier("function_call_expression"));
    }

    @Override
    public CallExpression deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        Identifier caller = jsonDeserializationContext.deserialize(getElement(object, "caller"), Identifier.class);

        List<Expression> args = getElement(object, "args").getAsJsonArray().asList().stream().map(arg ->
                (Expression) jsonDeserializationContext.deserialize(arg, Expression.class)).collect(Collectors.toList());

        return new CallExpression(caller, args);
    }

    @Override
    public JsonElement serialize(CallExpression callExpression, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        result.add("caller", jsonSerializationContext.serialize(callExpression.getCaller()));

        JsonArray args = new JsonArray();
        for (Expression arg : callExpression.getArgs()) {
            args.add(jsonSerializationContext.serialize(arg));
        }
        result.add("args", args);

        return result;
    }
}