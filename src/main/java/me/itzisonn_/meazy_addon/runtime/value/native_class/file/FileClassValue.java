package me.itzisonn_.meazy_addon.runtime.value.native_class.file;

import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.Utils;
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
import me.itzisonn_.meazy_addon.runtime.environment.ClassEnvironmentImpl;
import me.itzisonn_.meazy_addon.runtime.value.BooleanValue;
import me.itzisonn_.meazy_addon.runtime.value.native_class.primitive.StringClassValue;

import java.io.*;
import java.util.List;
import java.util.Set;

public class FileClassValue extends NativeClassValue {
    public FileClassValue(ClassDeclarationEnvironment parent) {
        this(parent, null);
    }

    public FileClassValue(File file) {
        this(Registries.GLOBAL_ENVIRONMENT.getEntry().getValue(), file);
    }

    public FileClassValue(ClassDeclarationEnvironment parent, File file) {
        super(getClassEnvironment(parent, file));
    }
    
    private static ClassEnvironment getClassEnvironment(ClassDeclarationEnvironment parent, File file) {
        ClassEnvironment classEnvironment = new ClassEnvironmentImpl(parent, false, "File");

        
        classEnvironment.declareVariable(new VariableValue(
                "value",
                new DataType("Any", false),
                new InnerFileValue(file),
                false,
                Set.of(AddonModifiers.PRIVATE()),
                false));


        classEnvironment.declareConstructor(new NativeConstructorValue(List.of(
                new CallArgExpression("path", new DataType("String", false), true)),
                classEnvironment, Set.of()) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {
                if (!(constructorArgs.getFirst().getFinalRuntimeValue() instanceof StringClassValue stringValue)) {
                    throw new InvalidSyntaxException("Can't create file with non-string path value");
                }

                constructorEnvironment.getVariableDeclarationEnvironment("value").assignVariable("value", new InnerFileValue(new File(stringValue.getValue())));
            }
        });


