package me.itzisonn_.meazy_addon.runtime.evaluation_function.statement;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.runtime.MeazyNativeClass;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.FileEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.parser.ast.statement.UsingStatement;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.AbstractEvaluationFunction;

import java.io.File;
import java.util.ArrayList;

public class UsingStatementEvaluationFunction extends AbstractEvaluationFunction<UsingStatement> {
    public UsingStatementEvaluationFunction() {
        super("using_statement");
    }

    @Override
    public RuntimeValue<?> evaluate(UsingStatement usingStatement, RuntimeContext context, Environment environment, Object... extra) {
        if (!(environment instanceof FileEnvironment fileEnvironment)) {
            throw new InvalidSyntaxException("Can't use using statement in non-file environment");
        }

        Class<?> nativeClass;
        try {
            nativeClass = Class.forName(usingStatement.getClassName());
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException("Can't find native class", e);
        }
        if (!nativeClass.isAnnotationPresent(MeazyNativeClass.class)) {
            throw new InvalidSyntaxException("Can't use non-native class " + nativeClass.getName());
        }

        MeazyNativeClass nativeAnnotation = nativeClass.getAnnotation(MeazyNativeClass.class);
        if (nativeAnnotation == null) throw new InvalidSyntaxException("Can't use non-native class " + nativeClass.getName());

        ArrayList<File> accessibleFiles = new ArrayList<>();
        for (String file : nativeAnnotation.value()) {
            accessibleFiles.add(new File(file));
        }
        if (!accessibleFiles.isEmpty() && fileEnvironment.getParentFile() != null && !accessibleFiles.contains(fileEnvironment.getParentFile())) {
            throw new RuntimeException("Can't access native class " + nativeClass.getName());
        }

        fileEnvironment.addNativeClass(nativeClass);

        return null;
    }
}
