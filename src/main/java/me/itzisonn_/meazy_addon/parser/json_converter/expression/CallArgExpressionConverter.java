package me.itzisonn_.meazy_addon.parser.json_converter.expression;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.CallArgExpression;
import me.itzisonn_.meazy.parser.json_converter.Converter;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

import java.lang.reflect.Type;

public class CallArgExpressionConverter extends Converter<CallArgExpression> {
    public CallArgExpressionConverter() {
        super(RegistryIdentifier.ofDefault("call_arg_expression"));
    }

    @Override
    public CallArgExpression deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        String id = getElement(object, "id").getAsString();

        JsonObject dataTypeObject = getElement(object, "data_type").getAsJsonObject();
        String dataTypeId = getElement(dataTypeObject, "id", "data_type.id").getAsString();
        boolean dataTypeIsNullable = getElement(dataTypeObject, "is_nullable", "data_type.is_nullable").getAsBoolean();
        DataType dataType = new DataType(dataTypeId, dataTypeIsNullable);

        boolean isConstant = getElement(object, "is_constant").getAsBoolean();

        return new CallArgExpression(id, dataType, isConstant);
    }

    @Override
    public JsonElement serialize(CallArgExpression callArgExpression, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        result.addProperty("id", callArgExpression.getId());

        JsonObject dataTypeObject = new JsonObject();
        dataTypeObject.addProperty("id", callArgExpression.getDataType().getId());
        dataTypeObject.addProperty("is_nullable", callArgExpression.getDataType().isNullable());
        result.add("data_type", dataTypeObject);

        result.addProperty("is_constant", callArgExpression.isConstant());

        return result;
    }
}