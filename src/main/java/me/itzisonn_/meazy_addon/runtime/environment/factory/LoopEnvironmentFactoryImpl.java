package me.itzisonn_.meazy_addon.runtime.environment.factory;

import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.LoopEnvironment;
import me.itzisonn_.meazy.runtime.environment.factory.LoopEnvironmentFactory;
import me.itzisonn_.meazy_addon.runtime.environment.LoopEnvironmentImpl;

public class LoopEnvironmentFactoryImpl implements LoopEnvironmentFactory {
    @Override
    public LoopEnvironment create(Environment parent, boolean isShared) {
        return new LoopEnvironmentImpl(parent, isShared);
    }

    @Override
    public LoopEnvironment create(Environment parent) {
        return new LoopEnvironmentImpl(parent);
    }
}
