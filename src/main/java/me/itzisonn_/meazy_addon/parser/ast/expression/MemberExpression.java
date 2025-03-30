package me.itzisonn_.meazy_addon.parser.ast.expression;

import lombok.Getter;
import lombok.Setter;
import me.itzisonn_.meazy.parser.ast.Expression;

@Getter
@Setter
public class MemberExpression implements Expression {
    private Expression object;
    private final Expression member;
    private final boolean isNullSafe;

    public MemberExpression(Expression object, Expression member, boolean isNullSafe) {
        this.object = object;
        this.member = member;
        this.isNullSafe = isNullSafe;
    }

    @Override
    public String toCodeString() {
        String accessString = isNullSafe ? "?." : ".";
        return object.toCodeString() + accessString + member.toCodeString();
    }
}