package me.itzisonn_.meazy_addon.parser.ast.statement;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.ModifierStatement;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.Modifier;

import java.util.*;

@Getter
public class ClassDeclarationStatement extends ModifierStatement implements Statement {
    private final String id;
    private final Set<String> baseClasses;
    private final List<Statement> body;
    private final LinkedHashMap<String, List<Expression>> enumIds;

    public ClassDeclarationStatement(Set<Modifier> modifiers, String id, Set<String> baseClasses, List<Statement> body, LinkedHashMap<String, List<Expression>> enumIds) {
        super(modifiers);
        this.id = id;
        this.baseClasses = baseClasses;
        this.body = body;
        this.enumIds = enumIds;
    }

    public ClassDeclarationStatement(Set<Modifier> modifiers, String id, Set<String> baseClasses, List<Statement> body) {
        this(modifiers, id, baseClasses, body, new LinkedHashMap<>());
    }

    @Override
    public String toCodeString(int offset) throws IllegalArgumentException {
        String baseClassesString;
        if (!baseClasses.isEmpty()) {
            baseClassesString = " : " + String.join(", ", baseClasses);
        }
        else baseClassesString = "";

        String bodyString;
        if (!body.isEmpty() || !enumIds.isEmpty()) {
            StringBuilder bodyBuilder = new StringBuilder();
            for (Statement statement : body) {
                bodyBuilder.append(Statement.getOffset(offset)).append(statement.toCodeString(offset + 1)).append("\n");
            }

            bodyString = " {\n" + enumToString(offset) + bodyBuilder + Statement.getOffset(offset - 1) + "}";
        }
        else bodyString = "";

        return super.toCodeString(0) + "class " + id + baseClassesString + bodyString;
    }

    private String enumToString(int offset) {
        StringBuilder enumIdsBuilder = new StringBuilder();
        List<String> keySet = new ArrayList<>(enumIds.sequencedKeySet());

        for (int i = 0; i < enumIds.size(); i++) {
            String enumId = keySet.get(i);
            enumIdsBuilder.append(Statement.getOffset(offset)).append(enumId);

            StringBuilder argsBuilder = new StringBuilder();
            List<Expression> args = enumIds.get(enumId);
            for (int j = 0; j < args.size(); j++) {
                argsBuilder.append(args.get(j).toCodeString());
                if (j != args.size() - 1) argsBuilder.append(", ");
            }

            if (!args.isEmpty()) enumIdsBuilder.append("(").append(argsBuilder).append(")");

            if (i != enumIds.size() - 1) enumIdsBuilder.append(",");
            enumIdsBuilder.append("\n");
        }

        return enumIdsBuilder.toString();
    }
}
