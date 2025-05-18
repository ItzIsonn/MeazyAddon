package me.itzisonn_.meazy_addon.runtime.value.native_class.primitive;

import me.itzisonn_.meazy.runtime.environment.*;
import me.itzisonn_.meazy_addon.parser.AddonModifiers;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.CallArgExpression;
import me.itzisonn_.meazy_addon.runtime.environment.ClassEnvironmentImpl;
import me.itzisonn_.meazy.runtime.value.NullValue;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.runtime.value.impl.classes.NativeClassValueImpl;
import me.itzisonn_.meazy_addon.runtime.value.impl.constructor.NativeConstructorValueImpl;
import me.itzisonn_.meazy_addon.runtime.value.impl.function.NativeFunctionValueImpl;
import me.itzisonn_.meazy_addon.runtime.value.number.IntValue;
import me.itzisonn_.meazy_addon.runtime.value.number.NumberValue;

import java.util.List;
import java.util.Set;

public class IntClassValue extends NativeClassValueImpl {
    public IntClassValue(ClassDeclarationEnvironment parent) {
        super(Set.of("Number"), new ClassEnvironmentImpl(parent, false, "Int"));
        setupEnvironment(getEnvironment());
    }

    @Override
    public void setupEnvironment(ClassEnvironment classEnvironment) {
        classEnvironment.declareConstructor(new NativeConstructorValueImpl(List.of(), classEnvironment, Set.of(AddonModifiers.PRIVATE())) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, ConstructorEnvironment constructorEnvironment) {}
        });

        classEnvironment.declareFunction(new NativeFunctionValueImpl("valueOf", List.of(
                new CallArgExpression("object", new DataType("Any", false), true)),
                new DataType("Int", true), classEnvironment, Set.of(AddonModifiers.SHARED())) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, FunctionEnvironment functionEnvironment) {
                String value = functionArgs.getFirst().getFinalValue().toString();
                try {
                    return new IntValue(Integer.parseInt(value.replaceAll("\\.0$", "")));
                }
                catch (NumberFormatException ignore) {
                    return new NullValue();
                }
            }
        });
    }

    @Override
    public boolean isMatches(Object value) {
        if (value == null) return true;
        if (value instanceof Integer || value instanceof IntValue) return true;

        double doubleValue;
        if (value instanceof Number number) doubleValue = number.doubleValue();
        else if (value instanceof NumberValue<?> number) doubleValue = number.getValue().doubleValue();
        else return false;

        return doubleValue >= Integer.MIN_VALUE && doubleValue <= Integer.MAX_VALUE && doubleValue % 1 == 0;
    }
}
