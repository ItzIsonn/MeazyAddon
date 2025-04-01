package me.itzisonn_.meazy_addon.runtime.environment.default_classes;

import me.itzisonn_.meazy_addon.parser.AddonModifiers;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.runtime.environment.ClassDeclarationEnvironment;
import me.itzisonn_.meazy_addon.runtime.environment.ClassEnvironmentImpl;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.value.classes.constructors.DefaultConstructorValue;
import me.itzisonn_.meazy_addon.runtime.value.number.DoubleValue;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.runtime.value.StringValue;
import me.itzisonn_.meazy.runtime.value.function.DefaultFunctionValue;
import me.itzisonn_.meazy_addon.runtime.value.number.IntValue;

import java.util.*;

public class InputClassEnvironment extends ClassEnvironmentImpl {
    private static final Scanner SCANNER = new Scanner(System.in);

    public InputClassEnvironment(ClassDeclarationEnvironment parent) {
        super(parent, true, "Input");


        declareConstructor(new DefaultConstructorValue(List.of(), this, Set.of(AddonModifiers.PRIVATE())) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {}
        });


        declareFunction(new DefaultFunctionValue("read", List.of(), new DataType("String", false), this, Set.of(AddonModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                return new StringValue(SCANNER.next());
            }
        });

        declareFunction(new DefaultFunctionValue("readLine", List.of(), new DataType("String", false), this, Set.of(AddonModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                return new StringValue(SCANNER.nextLine());
            }
        });

        declareFunction(new DefaultFunctionValue("readInt", List.of(), new DataType("String", false), this, Set.of(AddonModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                return new IntValue(SCANNER.nextInt());
            }
        });

        declareFunction(new DefaultFunctionValue("readFloat", List.of(), new DataType("String", false), this, Set.of(AddonModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                return new DoubleValue(SCANNER.nextDouble());
            }
        });
    }
}
