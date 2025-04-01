package me.itzisonn_.meazy_addon.parser.ast.statement;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.Expression;

import java.util.List;

@Getter
public class WhileStatement implements Statement {
    private final Expression condition;
    private final List<Statement> body;

    public WhileStatement(Expression condition, List<Statement> body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public String toCodeString(int offset) throws IllegalArgumentException {
        StringBuilder bodyBuilder = new StringBuilder();
        for (Statement statement : body) {
            bodyBuilder.append(Statement.getOffset(offset)).append(statement.toCodeString(offset + 1)).append("\n");
        }

        return "while (" + condition.toCodeString(0) + ") {\n" + bodyBuilder + Statement.getOffset(offset - 1) + "}";
    }
}
