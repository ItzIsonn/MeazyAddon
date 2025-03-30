package me.itzisonn_.meazy_addon.runtime.environment.default_classes.primitive;

import me.itzisonn_.meazy_addon.parser.Modifiers;
import me.itzisonn_.meazy.runtime.environment.ClassDeclarationEnvironment;
import me.itzisonn_.meazy_addon.runtime.environment.ClassEnvironmentImpl;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.classes.constructors.DefaultConstructorValue;

import java.util.List;
import java.util.Set;

public class AnyClassEnvironment extends ClassEnvironmentImpl {
    public AnyClassEnvironment(ClassDeclarationEnvironment parent) {
        super(parent, true, "Any");


        declareConstructor(new DefaultConstructorValue(List.of(), this, Set.of(Modifiers.PRIVATE())) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {}
        });
    }
}
