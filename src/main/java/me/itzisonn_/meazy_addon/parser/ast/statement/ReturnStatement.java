package me.itzisonn_.meazy_addon.parser.ast.statement;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.Expression;

@Getter
public class ReturnStatement implements Statement {
    private final Expression value;

    public ReturnStatement(Expression value) {
        this.value = value;
    }

    @Override
    public String toCodeString(int offset) throws IllegalArgumentException {
        if (value == null) return "return";
        return "return " + value.toCodeString(0);
    }
}
