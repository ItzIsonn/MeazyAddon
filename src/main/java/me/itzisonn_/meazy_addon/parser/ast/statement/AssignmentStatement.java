package me.itzisonn_.meazy_addon.parser.ast.statement;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.expression.Expression;

@Getter
public class AssignmentStatement implements Statement {
    private final Expression id;
    private final Expression value;

    public AssignmentStatement(Expression id, Expression value) {
        this.id = id;
        this.value = value;
    }

    @Override
    public String toCodeString(int offset) {
        return id.toCodeString() + " = " + value.toCodeString();
    }
}
