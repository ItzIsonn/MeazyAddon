package me.itzisonn_.meazy_addon.runtime.value.native_class.file;

import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.ClassDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.GlobalEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.value.NullValue;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.VariableValue;
import me.itzisonn_.meazy.runtime.value.classes.ClassValue;
import me.itzisonn_.meazy.runtime.value.classes.NativeClassValue;
import me.itzisonn_.meazy.runtime.value.classes.constructor.NativeConstructorValue;
import me.itzisonn_.meazy.runtime.value.function.NativeFunctionValue;
import me.itzisonn_.meazy_addon.parser.AddonModifiers;
import me.itzisonn_.meazy_addon.runtime.environment.ClassEnvironmentImpl;
import me.itzisonn_.meazy_addon.runtime.value.native_class.collections.ListClassValue;
import me.itzisonn_.meazy_addon.runtime.value.native_class.primitive.StringClassValue;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FileReaderClassValue extends NativeClassValue {
    private static GlobalEnvironment globalEnvironment = null;

    public FileReaderClassValue(ClassDeclarationEnvironment parent) {
        this(parent, null);
    }

    public FileReaderClassValue(BufferedReader fileReader) {
        this(globalEnvironment, fileReader);
    }

    public FileReaderClassValue(ClassDeclarationEnvironment parent, BufferedReader fileReader) {
        super(new ClassEnvironmentImpl(parent, false, "FileReader"));
        setupEnvironment(getEnvironment());
        getEnvironment().assignVariable("value", new InnerFileReaderValue(fileReader));

        if (parent instanceof GlobalEnvironment globalEnv) globalEnvironment = globalEnv;
    }

    @Override
    public void setupEnvironment(ClassEnvironment classEnvironment) {
        classEnvironment.declareVariable(new VariableValue(
                "value",
                new DataType("Any", false),
                null,
                false,
                Set.of(AddonModifiers.PRIVATE()),
                false,
                classEnvironment));


        classEnvironment.declareConstructor(new NativeConstructorValue(List.of(
                new CallArgExpression("file", new DataType("File", false), true)),
                classEnvironment, Set.of()) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {
                if (!(constructorArgs.getFirst().getFinalRuntimeValue() instanceof ClassValue classValue)) {
                    throw new InvalidSyntaxException("Can't create file reader from non-file value");
                }
                if (!classValue.getId().equals("File")) throw new InvalidSyntaxException("Can't create file reader from non-file value");
                if (!(classValue.getEnvironment().getVariableDeclarationEnvironment("value").getVariable("value").getValue() instanceof FileClassValue.InnerFileValue fileValue)) {
                    throw new InvalidSyntaxException("Can't create file reader from non-file value");
                }

                try {
                    constructorEnvironment.getVariableDeclarationEnvironment("value")
                            .assignVariable("value", new InnerFileReaderValue(new BufferedReader(new FileReader(fileValue.getValue()))));
                }
                catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });


        classEnvironment.declareFunction(new NativeFunctionValue("readLine", List.of(), new DataType("String", true), classEnvironment, Set.of()) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerFileReaderValue fileReaderValue)) throw new InvalidSyntaxException("Can't read from non-file reader value");

                try {
                    String line = fileReaderValue.getValue().readLine();
                    if (line != null) return new StringClassValue(line);
                    else return new NullValue();
                }
                catch (IOException e) {
                    throw new RuntimeException("Can't read file", e);
                }
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("readAllLines", List.of(), new DataType("List", false), classEnvironment, Set.of()) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerFileReaderValue fileReaderValue)) throw new InvalidSyntaxException("Can't read from non-file reader value");

                List<RuntimeValue<?>> lines = new ArrayList<>();
                for (String line : fileReaderValue.getValue().lines().toList()) {
                    lines.add(new StringClassValue(line));
                }
                return new ListClassValue(lines);
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("close", List.of(), null, classEnvironment, Set.of()) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerFileReaderValue fileReaderValue)) throw new InvalidSyntaxException("Can't close non-file reader value");

                try {
                    fileReaderValue.getValue().close();
                }
                catch (IOException e) {
                    throw new RuntimeException("Can't close file reader", e);
                }
                return null;
            }
        });
    }

    public static class InnerFileReaderValue extends RuntimeValue<BufferedReader> {
        private InnerFileReaderValue(BufferedReader value) {
            super(value);
        }
    }
}
