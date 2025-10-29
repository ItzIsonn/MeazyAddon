package me.itzisonn_.meazy_addon.parser.modifier;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.ast.ModifierStatement;
import me.itzisonn_.meazy.parser.ast.expression.Identifier;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy_addon.parser.ast.expression.identifier.ConstructorClassIdentifier;
import me.itzisonn_.meazy_addon.parser.ast.statement.ConstructorDeclarationStatement;
import me.itzisonn_.meazy_addon.parser.ast.statement.FunctionDeclarationStatement;
import me.itzisonn_.meazy_addon.parser.ast.statement.VariableDeclarationStatement;

public class PrivateModifier extends Modifier {
    public PrivateModifier() {
        super("private");
    }

    @Override
    public boolean canUse(ModifierStatement modifierStatement, RuntimeContext context, Environment environment) {
        if (modifierStatement.getModifiers().contains(AddonModifiers.ABSTRACT()) || modifierStatement.getModifiers().contains(AddonModifiers.PROTECTED()) ||
                modifierStatement.getModifiers().contains(AddonModifiers.OPEN())) return false;

        if (modifierStatement instanceof VariableDeclarationStatement || modifierStatement instanceof FunctionDeclarationStatement ||
                modifierStatement instanceof ConstructorDeclarationStatement) {
            return environment instanceof ClassEnvironment;
        }
        return false;
    }

    @Override
    public boolean canAccess(Environment requestEnvironment, Environment environment, Identifier identifier, boolean hasModifier) {
        if (!hasModifier) return true;

        if (identifier instanceof ConstructorClassIdentifier) {
            return requestEnvironment.hasParent(env -> {
                if (env instanceof ClassEnvironment classEnv) return classEnv.getId().equals(identifier.getId());
                return false;
            });
        }

        return requestEnvironment == environment || requestEnvironment.hasParent(environment);
    }
}
