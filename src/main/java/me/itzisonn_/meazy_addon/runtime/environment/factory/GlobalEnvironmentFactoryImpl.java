package me.itzisonn_.meazy_addon.runtime.environment.factory;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.runtime.environment.GlobalEnvironment;
import me.itzisonn_.meazy.runtime.environment.factory.GlobalEnvironmentFactory;
import me.itzisonn_.meazy_addon.runtime.environment.GlobalEnvironmentImpl;

public class GlobalEnvironmentFactoryImpl implements GlobalEnvironmentFactory {
    @Override
    public GlobalEnvironment create(RuntimeContext context) {
        return new GlobalEnvironmentImpl(context);
    }
}
