package me.itzisonn_.meazy_addon.parser.modifier;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.ast.ModifierStatement;
import me.itzisonn_.meazy.parser.ast.expression.Identifier;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.FileEnvironment;
import me.itzisonn_.meazy_addon.parser.ast.statement.ClassDeclarationStatement;
import me.itzisonn_.meazy_addon.parser.ast.statement.ConstructorDeclarationStatement;
import me.itzisonn_.meazy_addon.parser.ast.statement.FunctionDeclarationStatement;
import me.itzisonn_.meazy_addon.parser.ast.statement.VariableDeclarationStatement;

public class OpenModifier extends Modifier {
    public OpenModifier() {
        super("open");
    }

    @Override
    public boolean canUse(ModifierStatement modifierStatement, RuntimeContext context, Environment environment) {
        if (modifierStatement.getModifiers().contains(AddonModifiers.PRIVATE()) || modifierStatement.getModifiers().contains(AddonModifiers.PROTECTED())) return false;

        if (environment instanceof FileEnvironment) {
            return modifierStatement instanceof VariableDeclarationStatement || modifierStatement instanceof FunctionDeclarationStatement ||
                    modifierStatement instanceof ClassDeclarationStatement;
        }

        if (environment instanceof ClassEnvironment classEnvironment && classEnvironment.getModifiers().contains(AddonModifiers.OPEN())) {
            return modifierStatement instanceof VariableDeclarationStatement || modifierStatement instanceof FunctionDeclarationStatement ||
                    modifierStatement instanceof ConstructorDeclarationStatement;
        }

        return false;
    }

    @Override
    public boolean canAccess(Environment requestEnvironment, Environment environment, Identifier identifier, boolean hasModifier) {
        if (hasModifier) return true;
        return environment.getParentFile() == null || environment.getParentFile().equals(requestEnvironment.getParentFile());
    }
}
