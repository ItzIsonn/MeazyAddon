package me.itzisonn_.meazy_addon.parser.modifier;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.ast.ModifierStatement;
import me.itzisonn_.meazy.parser.ast.expression.Identifier;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy_addon.parser.ast.statement.VariableDeclarationStatement;

public class SetModifier extends Modifier {
    public SetModifier() {
        super("set");
    }

    @Override
    public boolean canUse(ModifierStatement modifierStatement, RuntimeContext context, Environment environment) {
        return modifierStatement instanceof VariableDeclarationStatement variableDeclarationStatement && !variableDeclarationStatement.isConstant()
                && environment instanceof ClassEnvironment;
    }

    @Override
    public boolean canAccess(Environment requestEnvironment, Environment environment, Identifier identifier, boolean hasModifier) {
        return true;
    }
}
