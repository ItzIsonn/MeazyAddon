package me.itzisonn_.meazy_addon.parser.ast.expression.call_expression;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.expression.Identifier;

import java.util.List;

@Getter
public abstract class CallExpression implements Expression {
    protected final Identifier caller;
    protected final List<Expression> args;

    public CallExpression(Identifier caller, List<Expression> args) {
        this.caller = caller;
        this.args = args;
    }
}