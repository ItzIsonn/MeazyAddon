package me.itzisonn_.meazy_addon.parser.modifier;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.ast.ModifierStatement;
import me.itzisonn_.meazy.parser.ast.expression.Identifier;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy_addon.parser.ast.statement.ClassDeclarationStatement;
import me.itzisonn_.meazy_addon.parser.ast.statement.ConstructorDeclarationStatement;
import me.itzisonn_.meazy_addon.parser.ast.statement.FunctionDeclarationStatement;

public class NativeModifier extends Modifier {
    public NativeModifier() {
        super("native");
    }

    @Override
    public boolean canUse(ModifierStatement modifierStatement, RuntimeContext context, Environment environment) {
        if (environment.getFileEnvironment().getNativeClasses().isEmpty()) return false;

        return modifierStatement instanceof FunctionDeclarationStatement || modifierStatement instanceof ConstructorDeclarationStatement ||
                modifierStatement instanceof ClassDeclarationStatement;
    }

    @Override
    public boolean canAccess(Environment requestEnvironment, Environment environment, Identifier identifier, boolean hasModifier) {
        return true;
    }
}
