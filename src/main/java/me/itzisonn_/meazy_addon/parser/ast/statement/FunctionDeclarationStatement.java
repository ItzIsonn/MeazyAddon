package me.itzisonn_.meazy_addon.parser.ast.statement;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.ModifierStatement;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.data_type.DataType;
import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import me.itzisonn_.meazy_addon.parser.modifier.AddonModifiers;

import java.util.List;
import java.util.Set;

@Getter
public class FunctionDeclarationStatement extends ModifierStatement implements Statement {
    private final String id;
    private final String classId;
    private final List<ParameterExpression> parameters;
    private final List<Statement> body;
    private final DataType returnDataType;

    public FunctionDeclarationStatement(Set<Modifier> modifiers, String id, String classId, List<ParameterExpression> parameters, List<Statement> body, DataType returnDataType) {
        super(modifiers);
        this.id = id;
        this.classId = classId;
        this.parameters = parameters;
        this.body = body;
        this.returnDataType = returnDataType;
    }

    public FunctionDeclarationStatement(Set<Modifier> modifiers, String id, List<ParameterExpression> parameters, List<Statement> body, DataType returnDataType) {
        this(modifiers, id, null, parameters, body, returnDataType);
    }

    @Override
    public String toCodeString(int offset) throws IllegalArgumentException {
        String classString = classId == null ? "" :  classId + ".";

        StringBuilder argsBuilder = new StringBuilder();
        for (int i = 0; i < parameters.size(); i++) {
            argsBuilder.append(parameters.get(i).toCodeString(0));
            if (i != parameters.size() - 1) argsBuilder.append(", ");
        }

        String returnDataTypeString = returnDataType == null ? "" : ":" + returnDataType;

        String bodyString;
        if (!modifiers.contains(AddonModifiers.ABSTRACT()) && !modifiers.contains(AddonModifiers.NATIVE())) {
            StringBuilder bodyBuilder = new StringBuilder();
            for (Statement statement : body) {
                bodyBuilder.append(Statement.getOffset(offset)).append(statement.toCodeString(offset + 1)).append("\n");
            }
            bodyString = " {\n" + bodyBuilder + Statement.getOffset(offset - 1) + "}";
        }
        else bodyString = "";

        return super.toCodeString(0) + "function " + classString + id + "(" + argsBuilder + ")" + returnDataTypeString + bodyString;
    }
}
