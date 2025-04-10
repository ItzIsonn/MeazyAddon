package me.itzisonn_.meazy_addon.runtime.value.native_class.primitive;

import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.value.classes.NativeClassValue;
import me.itzisonn_.meazy_addon.parser.AddonModifiers;
import me.itzisonn_.meazy.runtime.environment.ClassDeclarationEnvironment;
import me.itzisonn_.meazy_addon.runtime.environment.ClassEnvironmentImpl;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.classes.constructors.NativeConstructorValue;

import java.util.List;
import java.util.Set;

public class AnyClassValue extends NativeClassValue {
    public AnyClassValue(ClassDeclarationEnvironment parent) {
        super(getClassEnvironment(parent));
    }

    private static ClassEnvironment getClassEnvironment(ClassDeclarationEnvironment parent) {
        ClassEnvironment classEnvironment = new ClassEnvironmentImpl(parent, false, "Any");

        classEnvironment.declareConstructor(new NativeConstructorValue(List.of(), classEnvironment, Set.of(AddonModifiers.PRIVATE())) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {}
        });

        return classEnvironment;
    }

    @Override
    public boolean isMatches(Object value) {
        return true;
    }
}
