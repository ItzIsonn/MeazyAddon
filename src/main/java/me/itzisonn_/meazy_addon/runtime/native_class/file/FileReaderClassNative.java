package me.itzisonn_.meazy_addon.runtime.native_class.file;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.runtime.native_annotation.Argument;
import me.itzisonn_.meazy.runtime.native_annotation.Function;
import me.itzisonn_.meazy.runtime.native_annotation.NativeContainer;
import me.itzisonn_.meazy.runtime.environment.*;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.ClassValue;
import me.itzisonn_.meazy_addon.runtime.value.NullValue;
import me.itzisonn_.meazy_addon.runtime.value.impl.RuntimeValueImpl;
import me.itzisonn_.meazy_addon.runtime.native_class.collection.ListClassNative;
import me.itzisonn_.meazy_addon.runtime.native_class.primitive.StringClassNative;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@NativeContainer("data/program/file/file_reader.mea")
public class FileReaderClassNative {
    @Function
    public static InnerFileReaderValue getNativeFileReader(@Argument RuntimeValue<?> file) {
        if (!(file.getFinalRuntimeValue() instanceof ClassValue classValue)) {
            throw new RuntimeException("Can't create file reader from non-file value");
        }
        if (!classValue.getId().equals("File")) throw new RuntimeException("Can't create file reader from non-file value");
        if (!(classValue.getEnvironment().getVariableDeclarationEnvironment("file").getVariable("file").getValue() instanceof FileClassNative.InnerFileValue fileValue)) {
            throw new RuntimeException("Can't create file reader from non-file value");
        }

        try {
            return new InnerFileReaderValue(new BufferedReader(new FileReader(fileValue.getValue())));
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }



    @Function
    public static RuntimeValue<?> readLine(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("fileReader").getVariable("fileReader").getValue();
        if (!(value instanceof InnerFileReaderValue fileReaderValue)) throw new RuntimeException("Can't read from non-file reader value");

        try {
            String line = fileReaderValue.getValue().readLine();
            if (line != null) return StringClassNative.newString(functionEnvironment.getFileEnvironment(), line);
            else return new NullValue();
        }
        catch (IOException e) {
            throw new RuntimeException("Can't read file", e);
        }
    }

    @Function
    public static ClassValue readAllLines(RuntimeContext context, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("fileReader").getVariable("fileReader").getValue();
        if (!(value instanceof InnerFileReaderValue fileReaderValue)) throw new RuntimeException("Can't read from non-file reader value");

        List<RuntimeValue<?>> lines = new ArrayList<>();
        for (String line : fileReaderValue.getValue().lines().toList()) {
            lines.add(StringClassNative.newString(functionEnvironment.getFileEnvironment(), line));
        }
        return ListClassNative.newList(functionEnvironment, context, lines);
    }

    @Function
    public static void close(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("fileReader").getVariable("fileReader").getValue();
        if (!(value instanceof InnerFileReaderValue fileReaderValue)) throw new RuntimeException("Can't close non-file reader value");

        try {
            fileReaderValue.getValue().close();
        }
        catch (IOException e) {
            throw new RuntimeException("Can't close file reader", e);
        }
    }



    public static class InnerFileReaderValue extends RuntimeValueImpl<BufferedReader> {
        protected InnerFileReaderValue(BufferedReader value) {
            super(value);
        }
    }
}
