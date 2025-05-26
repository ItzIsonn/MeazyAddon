package me.itzisonn_.meazy_addon.parser.ast.statement;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.ModifierStatement;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.data_type.DataType;
import me.itzisonn_.meazy.parser.ast.Expression;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class VariableDeclarationStatement extends ModifierStatement implements Statement {
    private final boolean isConstant;
    private final List<VariableDeclarationInfo> declarationInfos;

    public VariableDeclarationStatement(Set<Modifier> modifiers, boolean isConstant, List<VariableDeclarationInfo> declarationInfos) {
        super(modifiers);
        this.isConstant = isConstant;
        this.declarationInfos = declarationInfos;
    }

    @Override
    public String toCodeString(int offset) throws IllegalArgumentException {
        String keywordString = isConstant ? "val" : "var";

        String declarationString = declarationInfos.stream().map(variableDeclarationInfo -> {
            String value = variableDeclarationInfo.getValue() == null ? "" : " = " + variableDeclarationInfo.getValue().toCodeString(0);
            return variableDeclarationInfo.getId() + ":" + variableDeclarationInfo.getDataType() + value;
        }).collect(Collectors.joining(", "));

        return super.toCodeString(0) + keywordString + " " + declarationString;
    }

    @Getter
    public static class VariableDeclarationInfo {
        private final String id;
        private final DataType dataType;
        private final Expression value;

        public VariableDeclarationInfo(String id, DataType dataType, Expression value) {
            this.id = id;
            this.dataType = dataType;
            this.value = value;
        }
    }
}
