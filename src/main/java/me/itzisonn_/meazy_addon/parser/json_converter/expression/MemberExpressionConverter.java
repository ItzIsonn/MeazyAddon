package me.itzisonn_.meazy_addon.parser.json_converter.expression;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.Expression;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.parser.ast.expression.MemberExpression;
import me.itzisonn_.meazy.parser.json_converter.Converter;

import java.lang.reflect.Type;

public class MemberExpressionConverter extends Converter<MemberExpression> {
    public MemberExpressionConverter() {
        super(AddonMain.getIdentifier("member_expression"));
    }

    @Override
    public MemberExpression deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        Expression objectExpression = jsonDeserializationContext.deserialize(getElement(object, "object"), Expression.class);
        Expression member = jsonDeserializationContext.deserialize(getElement(object, "member"), Expression.class);
        boolean isNullSafe = getElement(object, "is_null_safe").getAsBoolean();

        return new MemberExpression(objectExpression, member, isNullSafe);
    }

    @Override
    public JsonElement serialize(MemberExpression memberExpression, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        result.add("object", jsonSerializationContext.serialize(memberExpression.getObject()));
        result.add("member", jsonSerializationContext.serialize(memberExpression.getMember()));
        result.addProperty("is_null_safe", memberExpression.isNullSafe());

        return result;
    }
}