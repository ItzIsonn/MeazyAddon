package me.itzisonn_.meazy_addon.parser.modifier;

import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.ast.ModifierStatement;
import me.itzisonn_.meazy.parser.ast.expression.identifier.Identifier;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy_addon.parser.ast.statement.ClassDeclarationStatement;
import me.itzisonn_.meazy_addon.parser.ast.statement.FunctionDeclarationStatement;

public class AbstractModifier extends Modifier {
    public AbstractModifier() {
        super("abstract");
    }

    @Override
    public boolean canUse(ModifierStatement modifierStatement, Environment environment) {
        if (modifierStatement.getModifiers().contains(AddonModifiers.PRIVATE()) || modifierStatement.getModifiers().contains(AddonModifiers.SHARED()) ||
                modifierStatement.getModifiers().contains(AddonModifiers.FINAL()) || modifierStatement.getModifiers().contains(AddonModifiers.ENUM())) return false;

        if (modifierStatement instanceof ClassDeclarationStatement) return true;
        if (modifierStatement instanceof FunctionDeclarationStatement && environment instanceof ClassEnvironment classEnvironment) {
            return classEnvironment.getModifiers().contains(AddonModifiers.ABSTRACT());
        }
        return false;
    }

    @Override
    public boolean canAccess(Environment requestEnvironment, Environment environment, Identifier identifier, boolean hasModifier) {
        return true;
    }
}
