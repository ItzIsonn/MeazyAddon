package me.itzisonn_.meazy_addon.runtime.environment.default_classes.primitive;

import me.itzisonn_.meazy_addon.parser.AddonModifiers;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.ClassDeclarationEnvironment;
import me.itzisonn_.meazy_addon.runtime.environment.ClassEnvironmentImpl;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy_addon.runtime.value.BooleanValue;
import me.itzisonn_.meazy.runtime.value.NullValue;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.classes.constructors.DefaultConstructorValue;
import me.itzisonn_.meazy.runtime.value.function.DefaultFunctionValue;

import java.util.List;
import java.util.Set;

public class BooleanClassEnvironment extends ClassEnvironmentImpl {
    public BooleanClassEnvironment(ClassDeclarationEnvironment parent) {
        super(parent, true, "Boolean");


        declareConstructor(new DefaultConstructorValue(List.of(), this, Set.of(AddonModifiers.PRIVATE())) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {}
        });


        declareFunction(new DefaultFunctionValue("valueOf", List.of(
                new CallArgExpression("object", new DataType("Any", false), true)),
                new DataType("Boolean", true), this, Set.of(AddonModifiers.SHARED())) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                String value = functionArgs.getFirst().getFinalValue().toString();
                return switch (value) {
                    case "true", "false" -> new BooleanValue(Boolean.parseBoolean(value));
                    case "0" -> new BooleanValue(false);
                    case "1" -> new BooleanValue(true);
                    default -> new NullValue();
                };
            }
        });
    }
}
