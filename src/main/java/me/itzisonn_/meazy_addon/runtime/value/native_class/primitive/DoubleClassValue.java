package me.itzisonn_.meazy_addon.runtime.value.native_class.primitive;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.runtime.environment.*;
import me.itzisonn_.meazy_addon.parser.modifier.AddonModifiers;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy_addon.parser.data_type.DataTypeImpl;
import me.itzisonn_.meazy_addon.runtime.environment.ClassEnvironmentImpl;
import me.itzisonn_.meazy_addon.runtime.value.NullValue;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.runtime.value.impl.classes.NativeClassValueImpl;
import me.itzisonn_.meazy_addon.runtime.value.impl.constructor.NativeConstructorValueImpl;
import me.itzisonn_.meazy_addon.runtime.value.impl.function.NativeFunctionValueImpl;
import me.itzisonn_.meazy_addon.runtime.value.number.DoubleValue;
import me.itzisonn_.meazy_addon.runtime.value.number.NumberValue;

import java.util.List;
import java.util.Set;

public class DoubleClassValue extends NativeClassValueImpl {
    public DoubleClassValue(ClassDeclarationEnvironment parent) {
        super(Set.of("Number"), new ClassEnvironmentImpl(parent, false, "Double"));
        setupEnvironment(getEnvironment());
    }

    @Override
    public void setupEnvironment(ClassEnvironment classEnvironment) {
        classEnvironment.declareConstructor(new NativeConstructorValueImpl(List.of(), classEnvironment, Set.of(AddonModifiers.PRIVATE())) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, RuntimeContext context, ConstructorEnvironment constructorEnvironment) {}
        });

        classEnvironment.declareFunction(new NativeFunctionValueImpl("valueOf", List.of(
                new CallArgExpression("object", new DataTypeImpl("Any", false), true)),
                new DataTypeImpl("Double", true), classEnvironment, Set.of(AddonModifiers.SHARED())) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, RuntimeContext context, FunctionEnvironment functionEnvironment) {
                String value = functionArgs.getFirst().getFinalValue().toString();
                try {
                    return new DoubleValue(Double.parseDouble(value));
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
        if (value instanceof Double || value instanceof DoubleValue) return true;

        double doubleValue;
        if (value instanceof Number number) doubleValue = number.doubleValue();
        else if (value instanceof NumberValue<?> number) doubleValue = number.getValue().doubleValue();
        else return false;

        return doubleValue >= -Double.MAX_VALUE && doubleValue <= Double.MAX_VALUE;
    }
}
