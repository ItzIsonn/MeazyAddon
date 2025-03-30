package me.itzisonn_.meazy_addon.runtime.environment.default_classes.primitive;

import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy_addon.parser.Modifiers;
import me.itzisonn_.meazy.parser.ast.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.ClassDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy_addon.runtime.environment.ClassEnvironmentImpl;
import me.itzisonn_.meazy.runtime.value.NullValue;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.runtime.value.StringValue;
import me.itzisonn_.meazy.runtime.value.classes.constructors.DefaultConstructorValue;
import me.itzisonn_.meazy.runtime.value.function.DefaultFunctionValue;

import java.util.List;
import java.util.Set;

public class CharClassEnvironment extends ClassEnvironmentImpl {
    public CharClassEnvironment(ClassDeclarationEnvironment parent) {
        super(parent, true, "Char");


        declareConstructor(new DefaultConstructorValue(List.of(), this, Set.of(Modifiers.PRIVATE())) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {}
        });


        declareFunction(new DefaultFunctionValue("valueOf", List.of(
                new CallArgExpression("object", new DataType("Any", false), true)),
                new DataType("Char", true), this, Set.of(Modifiers.SHARED())) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                String value = functionArgs.getFirst().getFinalValue().toString();
                if (value.length() == 1) return new StringValue(value);
                return new NullValue();
            }
        });
    }
}
