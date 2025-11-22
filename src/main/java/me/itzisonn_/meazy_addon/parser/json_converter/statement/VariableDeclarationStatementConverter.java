package me.itzisonn_.meazy_addon.parser.json_converter.statement;

import com.google.gson.*;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.parser.modifier.AddonModifiers;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy_addon.parser.ast.statement.VariableDeclarationStatement;
import me.itzisonn_.meazy.parser.json_converter.Converter;
import me.itzisonn_.meazy.parser.json_converter.InvalidCompiledFileException;

import java.lang.reflect.Type;
import java.util.Set;
import java.util.stream.Collectors;

public class VariableDeclarationStatementConverter extends Converter<VariableDeclarationStatement> {
    public VariableDeclarationStatementConverter() {
        super(AddonMain.getIdentifier("variable_declaration_statement"));
    }

    @Override
    public VariableDeclarationStatement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        Set<Modifier> modifiers = getElement(object, "modifiers").getAsJsonArray().asList().stream().map(element -> {
            Modifier modifier = AddonModifiers.parse(element.getAsString());
            if (modifier == null) {
                throw new InvalidCompiledFileException("Unknown Modifier with id " + element.getAsString());
            }
            return modifier;
        }).collect(Collectors.toSet());

        boolean isConstant = getElement(object, "is_constant").getAsBoolean();

        String id = getElement(object, "id").getAsString();

        JsonObject dataTypeObject = getElement(object, "data_type").getAsJsonObject();
        String dataTypeId = getElement(dataTypeObject, "id", "data_type.id").getAsString();
        boolean dataTypeIsNullable = getElement(dataTypeObject, "is_nullable", "data_type.is_nullable").getAsBoolean();

        Expression value = null;
        if (object.get("value") != null) {
            value = jsonDeserializationContext.deserialize(object.get("value"), Expression.class);
        }

        return new VariableDeclarationStatement(modifiers, isConstant, id, Registries.DATA_TYPE_FACTORY.getEntry().getValue().create(dataTypeId, dataTypeIsNullable), value);
    }

    @Override
    public JsonElement serialize(VariableDeclarationStatement variableDeclarationStatement, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        JsonArray modifiers = new JsonArray();
        for (Modifier modifier : variableDeclarationStatement.getModifiers()) {
            modifiers.add(modifier.getId());
        }
        result.add("modifiers", modifiers);

        result.addProperty("is_constant", variableDeclarationStatement.isConstant());
        result.addProperty("id", variableDeclarationStatement.getId());
        if (variableDeclarationStatement.getValue() != null) result.add("value", jsonSerializationContext.serialize(variableDeclarationStatement.getValue()));

        JsonObject dataTypeObject = new JsonObject();
        dataTypeObject.addProperty("id", variableDeclarationStatement.getDataType().getId());
        dataTypeObject.addProperty("is_nullable", variableDeclarationStatement.getDataType().isNullable());
        result.add("data_type", dataTypeObject);

        return result;
    }
}