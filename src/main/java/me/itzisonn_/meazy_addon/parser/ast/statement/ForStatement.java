package me.itzisonn_.meazy_addon.parser.ast.statement;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.expression.Expression;

import java.util.List;

@Getter
public class ForStatement implements Statement {
    private final VariableDeclarationStatement variableDeclarationStatement;
    private final Expression collection;
    private final List<Statement> body;

    public ForStatement(VariableDeclarationStatement variableDeclarationStatement, Expression collection, List<Statement> body) {
        this.variableDeclarationStatement = variableDeclarationStatement;
        this.collection = collection;
        this.body = body;
    }

    @Override
    public String toCodeString(int offset) throws IllegalArgumentException {
        String variableDeclarationString = variableDeclarationStatement == null ? "" : variableDeclarationStatement.toCodeString(0);

        StringBuilder bodyBuilder = new StringBuilder();
        for (Statement statement : body) {
            bodyBuilder.append(Statement.getOffset(offset)).append(statement.toCodeString(offset + 1)).append("\n");
        }

        return "for (" + variableDeclarationString + " in " + collection.toCodeString(0) + ") {\n" + bodyBuilder + Statement.getOffset(offset - 1) + "}";
    }
}
