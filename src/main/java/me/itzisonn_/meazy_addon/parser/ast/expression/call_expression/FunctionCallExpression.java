package me.itzisonn_.meazy_addon.parser.ast.expression.call_expression;

import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy_addon.parser.ast.expression.identifier.FunctionIdentifier;

import java.util.List;

public class FunctionCallExpression extends CallExpression {
    public FunctionCallExpression(FunctionIdentifier caller, List<Expression> args) {
        super(caller, args);
    }
}