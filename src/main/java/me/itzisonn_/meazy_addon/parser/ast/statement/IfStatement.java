package me.itzisonn_.meazy_addon.parser.ast.statement;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.expression.Expression;

import java.util.List;

@Getter
public class IfStatement implements Statement {
    private final Expression condition;
    private final List<Statement> body;
    private final IfStatement elseStatement;

    public IfStatement(Expression condition, List<Statement> body, IfStatement elseStatement) {
        this.condition = condition;
        this.body = body;
        this.elseStatement = elseStatement;
    }

    @Override
    public String toCodeString(int offset) throws IllegalArgumentException {
        String conditionString = condition == null ? "" : "if (" + condition.toCodeString(0) + ") ";

        StringBuilder bodyBuilder = new StringBuilder();
        for (Statement statement : body) {
            bodyBuilder.append(Statement.getOffset(offset)).append(statement.toCodeString(offset + 1)).append("\n");
        }

        String elseString = elseStatement == null ? "" : "\n" + Statement.getOffset(offset - 1) + "else " + elseStatement.toCodeString(offset);

        return conditionString + "{\n" + bodyBuilder + Statement.getOffset(offset - 1) + "}" + elseString;
    }
}
