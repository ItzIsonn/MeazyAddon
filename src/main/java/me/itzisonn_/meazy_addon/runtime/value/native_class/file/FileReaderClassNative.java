package me.itzisonn_.meazy_addon.runtime.value.native_class.file;

import me.itzisonn_.meazy.runtime.MeazyNativeClass;
import me.itzisonn_.meazy.runtime.environment.*;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.value.NullValue;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.classes.ClassValue;
import me.itzisonn_.meazy_addon.runtime.value.impl.RuntimeValueImpl;
import me.itzisonn_.meazy_addon.runtime.value.native_class.collection.ListClassNative;
import me.itzisonn_.meazy_addon.runtime.value.native_class.primitive.StringClassValue;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@MeazyNativeClass("datagen/file/file_reader.mea")
public class FileReaderClassNative {
    public static InnerFileReaderValue getNativeFileReader(RuntimeValue<?> file, FunctionEnvironment functionEnvironment) {
        if (!(file.getFinalRuntimeValue() instanceof ClassValue classValue)) {
            throw new InvalidSyntaxException("Can't create file reader from non-file value");
        }
        if (!classValue.getId().equals("File")) throw new InvalidSyntaxException("Can't create file reader from non-file value");
        if (!(classValue.getEnvironment().getVariableDeclarationEnvironment("file").getVariable("file").getValue() instanceof FileClassNative.InnerFileValue fileValue)) {
            throw new InvalidSyntaxException("Can't create file reader from non-file value");
        }

        try {
            return new InnerFileReaderValue(new BufferedReader(new FileReader(fileValue.getValue())));
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }



    public static RuntimeValue<?> readLine(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("fileReader").getVariable("fileReader").getValue();
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

    public static ClassValue readAllLines(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("fileReader").getVariable("fileReader").getValue();
        if (!(value instanceof InnerFileReaderValue fileReaderValue)) throw new InvalidSyntaxException("Can't read from non-file reader value");

        List<RuntimeValue<?>> lines = new ArrayList<>();
        for (String line : fileReaderValue.getValue().lines().toList()) {
            lines.add(new StringClassValue(line));
        }
        return ListClassNative.newList(functionEnvironment, lines);
    }

    public static void close(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("fileReader").getVariable("fileReader").getValue();
        if (!(value instanceof InnerFileReaderValue fileReaderValue)) throw new InvalidSyntaxException("Can't close non-file reader value");

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
