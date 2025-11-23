package me.itzisonn_.meazy_addon.parser.json_converter.expression;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.json_converter.Converter;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.parser.ast.expression.PostfixExpression;

import java.lang.reflect.Type;

public class PostfixExpressionConverter extends Converter<PostfixExpression> {
    public PostfixExpressionConverter() {
        super(AddonMain.getIdentifier("postfix_expression"));
    }

    @Override
    public PostfixExpression deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        Expression value = jsonDeserializationContext.deserialize(getElement(object, "value"), Expression.class);
        String operator = getElement(object, "operator").getAsString();

        return new PostfixExpression(value, operator);
    }

    @Override
    public JsonElement serialize(PostfixExpression operatorExpression, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        result.add("value", jsonSerializationContext.serialize(operatorExpression.getValue()));
        result.addProperty("operator", operatorExpression.getOperator());

        return result;
    }
}