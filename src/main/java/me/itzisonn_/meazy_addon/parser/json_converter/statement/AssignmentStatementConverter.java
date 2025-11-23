package me.itzisonn_.meazy_addon.parser.json_converter.statement;

import com.google.gson.*;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.parser.ast.statement.AssignmentStatement;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.json_converter.Converter;

import java.lang.reflect.Type;

public class AssignmentStatementConverter extends Converter<AssignmentStatement> {
    public AssignmentStatementConverter() {
        super(AddonMain.getIdentifier("assignment_statement"));
    }

    @Override
    public AssignmentStatement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        Expression id = jsonDeserializationContext.deserialize(getElement(object, "id"), Expression.class);
        Expression value = jsonDeserializationContext.deserialize(getElement(object, "value"), Expression.class);

        return new AssignmentStatement(id, value);
    }

    @Override
    public JsonElement serialize(AssignmentStatement assignmentStatement, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        result.add("id", jsonSerializationContext.serialize(assignmentStatement.getId()));
        result.add("value", jsonSerializationContext.serialize(assignmentStatement.getValue()));

        return result;
    }
}