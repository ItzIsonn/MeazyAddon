package me.itzisonn_.meazy_addon.parser.json_converter.expression.call_expression;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.Expression;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.parser.ast.expression.call_expression.ClassCallExpression;
import me.itzisonn_.meazy_addon.parser.ast.expression.identifier.ClassIdentifier;
import me.itzisonn_.meazy.parser.json_converter.Converter;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

public class ClassCallExpressionConverter extends Converter<ClassCallExpression> {
    public ClassCallExpressionConverter() {
        super(AddonMain.getIdentifier("class_call_expression"));
    }

    @Override
    public ClassCallExpression deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        ClassIdentifier caller = jsonDeserializationContext.deserialize(getElement(object, "caller"), ClassIdentifier.class);

        List<Expression> args = getElement(object, "args").getAsJsonArray().asList().stream().map(arg ->
                (Expression) jsonDeserializationContext.deserialize(arg, Expression.class)).collect(Collectors.toList());

        return new ClassCallExpression(caller, args);
    }

    @Override
    public JsonElement serialize(ClassCallExpression classCallExpression, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        result.add("caller", jsonSerializationContext.serialize(classCallExpression.getCaller()));

        JsonArray args = new JsonArray();
        for (Expression arg : classCallExpression.getArgs()) {
            args.add(jsonSerializationContext.serialize(arg));
        }
        result.add("args", args);

        return result;
    }
}