package me.itzisonn_.meazy_addon.parser.modifier;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.ast.ModifierStatement;
import me.itzisonn_.meazy.parser.ast.expression.Identifier;
import me.itzisonn_.meazy.runtime.environment.*;
import me.itzisonn_.meazy.runtime.value.VariableValue;
import me.itzisonn_.meazy_addon.parser.ast.expression.identifier.FunctionIdentifier;
import me.itzisonn_.meazy_addon.parser.ast.expression.identifier.VariableIdentifier;
import me.itzisonn_.meazy_addon.parser.ast.statement.FunctionDeclarationStatement;
import me.itzisonn_.meazy_addon.parser.ast.statement.VariableDeclarationStatement;

public class SharedModifier extends Modifier {
    public SharedModifier() {
        super("shared");
    }

    @Override
    public boolean canUse(ModifierStatement modifierStatement, RuntimeContext context, Environment environment) {
        if (modifierStatement.getModifiers().contains(AddonModifiers.ABSTRACT())) return false;

        if (modifierStatement instanceof VariableDeclarationStatement || modifierStatement instanceof FunctionDeclarationStatement) {
            return environment instanceof ClassEnvironment;
        }
        return false;
    }

    @Override
    public boolean canAccess(Environment requestEnvironment, Environment environment, Identifier identifier, boolean hasModifier) {
        if (hasModifier) return true;

        if (identifier instanceof VariableIdentifier) {
            if (!(environment instanceof VariableDeclarationEnvironment variableDeclarationEnvironment)) return true;

            VariableValue variableValue = variableDeclarationEnvironment.getVariable(identifier.getId());
            if (variableValue == null) return true;

            return !environment.isShared() || variableValue.isArgument() || environment instanceof FileEnvironment || environment instanceof GlobalEnvironment;
        }

        if (identifier instanceof FunctionIdentifier) {
            return !environment.isShared() || environment instanceof FileEnvironment || environment instanceof GlobalEnvironment;
        }

        return true;
    }
}
