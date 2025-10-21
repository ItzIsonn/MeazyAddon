package me.itzisonn_.meazy_addon.runtime.environment;

import me.itzisonn_.meazy.runtime.environment.FunctionDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;

public class FunctionEnvironmentImpl extends VariableDeclarationEnvironmentImpl implements FunctionEnvironment {
    public FunctionEnvironmentImpl(FunctionDeclarationEnvironment parent, boolean isShared) {
        super(parent, isShared);
    }

    public FunctionEnvironmentImpl(FunctionDeclarationEnvironment parent) {
        super(parent, false);
    }
}