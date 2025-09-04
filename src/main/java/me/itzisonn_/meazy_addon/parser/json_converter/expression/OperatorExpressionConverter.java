package me.itzisonn_.meazy_addon.parser.json_converter.expression;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.parser.ast.expression.OperatorExpression;
import me.itzisonn_.meazy.parser.json_converter.Converter;
import me.itzisonn_.meazy.parser.json_converter.InvalidCompiledFileException;
import me.itzisonn_.meazy.parser.operator.Operator;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.registry.RegistryEntry;
import me.itzisonn_.registry.RegistryIdentifier;

import java.lang.reflect.Type;

public class OperatorExpressionConverter extends Converter<OperatorExpression> {
    public OperatorExpressionConverter() {
        super(AddonMain.getIdentifier("operator_expression"));
    }

    @Override
    public OperatorExpression deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        Expression left = jsonDeserializationContext.deserialize(getElement(object, "left"), Expression.class);

        Expression right;
        if (object.get("right") != null) right = jsonDeserializationContext.deserialize(getElement(object, "right"), Expression.class);
        else right = null;

        String operatorId = getElement(object, "operator").getAsString();
        RegistryEntry<Operator> operatorEntry = Registries.OPERATORS.getEntry(RegistryIdentifier.of(operatorId));
        if (operatorEntry == null) throw new InvalidCompiledFileException("Can't find operator " + operatorId);

        return new OperatorExpression(left, right, operatorEntry.getValue());
    }

    @Override
    public JsonElement serialize(OperatorExpression operatorExpression, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        result.add("left", jsonSerializationContext.serialize(operatorExpression.getLeft()));
        if (operatorExpression.getRight() != null) result.add("right", jsonSerializationContext.serialize(operatorExpression.getRight()));

        RegistryEntry<Operator> operatorEntry = Registries.OPERATORS.getEntry(operatorExpression.getOperator());
        if (operatorEntry == null) throw new InvalidCompiledFileException("Can't find operator " + operatorExpression.getOperator().getSymbol());
        result.addProperty("operator", operatorEntry.getIdentifier().toString());

        return result;
    }
}