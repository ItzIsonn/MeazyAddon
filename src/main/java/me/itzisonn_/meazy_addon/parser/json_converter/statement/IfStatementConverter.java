package me.itzisonn_.meazy_addon.parser.json_converter.statement;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.Expression;
import me.itzisonn_.meazy_addon.parser.ast.statement.IfStatement;
import me.itzisonn_.meazy.parser.json_converter.Converter;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

public class IfStatementConverter extends Converter<IfStatement> {
    public IfStatementConverter() {
        super(RegistryIdentifier.ofDefault("if_statement"));
    }

    @Override
    public IfStatement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        Expression condition;
        if (object.get("condition") != null) {
            condition = jsonDeserializationContext.deserialize(getElement(object, "condition"), Expression.class);
        }
        else condition = null;

        List<Statement> body = getElement(object, "body").getAsJsonArray().asList().stream().map(statement ->
                (Statement) jsonDeserializationContext.deserialize(statement, Statement.class)).collect(Collectors.toList());

        IfStatement elseStatement = null;
        if (object.get("else_statement") != null) {
            elseStatement = jsonDeserializationContext.deserialize(object.get("else_statement"), IfStatement.class);
        }

        return new IfStatement(condition, body, elseStatement);
    }

    @Override
    public JsonElement serialize(IfStatement ifStatement, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        if (ifStatement.getCondition() != null) result.add("condition", jsonSerializationContext.serialize(ifStatement.getCondition()));

        JsonArray body = new JsonArray();
        for (Statement statement : ifStatement.getBody()) {
            body.add(jsonSerializationContext.serialize(statement));
        }
        result.add("body", body);

        if (ifStatement.getElseStatement() != null) {
            result.add("else_statement", jsonSerializationContext.serialize(ifStatement.getElseStatement()));
        }

        return result;
    }
}