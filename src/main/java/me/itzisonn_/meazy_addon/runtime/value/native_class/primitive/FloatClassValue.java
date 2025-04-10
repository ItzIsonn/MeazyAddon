package me.itzisonn_.meazy_addon.runtime.value.native_class.primitive;

import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.value.classes.NativeClassValue;
import me.itzisonn_.meazy_addon.parser.AddonModifiers;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.ClassDeclarationEnvironment;
import me.itzisonn_.meazy_addon.runtime.environment.ClassEnvironmentImpl;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.value.NullValue;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.classes.constructors.NativeConstructorValue;
import me.itzisonn_.meazy.runtime.value.function.NativeFunctionValue;
import me.itzisonn_.meazy_addon.runtime.value.number.FloatValue;
import me.itzisonn_.meazy_addon.runtime.value.number.IntValue;

import java.util.List;
import java.util.Set;

public class FloatClassValue extends NativeClassValue {
    public FloatClassValue(ClassDeclarationEnvironment parent) {
        super(getClassEnvironment(parent));
    }

    private static ClassEnvironment getClassEnvironment(ClassDeclarationEnvironment parent) {
        ClassEnvironment classEnvironment = new ClassEnvironmentImpl(parent, false, "Float");

        classEnvironment.declareConstructor(new NativeConstructorValue(List.of(), classEnvironment, Set.of(AddonModifiers.PRIVATE())) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {}
        });

        classEnvironment.declareFunction(new NativeFunctionValue("valueOf", List.of(
                new CallArgExpression("object", new DataType("Any", false), true)),
                new DataType("Float", true), classEnvironment, Set.of(AddonModifiers.SHARED())) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                String value = functionArgs.getFirst().getFinalValue().toString();
                try {
                    return new FloatValue(Float.parseFloat(value));
                }
                catch (NumberFormatException ignore) {
                    return new NullValue();
                }
            }
        });

        return classEnvironment;
    }

    @Override
    public boolean isMatches(Object value) {
        if (value == null) return true;
        if (value instanceof Integer || value instanceof IntValue || value instanceof Float || value instanceof FloatValue) return true;
        try {
            Float.parseFloat(value.toString());
            return true;
        }
        catch (NumberFormatException ignore) {
            return false;
        }
    }
}
