package me.itzisonn_.meazy_addon.runtime.value.native_class.primitive;

import me.itzisonn_.meazy.runtime.environment.*;
import me.itzisonn_.meazy_addon.parser.AddonModifiers;
import me.itzisonn_.meazy_addon.runtime.environment.ClassEnvironmentImpl;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.runtime.value.impl.classes.NativeClassValueImpl;
import me.itzisonn_.meazy_addon.runtime.value.impl.constructor.NativeConstructorValueImpl;

import java.util.List;
import java.util.Set;

public class AnyClassValue extends NativeClassValueImpl {
    public AnyClassValue(ClassDeclarationEnvironment parent) {
        super(new ClassEnvironmentImpl(parent, false, "Any"));
        setupEnvironment(getEnvironment());
    }

    @Override
    public void setupEnvironment(ClassEnvironment classEnvironment) {
        classEnvironment.declareConstructor(new NativeConstructorValueImpl(List.of(), classEnvironment, Set.of(AddonModifiers.PROTECTED())) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, ConstructorEnvironment constructorEnvironment) {}
        });
    }

    @Override
    public boolean isMatches(Object value) {
        return true;
    }
}
