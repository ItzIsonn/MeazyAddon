package me.itzisonn_.meazy_addon.runtime.native_class.file;

import me.itzisonn_.meazy.FileUtils;
import me.itzisonn_.meazy.runtime.native_annotation.Argument;
import me.itzisonn_.meazy.runtime.native_annotation.Function;
import me.itzisonn_.meazy.runtime.native_annotation.NativeContainer;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.ClassValue;
import me.itzisonn_.meazy_addon.runtime.value.BooleanValue;
import me.itzisonn_.meazy_addon.runtime.value.impl.RuntimeValueImpl;
import me.itzisonn_.meazy_addon.runtime.native_class.primitive.StringClassNative;

import java.io.*;

@NativeContainer("data/program/file/file.mea")
public class FileClassNative {
    @Function
    public static InnerFileValue getNativeFile(@Argument RuntimeValue<?> path) {
        return new InnerFileValue(new File(String.valueOf(path.getFinalValue())));
    }



    @Function
    public static ClassValue getPath(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("file").getVariable("file").getValue();
        if (!(value instanceof InnerFileValue fileValue)) throw new RuntimeException("Can't get path of non-file value");

        return StringClassNative.newString(functionEnvironment.getFileEnvironment(), fileValue.getValue().getPath());
    }

    @Function
    public static ClassValue getName(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("file").getVariable("file").getValue();
        if (!(value instanceof InnerFileValue fileValue)) throw new RuntimeException("Can't get name of non-file value");

        return StringClassNative.newString(functionEnvironment.getFileEnvironment(), fileValue.getValue().getName());
    }

    @Function
    public static ClassValue getExtension(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("file").getVariable("file").getValue();
        if (!(value instanceof InnerFileValue fileValue)) throw new RuntimeException("Can't get extension of non-file value");

        return StringClassNative.newString(functionEnvironment.getFileEnvironment(), FileUtils.getExtension(fileValue.getValue()));
    }



    @Function
    public static ClassValue getParent(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("file").getVariable("file").getValue();
        if (!(value instanceof InnerFileValue fileValue)) throw new RuntimeException("Can't get parent of non-file value");

        return StringClassNative.newString(functionEnvironment.getFileEnvironment(), fileValue.getValue().getParent());
    }



    @Function
    public static BooleanValue exists(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("file").getVariable("file").getValue();
        if (!(value instanceof InnerFileValue fileValue)) throw new RuntimeException("Can't get data of non-file value");

        return BooleanValue.of(fileValue.getValue().exists());
    }

    @Function
    public static BooleanValue isHidden(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("file").getVariable("file").getValue();
        if (!(value instanceof InnerFileValue fileValue)) throw new RuntimeException("Can't get data of non-file value");

        return BooleanValue.of(fileValue.getValue().isHidden());
    }

    @Function
    public static BooleanValue isFile(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("file").getVariable("file").getValue();
        if (!(value instanceof InnerFileValue fileValue)) throw new RuntimeException("Can't get data of non-file value");

        return BooleanValue.of(fileValue.getValue().isFile());
    }

    @Function
    public static BooleanValue isDirectory(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("file").getVariable("file").getValue();
        if (!(value instanceof InnerFileValue fileValue)) throw new RuntimeException("Can't get data of non-file value");

        return BooleanValue.of(fileValue.getValue().isDirectory());
    }




    @Function
    public static BooleanValue isReadable(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("file").getVariable("file").getValue();
        if (!(value instanceof InnerFileValue fileValue)) throw new RuntimeException("Can't get data of non-file value");

        return BooleanValue.of(fileValue.getValue().canRead());
    }

    @Function
    public static BooleanValue isWritable(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("file").getVariable("file").getValue();
        if (!(value instanceof InnerFileValue fileValue)) throw new RuntimeException("Can't get data of non-file value");

        return BooleanValue.of(fileValue.getValue().canWrite());
    }

    @Function
    public static BooleanValue setReadable(@Argument RuntimeValue<?> value, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> file = functionEnvironment.getVariableDeclarationEnvironment("file").getVariable("file").getValue();
        if (!(file instanceof InnerFileValue fileValue)) throw new RuntimeException("Can't change readability of non-file value");

        if (!(value.getFinalRuntimeValue() instanceof BooleanValue booleanValue)) {
            throw new RuntimeException("Can't change readability to non-boolean value");
        }

        return BooleanValue.of(fileValue.getValue().setReadable(booleanValue.getValue()));
    }

    @Function
    public static BooleanValue setWritable(@Argument RuntimeValue<?> value, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> file = functionEnvironment.getVariableDeclarationEnvironment("file").getVariable("file").getValue();
        if (!(file instanceof InnerFileValue fileValue)) throw new RuntimeException("Can't change ability to write of non-file value");

        if (!(value.getFinalRuntimeValue() instanceof BooleanValue booleanValue)) {
            throw new RuntimeException("Can't change ability to write to non-boolean value");
        }

        return BooleanValue.of(fileValue.getValue().setWritable(booleanValue.getValue()));
    }



    @Function
    public static BooleanValue create(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("file").getVariable("file").getValue();
        if (!(value instanceof InnerFileValue fileValue)) throw new RuntimeException("Can't create non-file value");

        try {
            return BooleanValue.of(fileValue.getValue().createNewFile());
        }
        catch (IOException e) {
            throw new RuntimeException("Can't create new file", e);
        }
    }

    @Function
    public static BooleanValue delete(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("file").getVariable("file").getValue();
        if (!(value instanceof InnerFileValue fileValue)) throw new RuntimeException("Can't delete non-file value");

        return BooleanValue.of(fileValue.getValue().delete());
    }

    @Function
    public static BooleanValue rename(@Argument RuntimeValue<?> destination, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("file").getVariable("file").getValue();
        if (!(value instanceof InnerFileValue fileValue)) throw new RuntimeException("Can't delete non-file value");

        if (!(destination.getFinalRuntimeValue() instanceof ClassValue classValue)) {
            throw new RuntimeException("Can't rename file to non-file value");
        }
        if (!classValue.getId().equals("File")) throw new RuntimeException("Can't rename file to non-file value");
        if (!(classValue.getEnvironment().getVariableDeclarationEnvironment("file").getVariable("file").getValue() instanceof InnerFileValue destinationValue)) {
            throw new RuntimeException("Can't rename file to non-file value");
        }

        return BooleanValue.of(fileValue.getValue().renameTo(destinationValue.getValue()));
    }

    @Function
    public static BooleanValue createDirectory(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("file").getVariable("file").getValue();
        if (!(value instanceof InnerFileValue fileValue)) throw new RuntimeException("Can't create directory of non-file value");

        return BooleanValue.of(fileValue.getValue().mkdir());
    }

    @Function
    public static BooleanValue createDirectories(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("file").getVariable("file").getValue();
        if (!(value instanceof InnerFileValue fileValue)) throw new RuntimeException("Can't create directories of non-file value");

        return BooleanValue.of(fileValue.getValue().mkdirs());
    }



    public static class InnerFileValue extends RuntimeValueImpl<File> {
        protected InnerFileValue(File value) {
            super(value);
        }
    }
}
