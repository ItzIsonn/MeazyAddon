package me.itzisonn_.meazy_addon.runtime.environment;

import me.itzisonn_.meazy.runtime.environment.ConstructorDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.ConstructorEnvironment;

public class ConstructorEnvironmentImpl extends VariableDeclarationEnvironmentImpl implements ConstructorEnvironment {
    public ConstructorEnvironmentImpl(ConstructorDeclarationEnvironment parent, boolean isShared) {
        super(parent, isShared);
    }

    public ConstructorEnvironmentImpl(ConstructorDeclarationEnvironment parent) {
        super(parent, false);
    }
}