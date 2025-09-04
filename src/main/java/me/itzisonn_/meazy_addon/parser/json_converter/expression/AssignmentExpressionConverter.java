package me.itzisonn_.meazy_addon.parser.json_converter.expression;

import com.google.gson.*;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.parser.ast.expression.AssignmentExpression;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.json_converter.Converter;

import java.lang.reflect.Type;

public class AssignmentExpressionConverter extends Converter<AssignmentExpression> {
    public AssignmentExpressionConverter() {
        super(AddonMain.getIdentifier("assignment_expression"));
    }

    @Override
    public AssignmentExpression deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        Expression id = jsonDeserializationContext.deserialize(getElement(object, "id"), Expression.class);
        Expression value = jsonDeserializationContext.deserialize(getElement(object, "value"), Expression.class);

        return new AssignmentExpression(id, value);
    }

    @Override
    public JsonElement serialize(AssignmentExpression assignmentExpression, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        result.add("id", jsonSerializationContext.serialize(assignmentExpression.getId()));
        result.add("value", jsonSerializationContext.serialize(assignmentExpression.getValue()));

        return result;
    }
}