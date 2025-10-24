package me.itzisonn_.meazy_addon.runtime.evaluation_function.statement;

import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.runtime.environment.*;
import me.itzisonn_.meazy.runtime.interpreter.Interpreter;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.classes.ClassValue;
import me.itzisonn_.meazy_addon.parser.ast.statement.ClassDeclarationStatement;
import me.itzisonn_.meazy_addon.parser.data_type.DataTypeImpl;
import me.itzisonn_.meazy_addon.parser.modifier.AddonModifiers;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.AbstractEvaluationFunction;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.EvaluationHelper;
import me.itzisonn_.meazy_addon.runtime.value.impl.VariableValueImpl;
import me.itzisonn_.meazy_addon.runtime.value.impl.classes.NativeClassValueImpl;
import me.itzisonn_.meazy_addon.runtime.value.impl.classes.RuntimeClassValueImpl;
import me.itzisonn_.meazy_addon.runtime.value.impl.function.NativeFunctionValueImpl;
import me.itzisonn_.meazy_addon.runtime.value.native_class.collection.ListClassNative;
import me.itzisonn_.meazy_addon.runtime.value.number.IntValue;

import java.lang.reflect.AccessFlag;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassDeclarationStatementEvaluationFunction extends AbstractEvaluationFunction<ClassDeclarationStatement> {
    public ClassDeclarationStatementEvaluationFunction() {
        super("class_declaration_statement");
    }

    @Override
    public RuntimeValue<?> evaluate(ClassDeclarationStatement classDeclarationStatement, RuntimeContext context, Environment environment, Object... extra) {
        if (!(environment instanceof ClassDeclarationEnvironment classDeclarationEnvironment)) {
            throw new InvalidSyntaxException("Can't declare class in this environment");
        }

        for (Modifier modifier : classDeclarationStatement.getModifiers()) {
            if (!modifier.canUse(classDeclarationStatement, context, environment))
                throw new InvalidSyntaxException("Can't use '" + modifier.getId() + "' Modifier");
        }

        ClassEnvironment classEnvironment = Registries.CLASS_ENVIRONMENT_FACTORY.getEntry().getValue().create(
                classDeclarationEnvironment,
                true,
                classDeclarationStatement.getId(),
                classDeclarationStatement.getModifiers());

        Interpreter interpreter = context.getInterpreter();
        for (Statement statement : classDeclarationStatement.getBody()) {
            interpreter.evaluate(statement, classEnvironment);
        }

        for (String enumId : classDeclarationStatement.getEnumIds().keySet()) {
            classEnvironment.declareVariable(new VariableValueImpl(
                    enumId,
                    new DataTypeImpl(classDeclarationStatement.getId(), false),
                    (RuntimeValue<?>) null,
                    true,
                    Set.of(AddonModifiers.SHARED()),
                    false,
                    classEnvironment
            ));
        }

        ClassValue classValue = null;
        if (classDeclarationStatement.getModifiers().contains(AddonModifiers.NATIVE())) {
            for (Class<?> nativeClass : environment.getFileEnvironment().getNativeClasses()) {
                Method method;
                try {
                    method = nativeClass.getDeclaredMethod("newInstance", Set.class, ClassEnvironment.class, List.class);
                }
                catch (NoSuchMethodException e) {
                    continue;
                }

                if (!method.accessFlags().contains(AccessFlag.STATIC)) {
                    throw new InvalidSyntaxException("Can't call non-static native method to create new instance of class with id " + classEnvironment.getId());
                }
                if (!method.canAccess(null)) {
                    throw new InvalidSyntaxException("Can't call non-accessible native method to create new instance of class with id " + classEnvironment.getId());
                }
                if (!ClassValue.class.isAssignableFrom(method.getReturnType())) {
                    throw new RuntimeException("Return value of native method with id " + method.getName() + " is invalid");
                }

                try {
                    Object object = method.invoke(null, classDeclarationStatement.getBaseClasses(), classEnvironment, classDeclarationStatement.getBody());
                    classValue = (ClassValue) object;
                }
                catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException("Failed to call native method", e);
                }
            }
        }

        if (classValue == null) {
            classValue = new RuntimeClassValueImpl(
                    classDeclarationStatement.getBaseClasses(),
                    classEnvironment,
                    classDeclarationStatement.getBody());
        }

        classDeclarationEnvironment.declareClass(classValue);

        int enumOrdinal = 1;
        List<ClassValue> enumValues = new ArrayList<>();
        for (String enumId : classDeclarationStatement.getEnumIds().keySet()) {
            List<RuntimeValue<?>> args = classDeclarationStatement.getEnumIds().get(enumId).stream().map(expression -> interpreter.evaluate(expression, classEnvironment)).collect(Collectors.toList());
            ClassEnvironment enumEnvironment = EvaluationHelper.initClassEnvironment(context, classValue, classEnvironment, args);

            int finalEnumOrdinal = enumOrdinal;
            enumEnvironment.declareFunction(new NativeFunctionValueImpl("getOrdinal", List.of(), new DataTypeImpl("Int", false), enumEnvironment, Set.of()) {
                @Override
                public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, RuntimeContext context, FunctionEnvironment functionEnvironment) {
                    return new IntValue(finalEnumOrdinal);
                }
            });
            enumOrdinal++;

            ClassValue enumValue = new NativeClassValueImpl(enumEnvironment);
            classEnvironment.assignVariable(enumId, enumValue);
            enumValues.add(enumValue);
        }

        if (classDeclarationStatement.getModifiers().contains(AddonModifiers.ENUM())) {
            classEnvironment.declareFunction(new NativeFunctionValueImpl("getValues", List.of(), new DataTypeImpl("List", false), classEnvironment, Set.of(AddonModifiers.SHARED())) {
                @Override
                public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, RuntimeContext context, FunctionEnvironment functionEnvironment) {
                    return ListClassNative.newList(functionEnvironment, context, new ArrayList<>(enumValues));
                }
            });
        }

        return null;
    }
}
