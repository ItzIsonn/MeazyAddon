package me.itzisonn_.meazy_addon.runtime.environment.factory;

import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.runtime.environment.ClassDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.factory.ClassEnvironmentFactory;
import me.itzisonn_.meazy_addon.runtime.environment.ClassEnvironmentImpl;

import java.util.Set;

public class ClassEnvironmentFactoryImpl implements ClassEnvironmentFactory {
    @Override
    public ClassEnvironment create(ClassDeclarationEnvironment parent, boolean isShared, String id, Set<Modifier> modifiers) {
        return new ClassEnvironmentImpl(parent, isShared, id, modifiers);
    }

    @Override
    public ClassEnvironment create(ClassDeclarationEnvironment parent, boolean isShared, String id) {
        return new ClassEnvironmentImpl(parent, isShared, id);
    }

    @Override
    public ClassEnvironment create(ClassDeclarationEnvironment parent, String id, Set<Modifier> modifiers) {
        return new ClassEnvironmentImpl(parent, id, modifiers);
    }

    @Override
    public ClassEnvironment create(ClassDeclarationEnvironment parent, String id) {
        return new ClassEnvironmentImpl(parent, id);
    }
}
