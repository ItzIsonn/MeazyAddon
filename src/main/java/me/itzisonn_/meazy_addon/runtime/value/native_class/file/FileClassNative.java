package me.itzisonn_.meazy_addon.runtime.value.native_class.file;

import me.itzisonn_.meazy.FileUtils;
import me.itzisonn_.meazy.runtime.MeazyNativeClass;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.classes.ClassValue;
import me.itzisonn_.meazy_addon.runtime.value.BooleanValue;
import me.itzisonn_.meazy_addon.runtime.value.impl.RuntimeValueImpl;
import me.itzisonn_.meazy_addon.runtime.value.native_class.primitive.StringClassValue;

import java.io.*;

@MeazyNativeClass("datagen/file/file.mea")
public class FileClassNative {
    public static InnerFileValue getNativeFile(RuntimeValue<?> path, FunctionEnvironment functionEnvironment) {
        return new InnerFileValue(new File(String.valueOf(path.getFinalValue())));
    }



    public static StringClassValue getPath(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("file").getVariable("file").getValue();
        if (!(value instanceof InnerFileValue fileValue)) throw new InvalidSyntaxException("Can't get path of non-file value");

        return new StringClassValue(fileValue.getValue().getPath());
    }

    public static StringClassValue getName(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("file").getVariable("file").getValue();
        if (!(value instanceof InnerFileValue fileValue)) throw new InvalidSyntaxException("Can't get name of non-file value");

        return new StringClassValue(fileValue.getValue().getName());
    }

    public static StringClassValue getExtension(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("file").getVariable("file").getValue();
        if (!(value instanceof InnerFileValue fileValue)) throw new InvalidSyntaxException("Can't get extension of non-file value");

        return new StringClassValue(FileUtils.getExtension(fileValue.getValue()));
    }



    public static StringClassValue getParent(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("file").getVariable("file").getValue();
        if (!(value instanceof InnerFileValue fileValue)) throw new InvalidSyntaxException("Can't get parent of non-file value");

        return new StringClassValue(fileValue.getValue().getParent());
    }



    public static BooleanValue exists(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("file").getVariable("file").getValue();
        if (!(value instanceof InnerFileValue fileValue)) throw new InvalidSyntaxException("Can't get data of non-file value");

        return new BooleanValue(fileValue.getValue().exists());
    }

    public static BooleanValue isHidden(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("file").getVariable("file").getValue();
        if (!(value instanceof InnerFileValue fileValue)) throw new InvalidSyntaxException("Can't get data of non-file value");

        return new BooleanValue(fileValue.getValue().isHidden());
    }

    public static BooleanValue isFile(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("file").getVariable("file").getValue();
        if (!(value instanceof InnerFileValue fileValue)) throw new InvalidSyntaxException("Can't get data of non-file value");

        return new BooleanValue(fileValue.getValue().isFile());
    }

    public static BooleanValue isDirectory(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("file").getVariable("file").getValue();
        if (!(value instanceof InnerFileValue fileValue)) throw new InvalidSyntaxException("Can't get data of non-file value");

        return new BooleanValue(fileValue.getValue().isDirectory());
    }




    public static BooleanValue isReadable(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("file").getVariable("file").getValue();
        if (!(value instanceof InnerFileValue fileValue)) throw new InvalidSyntaxException("Can't get data of non-file value");

        return new BooleanValue(fileValue.getValue().canRead());
    }

    public static BooleanValue isWritable(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("file").getVariable("file").getValue();
        if (!(value instanceof InnerFileValue fileValue)) throw new InvalidSyntaxException("Can't get data of non-file value");

        return new BooleanValue(fileValue.getValue().canWrite());
    }

    public static BooleanValue setReadable(RuntimeValue<?> value, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> file = functionEnvironment.getVariableDeclarationEnvironment("file").getVariable("file").getValue();
        if (!(file instanceof InnerFileValue fileValue)) throw new InvalidSyntaxException("Can't change readability of non-file value");

        if (!(value.getFinalRuntimeValue() instanceof BooleanValue booleanValue)) {
            throw new InvalidSyntaxException("Can't change readability to non-boolean value");
        }

        return new BooleanValue(fileValue.getValue().setReadable(booleanValue.getValue()));
    }

    public static BooleanValue setWritable(RuntimeValue<?> value, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> file = functionEnvironment.getVariableDeclarationEnvironment("file").getVariable("file").getValue();
        if (!(file instanceof InnerFileValue fileValue)) throw new InvalidSyntaxException("Can't change ability to write of non-file value");

        if (!(value.getFinalRuntimeValue() instanceof BooleanValue booleanValue)) {
            throw new InvalidSyntaxException("Can't change ability to write to non-boolean value");
        }

        return new BooleanValue(fileValue.getValue().setWritable(booleanValue.getValue()));
    }



    public static BooleanValue create(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("file").getVariable("file").getValue();
        if (!(value instanceof InnerFileValue fileValue)) throw new InvalidSyntaxException("Can't create non-file value");

        try {
            return new BooleanValue(fileValue.getValue().createNewFile());
        }
        catch (IOException e) {
            throw new RuntimeException("Can't create new file", e);
        }
    }

    public static BooleanValue delete(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("file").getVariable("file").getValue();
        if (!(value instanceof InnerFileValue fileValue)) throw new InvalidSyntaxException("Can't delete non-file value");

        return new BooleanValue(fileValue.getValue().delete());
    }

    public static BooleanValue rename(RuntimeValue<?> destination, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("file").getVariable("file").getValue();
        if (!(value instanceof InnerFileValue fileValue)) throw new InvalidSyntaxException("Can't delete non-file value");

        if (!(destination.getFinalRuntimeValue() instanceof ClassValue classValue)) {
            throw new InvalidSyntaxException("Can't rename file to non-file value");
        }
        if (!classValue.getId().equals("File")) throw new InvalidSyntaxException("Can't rename file to non-file value");
        if (!(classValue.getEnvironment().getVariableDeclarationEnvironment("file").getVariable("file").getValue() instanceof InnerFileValue destinationValue)) {
            throw new InvalidSyntaxException("Can't rename file to non-file value");
        }

        return new BooleanValue(fileValue.getValue().renameTo(destinationValue.getValue()));
    }

    public static BooleanValue createDirectory(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("file").getVariable("file").getValue();
        if (!(value instanceof InnerFileValue fileValue)) throw new InvalidSyntaxException("Can't create directory of non-file value");

        return new BooleanValue(fileValue.getValue().mkdir());
    }

    public static BooleanValue createDirectories(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("file").getVariable("file").getValue();
        if (!(value instanceof InnerFileValue fileValue)) throw new InvalidSyntaxException("Can't create directories of non-file value");

        return new BooleanValue(fileValue.getValue().mkdirs());
    }



    public static class InnerFileValue extends RuntimeValueImpl<File> {
        protected InnerFileValue(File value) {
            super(value);
        }
    }
}
