package me.itzisonn_.meazy_addon.parser.modifier;

import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.ast.ModifierStatement;
import me.itzisonn_.meazy.parser.ast.expression.identifier.Identifier;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy_addon.parser.ast.statement.ClassDeclarationStatement;

public class DataModifier extends Modifier {
    public DataModifier() {
        super("data");
    }

    @Override
    public boolean canUse(ModifierStatement modifierStatement, Environment environment) {
        return modifierStatement instanceof ClassDeclarationStatement;
    }

    @Override
    public boolean canAccess(Environment requestEnvironment, Environment environment, Identifier identifier, boolean hasModifier) {
        return true;
    }
}
