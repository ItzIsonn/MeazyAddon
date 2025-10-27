package me.itzisonn_.meazy_addon.parser.modifier;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.ast.ModifierStatement;
import me.itzisonn_.meazy.parser.ast.expression.identifier.FunctionIdentifier;
import me.itzisonn_.meazy.parser.ast.expression.identifier.Identifier;
import me.itzisonn_.meazy.parser.ast.expression.identifier.VariableIdentifier;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidIdentifierException;
import me.itzisonn_.meazy.runtime.value.classes.ClassValue;
import me.itzisonn_.meazy_addon.parser.ast.statement.ConstructorDeclarationStatement;
import me.itzisonn_.meazy_addon.parser.ast.statement.FunctionDeclarationStatement;
import me.itzisonn_.meazy_addon.parser.ast.statement.VariableDeclarationStatement;

public class ProtectedModifier extends Modifier {
    public ProtectedModifier() {
        super("protected");
    }

    @Override
    public boolean canUse(ModifierStatement modifierStatement, RuntimeContext context, Environment environment) {
        if (modifierStatement.getModifiers().contains(AddonModifiers.PRIVATE()) || modifierStatement.getModifiers().contains(AddonModifiers.OPEN())) return false;

        if (modifierStatement instanceof VariableDeclarationStatement || modifierStatement instanceof FunctionDeclarationStatement ||
                modifierStatement instanceof ConstructorDeclarationStatement) {
            return environment instanceof ClassEnvironment;
        }
        return false;
    }

    @Override
    public boolean canAccess(Environment requestEnvironment, Environment environment, Identifier identifier, boolean hasModifier) {
        if (!hasModifier) return true;

        if (identifier instanceof VariableIdentifier) return requestEnvironment == environment || requestEnvironment.hasParent(environment) ||
                requestEnvironment.hasParent(parentEnv -> {
                    if (parentEnv instanceof ClassEnvironment classEnvironment) {
                        ClassEnvironment declarationEnvironment = (ClassEnvironment) environment.getParent(env -> env instanceof ClassEnvironment);
                        if (declarationEnvironment == null) return false;
                        if (classEnvironment.getId().equals(declarationEnvironment.getId())) return true;

                        ClassValue parentClassValue = environment.getFileEnvironment().getClass(classEnvironment.getId());
                        if (parentClassValue == null) {
                            throw new InvalidIdentifierException("Class with id " + classEnvironment.getId() + " doesn't exist");
                        }
                        return parentClassValue.getBaseClasses().stream().anyMatch(cls -> cls.equals(declarationEnvironment.getId()));
                    }
                    return false;
                });

        if (identifier instanceof FunctionIdentifier) return requestEnvironment == environment || requestEnvironment.hasParent(environment) ||
                requestEnvironment.hasParent(parentEnv -> {
                    if (parentEnv instanceof ClassEnvironment classEnvironment) {
                        ClassEnvironment declarationEnvironment;
                        if (environment instanceof ClassEnvironment env) declarationEnvironment = env;
                        else declarationEnvironment = (ClassEnvironment) environment.getParent(env -> env instanceof ClassEnvironment);

                        if (declarationEnvironment == null) return false;
                        if (classEnvironment.getId().equals(declarationEnvironment.getId())) return true;

                        ClassValue parentClassValue = environment.getFileEnvironment().getClass(classEnvironment.getId());
                        if (parentClassValue == null) {
                            throw new InvalidIdentifierException("Class with id " + classEnvironment.getId() + " doesn't exist");
                        }
                        return parentClassValue.getBaseClasses().stream().anyMatch(cls -> cls.equals(declarationEnvironment.getId()));
                    }
                    return false;
                });

        return true;
    }
}
