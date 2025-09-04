package me.itzisonn_.meazy_addon.parser.json_converter.statement;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.parser.modifier.AddonModifiers;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy_addon.parser.ast.statement.ConstructorDeclarationStatement;
import me.itzisonn_.meazy.parser.json_converter.Converter;
import me.itzisonn_.meazy.parser.json_converter.InvalidCompiledFileException;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ConstructorDeclarationStatementConverter extends Converter<ConstructorDeclarationStatement> {
    public ConstructorDeclarationStatementConverter() {
        super(AddonMain.getIdentifier("constructor_declaration_statement"));
    }

    @Override
    public ConstructorDeclarationStatement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        Set<Modifier> modifiers = getElement(object, "modifiers").getAsJsonArray().asList().stream().map(element -> {
            Modifier modifier = AddonModifiers.parse(element.getAsString());
            if (modifier == null) {
                throw new InvalidCompiledFileException("Unknown Modifier with id " + element.getAsString());
            }
            return modifier;
        }).collect(Collectors.toSet());

        List<CallArgExpression> args = getElement(object, "args").getAsJsonArray().asList().stream().map(arg ->
                (CallArgExpression) jsonDeserializationContext.deserialize(arg, CallArgExpression.class)).collect(Collectors.toList());

        List<Statement> body = getElement(object, "body").getAsJsonArray().asList().stream().map(statement ->
                (Statement) jsonDeserializationContext.deserialize(statement, Statement.class)).collect(Collectors.toList());

        return new ConstructorDeclarationStatement(modifiers, args, body);
    }

    @Override
    public JsonElement serialize(ConstructorDeclarationStatement constructorDeclarationStatement, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        JsonArray modifiers = new JsonArray();
        for (Modifier modifier : constructorDeclarationStatement.getModifiers()) {
            modifiers.add(modifier.getId());
        }
        result.add("modifiers", modifiers);

        JsonArray args = new JsonArray();
        for (CallArgExpression arg : constructorDeclarationStatement.getArgs()) {
            args.add(jsonSerializationContext.serialize(arg));
        }
        result.add("args", args);

        JsonArray body = new JsonArray();
        for (Statement statement : constructorDeclarationStatement.getBody()) {
            body.add(jsonSerializationContext.serialize(statement));
        }
        result.add("body", body);

        return result;
    }
}