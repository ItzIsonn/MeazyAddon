package me.itzisonn_.meazy_addon.parser.json_converter.expression.call_expression;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.Expression;
import me.itzisonn_.meazy_addon.parser.ast.expression.call_expression.FunctionCallExpression;
import me.itzisonn_.meazy_addon.parser.ast.expression.identifier.FunctionIdentifier;
import me.itzisonn_.meazy.parser.json_converter.Converter;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

public class FunctionCallExpressionConverter extends Converter<FunctionCallExpression> {
    public FunctionCallExpressionConverter() {
        super(RegistryIdentifier.ofDefault("function_call_expression"));
    }

    @Override
    public FunctionCallExpression deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        FunctionIdentifier caller = jsonDeserializationContext.deserialize(getElement(object, "caller"), FunctionIdentifier.class);

        List<Expression> args = getElement(object, "args").getAsJsonArray().asList().stream().map(arg ->
                (Expression) jsonDeserializationContext.deserialize(arg, Expression.class)).collect(Collectors.toList());

        return new FunctionCallExpression(caller, args);
    }

    @Override
    public JsonElement serialize(FunctionCallExpression functionCallExpression, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        result.add("caller", jsonSerializationContext.serialize(functionCallExpression.getCaller()));

        JsonArray args = new JsonArray();
        for (Expression arg : functionCallExpression.getArgs()) {
            args.add(jsonSerializationContext.serialize(arg));
        }
        result.add("args", args);

        return result;
    }
}