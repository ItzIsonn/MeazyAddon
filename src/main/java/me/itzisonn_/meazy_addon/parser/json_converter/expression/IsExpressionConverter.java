package me.itzisonn_.meazy_addon.parser.json_converter.expression;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.Expression;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.parser.ast.expression.IsExpression;
import me.itzisonn_.meazy.parser.json_converter.Converter;

import java.lang.reflect.Type;

public class IsExpressionConverter extends Converter<IsExpression> {
    public IsExpressionConverter() {
        super(AddonMain.getIdentifier("is_expression"));
    }

    @Override
    public IsExpression deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        Expression value = jsonDeserializationContext.deserialize(getElement(object, "value"), Expression.class);
        String dataType = getElement(object, "data_type").getAsString();
        boolean isLike = getElement(object, "is_like").getAsBoolean();

        return new IsExpression(value, dataType, isLike);
    }

    @Override
    public JsonElement serialize(IsExpression isExpression, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        result.add("value", jsonSerializationContext.serialize(isExpression.getValue()));
        result.addProperty("data_type", isExpression.getDataType());
        result.addProperty("is_like", isExpression.isLike());

        return result;
    }
}