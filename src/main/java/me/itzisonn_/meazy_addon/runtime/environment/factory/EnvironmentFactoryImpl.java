package me.itzisonn_.meazy_addon.runtime.environment.factory;

import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.factory.EnvironmentFactory;
import me.itzisonn_.meazy_addon.runtime.environment.EnvironmentImpl;

public class EnvironmentFactoryImpl implements EnvironmentFactory {
    @Override
    public Environment create(Environment parent, boolean isShared) {
        return new EnvironmentImpl(parent, isShared);
    }

    @Override
    public Environment create(Environment parent) {
        return new EnvironmentImpl(parent);
    }
}
