package me.itzisonn_.meazy_addon.parser.modifier;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.ast.ModifierStatement;
import me.itzisonn_.meazy.parser.ast.expression.identifier.Identifier;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy_addon.parser.ast.statement.ClassDeclarationStatement;
import me.itzisonn_.meazy_addon.parser.ast.statement.FunctionDeclarationStatement;

public class FinalModifier extends Modifier {
    public FinalModifier() {
        super("final");
    }

    @Override
    public boolean canUse(ModifierStatement modifierStatement, RuntimeContext context, Environment environment) {
        if (modifierStatement.getModifiers().contains(AddonModifiers.ABSTRACT())) return false;

        if (modifierStatement instanceof ClassDeclarationStatement) return true;
        return modifierStatement instanceof FunctionDeclarationStatement && environment instanceof ClassEnvironment;
    }

    @Override
    public boolean canAccess(RuntimeContext context, Environment requestEnvironment, Environment environment, Identifier identifier, boolean hasModifier) {
        return true;
    }
}
