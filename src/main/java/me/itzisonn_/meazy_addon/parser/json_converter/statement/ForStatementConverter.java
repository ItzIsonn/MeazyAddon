package me.itzisonn_.meazy_addon.parser.json_converter.statement;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.parser.ast.expression.AssignmentExpression;
import me.itzisonn_.meazy.parser.ast.Expression;
import me.itzisonn_.meazy_addon.parser.ast.statement.ForStatement;
import me.itzisonn_.meazy_addon.parser.ast.statement.VariableDeclarationStatement;
import me.itzisonn_.meazy.parser.json_converter.Converter;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

public class ForStatementConverter extends Converter<ForStatement> {
    public ForStatementConverter() {
        super(AddonMain.getIdentifier("for_statement"));
    }

    @Override
    public ForStatement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        VariableDeclarationStatement variableDeclarationStatement = null;
        if (object.get("variable_declaration") != null) {
            variableDeclarationStatement = jsonDeserializationContext.deserialize(object.get("variable_declaration"), VariableDeclarationStatement.class);
        }

        Expression condition = null;
        if (object.get("condition") != null) {
            condition = jsonDeserializationContext.deserialize(object.get("condition"), Expression.class);
        }

        AssignmentExpression assignmentExpression = null;
        if (object.get("assignment_expression") != null) {
            assignmentExpression = jsonDeserializationContext.deserialize(object.get("assignment_expression"), AssignmentExpression.class);
        }

        List<Statement> body = getElement(object, "body").getAsJsonArray().asList().stream().map(statement ->
                (Statement) jsonDeserializationContext.deserialize(statement, Statement.class)).collect(Collectors.toList());

        return new ForStatement(variableDeclarationStatement, condition, assignmentExpression, body);
    }

    @Override
    public JsonElement serialize(ForStatement forStatement, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        if (forStatement.getVariableDeclarationStatement() != null)
            result.add("variable_declaration", jsonSerializationContext.serialize(forStatement.getVariableDeclarationStatement()));
        if (forStatement.getCondition() != null)
            result.add("condition", jsonSerializationContext.serialize(forStatement.getCondition()));
        if (forStatement.getAssignmentExpression() != null)
            result.add("assignment_expression", jsonSerializationContext.serialize(forStatement.getAssignmentExpression()));

        JsonArray body = new JsonArray();
        for (Statement statement : forStatement.getBody()) {
            body.add(jsonSerializationContext.serialize(statement, Statement.class));
        }
        result.add("body", body);

        return result;
    }
}