package me.itzisonn_.meazy_addon.runtime.environment.factory;

import me.itzisonn_.meazy.runtime.environment.FileEnvironment;
import me.itzisonn_.meazy.runtime.environment.GlobalEnvironment;
import me.itzisonn_.meazy.runtime.environment.factory.FileEnvironmentFactory;
import me.itzisonn_.meazy_addon.runtime.environment.FileEnvironmentImpl;

import java.io.File;

public class FileEnvironmentFactoryImpl implements FileEnvironmentFactory {
    @Override
    public FileEnvironment create(GlobalEnvironment parent, File parentFile) {
        return new FileEnvironmentImpl(parent, parentFile);
    }
}
