package me.itzisonn_.meazy_addon.runtime.native_class.file;

import me.itzisonn_.meazy.runtime.MeazyNativeClass;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.classes.ClassValue;
import me.itzisonn_.meazy_addon.runtime.value.impl.RuntimeValueImpl;
import me.itzisonn_.meazy_addon.runtime.native_class.collection.InnerCollectionValue;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

@MeazyNativeClass("data/program/file/file_writer.mea")
public class FileWriterClassNative {
    public static InnerFileValue getNativeFile(RuntimeValue<?> file, FunctionEnvironment functionEnvironment) {
        if (!(file.getFinalRuntimeValue() instanceof ClassValue classValue)) {
            throw new InvalidSyntaxException("Can't create file writer from non-file value");
        }
        if (!classValue.getId().equals("File")) throw new InvalidSyntaxException("Can't create file writer from non-file value");
        if (!(classValue.getEnvironment().getVariableDeclarationEnvironment("file").getVariable("file").getValue() instanceof FileClassNative.InnerFileValue fileValue)) {
            throw new InvalidSyntaxException("Can't create file writer from non-file value");
        }

        return new InnerFileValue(fileValue.getValue());
    }



    public static void write(RuntimeValue<?> value, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> file = functionEnvironment.getVariableDeclarationEnvironment("file").getVariable("file").getValue();
        if (!(file instanceof InnerFileValue fileValue)) throw new InvalidSyntaxException("Can't write with non-file value");

        try {
            Files.write(fileValue.getValue().toPath(), String.valueOf(value.getFinalValue()).getBytes());
        }
        catch (IOException e) {
            throw new RuntimeException("Can't write to file", e);
        }
    }

    public static void writeAppend(RuntimeValue<?> value, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> file = functionEnvironment.getVariableDeclarationEnvironment("file").getVariable("file").getValue();
        if (!(file instanceof InnerFileValue fileValue)) throw new InvalidSyntaxException("Can't write with non-file value");

        try {
            Files.write(fileValue.getValue().toPath(), String.valueOf(value.getFinalValue()).getBytes(), StandardOpenOption.APPEND);
        }
        catch (IOException e) {
            throw new RuntimeException("Can't write to file", e);
        }
    }

    public static void writeLines(RuntimeValue<?> value, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> file = functionEnvironment.getVariableDeclarationEnvironment("file").getVariable("file").getValue();
        if (!(file instanceof InnerFileValue fileValue)) throw new InvalidSyntaxException("Can't write with non-file value");

        if (!(value.getFinalRuntimeValue() instanceof ClassValue classValue)) {
            throw new InvalidSyntaxException("Can't write non-collection value");
        }
        if (!classValue.getBaseClasses().contains("Collection")) throw new InvalidSyntaxException("Can't write non-collection value");
        if (!(classValue.getEnvironment().getVariableDeclarationEnvironment("collection").getVariable("collection").getValue() instanceof InnerCollectionValue<?> collectionValue)) {
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
    }



    public static class InnerFileValue extends RuntimeValueImpl<File> {
        private InnerFileValue(File value) {
            super(value);
        }
    }
}
