package me.itzisonn_.meazy_addon.parser.ast.statement;

import lombok.Getter;
import me.itzisonn_.meazy.Utils;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy_addon.parser.ast.expression.AssignmentExpression;
import me.itzisonn_.meazy.parser.ast.Expression;

import java.util.List;

@Getter
public class ForStatement implements Statement {
    private final VariableDeclarationStatement variableDeclarationStatement;
    private final Expression condition;
    private final AssignmentExpression assignmentExpression;
    private final List<Statement> body;

    public ForStatement(VariableDeclarationStatement variableDeclarationStatement, Expression condition, AssignmentExpression assignmentExpression, List<Statement> body) {
        this.variableDeclarationStatement = variableDeclarationStatement;
        this.condition = condition;
        this.assignmentExpression = assignmentExpression;
        this.body = body;
    }

    @Override
    public String toCodeString(int offset) throws IllegalArgumentException {
        String variableDeclarationString = variableDeclarationStatement == null ? "" : variableDeclarationStatement.toCodeString(0);
        String conditionString = condition == null ? "" : " " + condition.toCodeString(0);
        String assignmentExpressionString = assignmentExpression == null ? "" : " " + assignmentExpression.toCodeString(0);

        StringBuilder bodyBuilder = new StringBuilder();
        for (Statement statement : body) {
            bodyBuilder.append(Utils.getOffset(offset)).append(statement.toCodeString(offset + 1)).append("\n");
        }

        return "for (" + variableDeclarationString + ";" + conditionString + ";" + assignmentExpressionString + ") {\n" + bodyBuilder + Utils.getOffset(offset - 1) + "}";
    }
}
