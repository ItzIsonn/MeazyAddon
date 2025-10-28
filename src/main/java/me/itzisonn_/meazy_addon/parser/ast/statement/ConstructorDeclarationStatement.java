package me.itzisonn_.meazy_addon.parser.ast.statement;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.ModifierStatement;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;

import java.util.List;
import java.util.Set;

@Getter
public class ConstructorDeclarationStatement extends ModifierStatement implements Statement {
    private final List<ParameterExpression> parameters;
    private final List<Statement> body;

    public ConstructorDeclarationStatement(Set<Modifier> modifiers, List<ParameterExpression> parameters, List<Statement> body) {
        super(modifiers);
        this.parameters = parameters;
        this.body = body;
    }

    @Override
    public String toCodeString(int offset) throws IllegalArgumentException {
        StringBuilder argsBuilder = new StringBuilder();
        for (int i = 0; i < parameters.size(); i++) {
            argsBuilder.append(parameters.get(i).toCodeString());
            if (i != parameters.size() - 1) argsBuilder.append(", ");
        }

        StringBuilder bodyBuilder = new StringBuilder();
        for (Statement statement : body) {
            bodyBuilder.append(Statement.getOffset(offset)).append(statement.toCodeString(offset + 1)).append("\n");
        }

        return super.toCodeString(0) + "constructor(" + argsBuilder + ") {\n" + bodyBuilder + Statement.getOffset(offset - 1) + "}";
    }
}
