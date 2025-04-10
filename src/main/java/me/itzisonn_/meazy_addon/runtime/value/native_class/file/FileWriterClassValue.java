package me.itzisonn_.meazy_addon.runtime.value.native_class.file;

import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.ClassDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.VariableValue;
import me.itzisonn_.meazy.runtime.value.classes.ClassValue;
import me.itzisonn_.meazy.runtime.value.classes.NativeClassValue;
import me.itzisonn_.meazy.runtime.value.classes.constructors.NativeConstructorValue;
import me.itzisonn_.meazy.runtime.value.function.NativeFunctionValue;
import me.itzisonn_.meazy_addon.parser.AddonModifiers;
import me.itzisonn_.meazy_addon.runtime.value.native_class.collections.CollectionClassValue;
import me.itzisonn_.meazy_addon.runtime.environment.ClassEnvironmentImpl;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Set;

public class FileWriterClassValue extends NativeClassValue {
    public FileWriterClassValue(ClassDeclarationEnvironment parent) {
        this(parent, null);
    }

    public FileWriterClassValue(File file) {
        this(Registries.GLOBAL_ENVIRONMENT.getEntry().getValue(), file);
    }

    public FileWriterClassValue(ClassDeclarationEnvironment parent, File file) {
        super(getClassEnvironment(parent, file));
    }

    private static ClassEnvironment getClassEnvironment(ClassDeclarationEnvironment parent, File file) {
        ClassEnvironment classEnvironment = new ClassEnvironmentImpl(parent, false, "FileWriter");


        classEnvironment.declareVariable(new VariableValue(
                "value",
                new DataType("Any", false),
                new InnerFileValue(file),
                false,
                Set.of(AddonModifiers.PRIVATE()),
                false));


        classEnvironment.declareConstructor(new NativeConstructorValue(List.of(
                new CallArgExpression("file", new DataType("File", false), true)),
                classEnvironment, Set.of()) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {
                if (!(constructorArgs.getFirst().getFinalRuntimeValue() instanceof ClassValue classValue)) {
                    throw new InvalidSyntaxException("Can't create file writer from non-file value");
                }
                if (!classValue.getId().equals("File")) throw new InvalidSyntaxException("Can't create file writer from non-file value");
                if (!(classValue.getEnvironment().getVariableDeclarationEnvironment("value").getVariable("value").getValue() instanceof FileClassValue.InnerFileValue fileValue)) {
                    throw new InvalidSyntaxException("Can't create file writer from non-file value");
                }

                constructorEnvironment.getVariableDeclarationEnvironment("value")
                        .assignVariable("value", new InnerFileValue(fileValue.getValue()));
            }
        });


        classEnvironment.declareFunction(new NativeFunctionValue("write", List.of(
                new CallArgExpression("writeValue", new DataType("Any", false), true)),
                null, classEnvironment, Set.of()) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerFileValue fileValue)) throw new InvalidSyntaxException("Can't write with non-file value");

                try {
                    Files.write(fileValue.getValue().toPath(), String.valueOf(functionArgs.getFirst().getFinalValue()).getBytes());
                }
                catch (IOException e) {
                    throw new RuntimeException("Can't write to file", e);
                }

                return null;
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("writeAppend", List.of(
                new CallArgExpression("writeValue", new DataType("Any", false), true)),
                null, classEnvironment, Set.of()) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerFileValue fileValue)) throw new InvalidSyntaxException("Can't write with non-file value");

                try {
                    Files.write(fileValue.getValue().toPath(), String.valueOf(functionArgs.getFirst().getFinalValue()).getBytes(), StandardOpenOption.APPEND);
                }
                catch (IOException e) {
                    throw new RuntimeException("Can't write to file", e);
                }

                return null;
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("writeLines", List.of(
                new CallArgExpression("collection", new DataType("Collection", false), true)),
                null, classEnvironment, Set.of()) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerFileValue fileValue)) throw new InvalidSyntaxException("Can't write with non-file value");

                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof ClassValue classValue)) {
                    throw new InvalidSyntaxException("Can't write non-collection value");
                }
                if (!classValue.getBaseClasses().contains("Collection")) throw new InvalidSyntaxException("Can't write non-collection value");
                if (!(classValue.getEnvironment().getVariableDeclarationEnvironment("value").getVariable("value").getValue() instanceof CollectionClassValue.InnerCollectionValue<?> collectionValue)) {
                    throw new InvalidSyntaxException("Can't write non-collection value");
                }

                try {
                    Files.write(
                            fileValue.getValue().toPath(),
                            collectionValue.getValue().stream().map(runtimeValue -> String.valueOf(runtimeValue.getFinalValue())).toList(),
                            StandardCharsets.UTF_8);
                }
                catch (IOException e) {
                    throw new RuntimeException("Can't write to file", e);
                }

                return null;
            }
        });

        return classEnvironment;
    }

    public static class InnerFileValue extends RuntimeValue<File> {
        private InnerFileValue(File value) {
            super(value);
        }
    }
}
