package me.itzisonn_.meazy_addon.parser.json_converter.statement;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.parser.modifier.AddonModifiers;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy_addon.parser.ast.statement.VariableDeclarationStatement;
import me.itzisonn_.meazy.parser.json_converter.Converter;
import me.itzisonn_.meazy.parser.json_converter.InvalidCompiledFileException;
import me.itzisonn_.meazy_addon.parser.data_type.DataTypeImpl;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class VariableDeclarationConverter extends Converter<VariableDeclarationStatement> {
    public VariableDeclarationConverter() {
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

        List<VariableDeclarationStatement.VariableDeclarationInfo> declarationInfos = getElement(object, "declaration_infos").getAsJsonArray().asList().stream().map(element -> {
            JsonObject declarationObject = element.getAsJsonObject();
            String id = getElement(declarationObject, "id").getAsString();

            JsonObject dataTypeObject = getElement(declarationObject, "data_type").getAsJsonObject();
            String dataTypeId = getElement(dataTypeObject, "id", "data_type.id").getAsString();
            boolean dataTypeIsNullable = getElement(dataTypeObject, "is_nullable", "data_type.is_nullable").getAsBoolean();

            Expression value = null;
            if (declarationObject.get("value") != null) {
                value = jsonDeserializationContext.deserialize(declarationObject.get("value"), Expression.class);
            }

            return new VariableDeclarationStatement.VariableDeclarationInfo(id, new DataTypeImpl(dataTypeId, dataTypeIsNullable), value);
        }).toList();

        return new VariableDeclarationStatement(modifiers, isConstant, declarationInfos);
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

        JsonArray declarationInfos = new JsonArray();
        for (VariableDeclarationStatement.VariableDeclarationInfo declarationInfo : variableDeclarationStatement.getDeclarationInfos()) {
            JsonObject declarationObject = new JsonObject();
            declarationObject.addProperty("id", declarationInfo.getId());
            if (declarationInfo.getValue() != null) declarationObject.add("value", jsonSerializationContext.serialize(declarationInfo.getValue()));

            JsonObject dataTypeObject = new JsonObject();
            dataTypeObject.addProperty("id", declarationInfo.getDataType().getId());
            dataTypeObject.addProperty("is_nullable", declarationInfo.getDataType().isNullable());
            declarationObject.add("data_type", dataTypeObject);

            declarationInfos.add(declarationObject);
        }
        result.add("declaration_infos", declarationInfos);


        return result;
    }
}