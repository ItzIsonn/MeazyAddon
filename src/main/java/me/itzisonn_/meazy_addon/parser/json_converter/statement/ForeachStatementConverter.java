package me.itzisonn_.meazy_addon.parser.json_converter.statement;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.parser.ast.statement.ForeachStatement;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy_addon.parser.ast.statement.VariableDeclarationStatement;
import me.itzisonn_.meazy.parser.json_converter.Converter;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

public class ForeachStatementConverter extends Converter<ForeachStatement> {
    public ForeachStatementConverter() {
        super(AddonMain.getIdentifier("foreach_statement"));
    }

    @Override
    public ForeachStatement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        VariableDeclarationStatement variableDeclaration = jsonDeserializationContext.deserialize(getElement(object, "variable_declaration"), VariableDeclarationStatement.class);

        Expression collection = jsonDeserializationContext.deserialize(getElement(object, "collection"), Expression.class);

        List<Statement> body = getElement(object, "body").getAsJsonArray().asList().stream().map(statement ->
                (Statement) jsonDeserializationContext.deserialize(statement, Statement.class)).collect(Collectors.toList());

        return new ForeachStatement(variableDeclaration, collection, body);
    }

    @Override
    public JsonElement serialize(ForeachStatement foreachStatement, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        result.add("variable_declaration", jsonSerializationContext.serialize(foreachStatement.getVariableDeclarationStatement()));
        result.add("collection", jsonSerializationContext.serialize(foreachStatement.getCollection()));

        JsonArray body = new JsonArray();
        for (Statement statement : foreachStatement.getBody()) {
            body.add(jsonSerializationContext.serialize(statement, Statement.class));
        }
        result.add("body", body);

        return result;
    }
}