        classEnvironment.declareFunction(new NativeFunctionValue("getPath", List.of(), new DataType("String", false), classEnvironment, Set.of()) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerFileValue fileValue)) throw new InvalidSyntaxException("Can't get path of non-file value");

                return new StringClassValue(fileValue.getValue().getPath());
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("getName", List.of(), new DataType("String", false), classEnvironment, Set.of()) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerFileValue fileValue)) throw new InvalidSyntaxException("Can't get name of non-file value");

                return new StringClassValue(fileValue.getValue().getName());
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("getExtension", List.of(), new DataType("String", false), classEnvironment, Set.of()) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerFileValue fileValue)) throw new InvalidSyntaxException("Can't get extension of non-file value");

                return new StringClassValue(Utils.getExtension(fileValue.getValue()));
            }
        });


        classEnvironment.declareFunction(new NativeFunctionValue("getParent", List.of(), new DataType("String", false), classEnvironment, Set.of()) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerFileValue fileValue)) throw new InvalidSyntaxException("Can't get parent of non-file value");

                return new StringClassValue(fileValue.getValue().getParent());
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("getParentFile", List.of(), new DataType("File", false), classEnvironment, Set.of()) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerFileValue fileValue)) throw new InvalidSyntaxException("Can't get parent file of non-file value");

                return new FileClassValue(fileValue.getValue().getParentFile());
            }
        });


        classEnvironment.declareFunction(new NativeFunctionValue("exists", List.of(), new DataType("Boolean", false), classEnvironment, Set.of()) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerFileValue fileValue)) throw new InvalidSyntaxException("Can't get data of non-file value");

                return new BooleanValue(fileValue.getValue().exists());
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("isReadable", List.of(), new DataType("Boolean", false), classEnvironment, Set.of()) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerFileValue fileValue)) throw new InvalidSyntaxException("Can't get data of non-file value");

                return new BooleanValue(fileValue.getValue().canRead());
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("isWritable", List.of(), new DataType("Boolean", false), classEnvironment, Set.of()) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerFileValue fileValue)) throw new InvalidSyntaxException("Can't get data of non-file value");

                return new BooleanValue(fileValue.getValue().canWrite());
            }
        });


        classEnvironment.declareFunction(new NativeFunctionValue("isFile", List.of(), new DataType("Boolean", false), classEnvironment, Set.of()) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerFileValue fileValue)) throw new InvalidSyntaxException("Can't get data of non-file value");

                return new BooleanValue(fileValue.getValue().isFile());
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("isDirectory", List.of(), new DataType("Boolean", false), classEnvironment, Set.of()) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerFileValue fileValue)) throw new InvalidSyntaxException("Can't get data of non-file value");

                return new BooleanValue(fileValue.getValue().isDirectory());
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("isHidden", List.of(), new DataType("Boolean", false), classEnvironment, Set.of()) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerFileValue fileValue)) throw new InvalidSyntaxException("Can't get data of non-file value");

                return new BooleanValue(fileValue.getValue().isHidden());
            }
        });


        classEnvironment.declareFunction(new NativeFunctionValue("create", List.of(), new DataType("Boolean", false), classEnvironment, Set.of()) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerFileValue fileValue)) throw new InvalidSyntaxException("Can't create non-file value");

                try {
                    return new BooleanValue(fileValue.getValue().createNewFile());
                }
                catch (IOException e) {
                    throw new RuntimeException("Can't create new file", e);
                }
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("delete", List.of(), new DataType("Boolean", false), classEnvironment, Set.of()) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerFileValue fileValue)) throw new InvalidSyntaxException("Can't delete non-file value");

                return new BooleanValue(fileValue.getValue().delete());
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("rename", List.of(
                new CallArgExpression("destination", new DataType("File", false), true)),
                new DataType("Boolean", false), classEnvironment, Set.of()) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerFileValue fileValue)) throw new InvalidSyntaxException("Can't delete non-file value");

                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof ClassValue classValue)) {
                    throw new InvalidSyntaxException("Can't rename file to non-file value");
                }
                if (!classValue.getId().equals("File")) throw new InvalidSyntaxException("Can't rename file to non-file value");
                if (!(classValue.getEnvironment().getVariableDeclarationEnvironment("value").getVariable("value").getValue() instanceof InnerFileValue destinationValue)) {
                    throw new InvalidSyntaxException("Can't rename file to non-file value");
                }

                return new BooleanValue(fileValue.getValue().renameTo(destinationValue.getValue()));
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("createDirectory", List.of(), new DataType("Boolean", false), classEnvironment, Set.of()) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerFileValue fileValue)) throw new InvalidSyntaxException("Can't create directory of non-file value");

                return new BooleanValue(fileValue.getValue().mkdir());
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("createDirectories", List.of(), new DataType("Boolean", false), classEnvironment, Set.of()) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerFileValue fileValue)) throw new InvalidSyntaxException("Can't create directories of non-file value");

                return new BooleanValue(fileValue.getValue().mkdirs());
            }
        });


        classEnvironment.declareFunction(new NativeFunctionValue("setReadable", List.of(
                new CallArgExpression("bool", new DataType("Boolean", false), true)),
                new DataType("Boolean", false), classEnvironment, Set.of()) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerFileValue fileValue)) throw new InvalidSyntaxException("Can't change readability of non-file value");

                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof BooleanValue booleanValue)) {
                    throw new InvalidSyntaxException("Can't change readability to non-boolean value");
                }

                return new BooleanValue(fileValue.getValue().setReadable(booleanValue.getValue()));
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("setWritable", List.of(
                new CallArgExpression("bool", new DataType("Boolean", false), true)),
                new DataType("Boolean", false), classEnvironment, Set.of()) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerFileValue fileValue)) throw new InvalidSyntaxException("Can't change ability to write of non-file value");

                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof BooleanValue booleanValue)) {
                    throw new InvalidSyntaxException("Can't change ability to write to non-boolean value");
                }

                return new BooleanValue(fileValue.getValue().setWritable(booleanValue.getValue()));
            }
        });


        classEnvironment.declareFunction(new NativeFunctionValue("getReader", List.of(), new DataType("FileReader", false), classEnvironment, Set.of()) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerFileValue fileValue)) throw new InvalidSyntaxException("Can't get reader of non-file value");

                try {
                    return new FileReaderClassValue(new BufferedReader(new FileReader(fileValue.getValue())));
                }
                catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("getWriter", List.of(), new DataType("FileWriter", false), classEnvironment, Set.of()) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerFileValue fileValue)) throw new InvalidSyntaxException("Can't get writer of non-file value");

                return new FileWriterClassValue(fileValue.getValue());
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
