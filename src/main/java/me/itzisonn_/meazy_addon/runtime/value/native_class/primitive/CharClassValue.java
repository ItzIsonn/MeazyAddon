package me.itzisonn_.meazy_addon.runtime.value.native_class.primitive;

import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.runtime.environment.*;
import me.itzisonn_.meazy.runtime.value.classes.NativeClassValue;
import me.itzisonn_.meazy_addon.parser.AddonModifiers;
import me.itzisonn_.meazy.parser.ast.CallArgExpression;
import me.itzisonn_.meazy_addon.runtime.environment.ClassEnvironmentImpl;
import me.itzisonn_.meazy.runtime.value.NullValue;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.classes.constructor.NativeConstructorValue;
import me.itzisonn_.meazy.runtime.value.function.NativeFunctionValue;

import java.util.List;
import java.util.Set;

public class CharClassValue extends NativeClassValue {
    public CharClassValue(ClassDeclarationEnvironment parent) {
        super(new ClassEnvironmentImpl(parent, false, "Char"));
        setupEnvironment(getEnvironment());
    }

    @Override
    public void setupEnvironment(ClassEnvironment classEnvironment) {
        classEnvironment.declareConstructor(new NativeConstructorValue(List.of(), classEnvironment, Set.of(AddonModifiers.PRIVATE())) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, ConstructorEnvironment constructorEnvironment) {}
        });

        classEnvironment.declareFunction(new NativeFunctionValue("valueOf", List.of(
                new CallArgExpression("object", new DataType("Any", false), true)),
                new DataType("Char", true), classEnvironment, Set.of(AddonModifiers.SHARED())) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, FunctionEnvironment functionEnvironment) {
                String value = functionArgs.getFirst().getFinalValue().toString();
                if (value.length() == 1) return new StringClassValue(value);
                return new NullValue();
            }
        });
    }

    @Override
    public boolean isMatches(Object value) {
        if (value == null) return true;
        if (value instanceof Character) return true;
        return value.toString().length() == 1;
    }
}
