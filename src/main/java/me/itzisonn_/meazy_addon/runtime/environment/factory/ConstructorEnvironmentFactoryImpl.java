package me.itzisonn_.meazy_addon.runtime.environment.factory;

import me.itzisonn_.meazy.runtime.environment.ConstructorDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.ConstructorEnvironment;
import me.itzisonn_.meazy.runtime.environment.factory.ConstructorEnvironmentFactory;
import me.itzisonn_.meazy_addon.runtime.environment.ConstructorEnvironmentImpl;

public class ConstructorEnvironmentFactoryImpl implements ConstructorEnvironmentFactory {
    @Override
    public ConstructorEnvironment create(ConstructorDeclarationEnvironment parent, boolean isShared) {
        return new ConstructorEnvironmentImpl(parent, isShared);
    }

    @Override
    public ConstructorEnvironment create(ConstructorDeclarationEnvironment parent) {
        return new ConstructorEnvironmentImpl(parent);
    }
}
