package me.itzisonn_.meazy_addon.parser.json_converter.statement;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.Expression;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.parser.ast.statement.WhileStatement;
import me.itzisonn_.meazy.parser.json_converter.Converter;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

public class WhileStatementConverter extends Converter<WhileStatement> {
    public WhileStatementConverter() {
        super(AddonMain.getIdentifier("while_statement"));
    }

    @Override
    public WhileStatement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        Expression condition = jsonDeserializationContext.deserialize(getElement(object, "condition"), Expression.class);

        List<Statement> body = getElement(object, "body").getAsJsonArray().asList().stream().map(statement ->
                (Statement) jsonDeserializationContext.deserialize(statement, Statement.class)).collect(Collectors.toList());

        return new WhileStatement(condition, body);
    }

    @Override
    public JsonElement serialize(WhileStatement whileStatement, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        result.add("condition", jsonSerializationContext.serialize(whileStatement.getCondition()));

        JsonArray body = new JsonArray();
        for (Statement statement : whileStatement.getBody()) {
            body.add(jsonSerializationContext.serialize(statement));
        }
        result.add("body", body);

        return result;
    }
}