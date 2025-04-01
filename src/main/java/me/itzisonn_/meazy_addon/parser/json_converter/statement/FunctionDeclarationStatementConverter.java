package me.itzisonn_.meazy_addon.parser.json_converter.statement;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.parser.AddonModifiers;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.CallArgExpression;
import me.itzisonn_.meazy_addon.parser.ast.statement.FunctionDeclarationStatement;
import me.itzisonn_.meazy.parser.json_converter.Converter;
import me.itzisonn_.meazy.parser.json_converter.InvalidCompiledFileException;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FunctionDeclarationStatementConverter extends Converter<FunctionDeclarationStatement> {
    public FunctionDeclarationStatementConverter() {
        super(AddonMain.getIdentifier("function_declaration_statement"));
    }

    @Override
    public FunctionDeclarationStatement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
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

        String classId;
        if (object.get("class_id") != null) {
            classId = getElement(object, "class_id").getAsString();
        }
        else classId = null;

        List<CallArgExpression> args = getElement(object, "args").getAsJsonArray().asList().stream().map(arg ->
                (CallArgExpression) jsonDeserializationContext.deserialize(arg, CallArgExpression.class)).collect(Collectors.toList());

        List<Statement> body = getElement(object, "body").getAsJsonArray().asList().stream().map(statement ->
                (Statement) jsonDeserializationContext.deserialize(statement, Statement.class)).collect(Collectors.toList());

        DataType dataType;
        if (object.get("return_data_type") != null) {
            JsonObject dataTypeObject = getElement(object, "return_data_type").getAsJsonObject();
            String dataTypeId = getElement(dataTypeObject, "id", "return_data_type.id").getAsString();
            boolean dataTypeIsNullable = getElement(dataTypeObject, "is_nullable", "return_data_type.is_nullable").getAsBoolean();
            dataType = new DataType(dataTypeId, dataTypeIsNullable);
        }
        else dataType = null;

        return new FunctionDeclarationStatement(modifiers, id, classId, args, body, dataType);
    }

    @Override
    public JsonElement serialize(FunctionDeclarationStatement functionDeclarationStatement, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        JsonArray modifiers = new JsonArray();
        for (Modifier modifier : functionDeclarationStatement.getModifiers()) {
            modifiers.add(modifier.getId());
        }
        result.add("modifiers", modifiers);

        result.addProperty("id", functionDeclarationStatement.getId());

        if (functionDeclarationStatement.getClassId() != null) result.addProperty("class_id", functionDeclarationStatement.getClassId());

        JsonArray args = new JsonArray();
        for (CallArgExpression arg : functionDeclarationStatement.getArgs()) {
            args.add(jsonSerializationContext.serialize(arg));
        }
        result.add("args", args);

        JsonArray body = new JsonArray();
        for (Statement statement : functionDeclarationStatement.getBody()) {
            body.add(jsonSerializationContext.serialize(statement));
        }
        result.add("body", body);

        if (functionDeclarationStatement.getReturnDataType() != null) {
            JsonObject dataTypeObject = new JsonObject();
            dataTypeObject.addProperty("id", functionDeclarationStatement.getReturnDataType().getId());
            dataTypeObject.addProperty("is_nullable", functionDeclarationStatement.getReturnDataType().isNullable());
            result.add("return_data_type", dataTypeObject);
        }

        return result;
    }
}