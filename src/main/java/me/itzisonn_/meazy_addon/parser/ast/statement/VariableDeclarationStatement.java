package me.itzisonn_.meazy_addon.parser.ast.statement;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.ModifierStatement;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.data_type.DataType;
import me.itzisonn_.meazy.parser.ast.expression.Expression;

import java.util.Set;

@Getter
public class VariableDeclarationStatement extends ModifierStatement implements Statement {
    private final boolean isConstant;
    private final String id;
    private final DataType dataType;
    private final Expression value;

    public VariableDeclarationStatement(Set<Modifier> modifiers, boolean isConstant, String id, DataType dataType, Expression value) {
        super(modifiers);
        this.isConstant = isConstant;
        this.id = id;
        this.dataType = dataType;
        this.value = value;
    }

    @Override
    public String toCodeString(int offset) throws IllegalArgumentException {
        String keywordString = isConstant ? "val" : "var";

        String valueString = value == null ? "" : " = " + value.toCodeString(0);
        String declarationString = id + " : " + dataType + valueString;

        return super.toCodeString(0) + keywordString + " " + declarationString;
    }
}
