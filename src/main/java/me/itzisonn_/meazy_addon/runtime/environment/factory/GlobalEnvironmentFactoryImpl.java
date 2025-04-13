package me.itzisonn_.meazy_addon.runtime.environment.factory;

import me.itzisonn_.meazy.runtime.environment.GlobalEnvironment;
import me.itzisonn_.meazy.runtime.environment.factory.GlobalEnvironmentFactory;
import me.itzisonn_.meazy_addon.runtime.environment.GlobalEnvironmentImpl;

import java.io.File;

public class GlobalEnvironmentFactoryImpl implements GlobalEnvironmentFactory {
    @Override
    public GlobalEnvironment create(File parentFile) {
        return new GlobalEnvironmentImpl(parentFile);
    }
}
