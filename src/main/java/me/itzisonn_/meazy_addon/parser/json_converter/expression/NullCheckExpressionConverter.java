package me.itzisonn_.meazy_addon.parser.json_converter.expression;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.Expression;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.parser.ast.expression.NullCheckExpression;
import me.itzisonn_.meazy.parser.json_converter.Converter;

import java.lang.reflect.Type;

public class NullCheckExpressionConverter extends Converter<NullCheckExpression> {
    public NullCheckExpressionConverter() {
        super(AddonMain.getIdentifier("null_check_expression"));
    }

    @Override
    public NullCheckExpression deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        Expression checkExpression = jsonDeserializationContext.deserialize(getElement(object, "check_expression"), Expression.class);
        Expression nullExpression = jsonDeserializationContext.deserialize(getElement(object, "null_expression"), Expression.class);

        return new NullCheckExpression(checkExpression, nullExpression);
    }

    @Override
    public JsonElement serialize(NullCheckExpression nullCheckExpression, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        result.add("check_expression", jsonSerializationContext.serialize(nullCheckExpression.getCheckExpression()));
        result.add("null_expression", jsonSerializationContext.serialize(nullCheckExpression.getNullExpression()));

        return result;
    }
}