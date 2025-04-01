package me.itzisonn_.meazy_addon.parser.json_converter.statement;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.Expression;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.parser.AddonModifiers;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy_addon.parser.ast.statement.ClassDeclarationStatement;
import me.itzisonn_.meazy.parser.json_converter.Converter;
import me.itzisonn_.meazy.parser.json_converter.InvalidCompiledFileException;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class ClassDeclarationStatementConverter extends Converter<ClassDeclarationStatement> {
    public ClassDeclarationStatementConverter() {
        super(AddonMain.getIdentifier("class_declaration_statement"));
    }

    @Override
    public ClassDeclarationStatement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        Set<Modifier> modifiers = getElement(object, "modifiers").getAsJsonArray().asList().stream().map(element -> {
            Modifier modifier = AddonModifiers.parse(element.getAsString());
            if (modifier == null) {
                throw new InvalidCompiledFileException("Unknown Modifier with id " + element.getAsString());
            }
            return modifier;
        }).collect(Collectors.toSet());

        String id = getElement(object, "id").getAsString();

        Set<String> baseClasses;
        if (object.get("base_classes") != null) {
            baseClasses = getElement(object, "base_classes").getAsJsonArray().asList().stream()
                    .map(JsonElement::getAsString).collect(Collectors.toSet());
        }
        else baseClasses = new HashSet<>();

        List<Statement> body = getElement(object, "body").getAsJsonArray().asList().stream().map(statement ->
                (Statement) jsonDeserializationContext.deserialize(statement, Statement.class)).collect(Collectors.toList());

        LinkedHashMap<String, List<Expression>> enumIds = new LinkedHashMap<>();
        if (object.get("enum_ids") != null) {
            Map<String, JsonElement> map = getElement(object, "enum_ids").getAsJsonObject().asMap();
            for (String enumId : new LinkedHashMap<>(map).sequencedKeySet()) {
                enumIds.put(enumId, map.get(enumId).getAsJsonArray().asList().stream().map(expression ->
                        (Expression) jsonDeserializationContext.deserialize(expression, Expression.class)).toList());
            }
        }

        return new ClassDeclarationStatement(modifiers, id, baseClasses, body, enumIds);
    }

    @Override
    public JsonElement serialize(ClassDeclarationStatement classDeclarationStatement, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        JsonArray modifiers = new JsonArray();
        for (Modifier modifier : classDeclarationStatement.getModifiers()) {
            modifiers.add(modifier.getId());
        }
        result.add("modifiers", modifiers);

        result.addProperty("id", classDeclarationStatement.getId());

        if (classDeclarationStatement.getBaseClasses() != null) {
            JsonArray baseClasses = new JsonArray();
            for (String baseClass : classDeclarationStatement.getBaseClasses()) {
                baseClasses.add(baseClass);
            }
            result.add("base_classes", baseClasses);
        }

        JsonArray body = new JsonArray();
        for (Statement statement : classDeclarationStatement.getBody()) {
            body.add(jsonSerializationContext.serialize(statement));
        }
        result.add("body", body);

        JsonObject enumIds = new JsonObject();
        for (String enumId : classDeclarationStatement.getEnumIds().sequencedKeySet()) {
            JsonArray args = new JsonArray();
            for (Expression arg : classDeclarationStatement.getEnumIds().get(enumId)) {
                args.add(jsonSerializationContext.serialize(arg));
            }
            enumIds.add(enumId, args);
        }
        result.add("enum_ids", enumIds);

        return result;
    }
}