package me.itzisonn_.meazy_addon.parser.ast.expression.call_expression;

import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy_addon.parser.ast.expression.identifier.ClassIdentifier;

import java.util.List;

public class ClassCallExpression extends CallExpression {
    public ClassCallExpression(ClassIdentifier caller, List<Expression> args) {
        super(caller, args);
    }
}