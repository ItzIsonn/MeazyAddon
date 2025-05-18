package me.itzisonn_.meazy_addon.runtime.value.native_class.primitive;

import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.ClassDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import me.itzisonn_.meazy.runtime.value.NullValue;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.AddonUtils;
import me.itzisonn_.meazy_addon.parser.AddonModifiers;
import me.itzisonn_.meazy_addon.runtime.environment.ClassEnvironmentImpl;
import me.itzisonn_.meazy_addon.runtime.value.impl.classes.NativeClassValueImpl;
import me.itzisonn_.meazy_addon.runtime.value.impl.function.NativeFunctionValueImpl;
import me.itzisonn_.meazy_addon.runtime.value.number.*;

import java.util.List;
import java.util.Set;

public class NumberClassValue extends NativeClassValueImpl {
    public NumberClassValue(ClassDeclarationEnvironment parent) {
        super(new ClassEnvironmentImpl(parent, false, "Number", Set.of(AddonModifiers.ABSTRACT())));
        setupEnvironment(getEnvironment());
    }

    @Override
    public void setupEnvironment(ClassEnvironment classEnvironment) {
        classEnvironment.declareFunction(new NativeFunctionValueImpl("valueOf", List.of(
                new CallArgExpression("object", new DataType("Any", false), true)),
                new DataType("Number", true), classEnvironment, Set.of(AddonModifiers.SHARED())) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, FunctionEnvironment functionEnvironment) {
                String value = functionArgs.getFirst().getFinalValue().toString();
                try {
                    return AddonUtils.optimalNumberValue(Double.parseDouble(value));
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
        return value instanceof Number || value instanceof NumberValue<?>;
    }
}
