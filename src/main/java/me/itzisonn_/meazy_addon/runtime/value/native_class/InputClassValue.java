package me.itzisonn_.meazy_addon.runtime.value.native_class;

import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.value.classes.NativeClassValue;
import me.itzisonn_.meazy_addon.parser.AddonModifiers;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.runtime.environment.ClassDeclarationEnvironment;
import me.itzisonn_.meazy_addon.runtime.environment.ClassEnvironmentImpl;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.value.classes.constructor.NativeConstructorValue;
import me.itzisonn_.meazy_addon.runtime.value.native_class.primitive.StringClassValue;
import me.itzisonn_.meazy_addon.runtime.value.number.DoubleValue;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.function.NativeFunctionValue;
import me.itzisonn_.meazy_addon.runtime.value.number.IntValue;

import java.util.*;

public class InputClassValue extends NativeClassValue {
    private static final Scanner SCANNER = new Scanner(System.in);

    public InputClassValue(ClassDeclarationEnvironment parent) {
        super(new ClassEnvironmentImpl(parent, false, "Input"));
        setupEnvironment(getEnvironment());
    }

    @Override
    public void setupEnvironment(ClassEnvironment classEnvironment) {
        classEnvironment.declareConstructor(new NativeConstructorValue(List.of(), classEnvironment, Set.of(AddonModifiers.PRIVATE())) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {
            }
        });


        classEnvironment.declareFunction(new NativeFunctionValue("read", List.of(), new DataType("String", false), classEnvironment, Set.of(AddonModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                return new StringClassValue(SCANNER.next());
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("readLine", List.of(), new DataType("String", false), classEnvironment, Set.of(AddonModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                return new StringClassValue(SCANNER.nextLine());
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("readInt", List.of(), new DataType("String", false), classEnvironment, Set.of(AddonModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                return new IntValue(SCANNER.nextInt());
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("readFloat", List.of(), new DataType("String", false), classEnvironment, Set.of(AddonModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                return new DoubleValue(SCANNER.nextDouble());
            }
        });
    }
}