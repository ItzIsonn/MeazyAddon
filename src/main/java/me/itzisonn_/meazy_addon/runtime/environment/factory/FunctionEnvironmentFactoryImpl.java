package me.itzisonn_.meazy_addon.runtime.environment.factory;

import me.itzisonn_.meazy.runtime.environment.FunctionDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import me.itzisonn_.meazy.runtime.environment.factory.FunctionEnvironmentFactory;
import me.itzisonn_.meazy_addon.runtime.environment.FunctionEnvironmentImpl;

public class FunctionEnvironmentFactoryImpl implements FunctionEnvironmentFactory {
    @Override
    public FunctionEnvironment create(FunctionDeclarationEnvironment parent, boolean isShared) {
        return new FunctionEnvironmentImpl(parent, isShared);
    }

    @Override
    public FunctionEnvironment create(FunctionDeclarationEnvironment parent) {
        return new FunctionEnvironmentImpl(parent);
    }
}
