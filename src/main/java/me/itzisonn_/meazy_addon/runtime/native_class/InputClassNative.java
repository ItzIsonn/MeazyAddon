package me.itzisonn_.meazy_addon.runtime.native_class;

import me.itzisonn_.meazy.runtime.native_annotation.Function;
import me.itzisonn_.meazy.runtime.native_annotation.NativeContainer;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import me.itzisonn_.meazy.runtime.value.classes.ClassValue;
import me.itzisonn_.meazy_addon.runtime.native_class.primitive.StringClassNative;
import me.itzisonn_.meazy_addon.runtime.value.number.DoubleValue;
import me.itzisonn_.meazy_addon.runtime.value.number.IntValue;

import java.util.Scanner;

@NativeContainer("data/program/input.mea")
public class InputClassNative {
    private static final Scanner SCANNER = new Scanner(System.in);

    @Function
    public static ClassValue read(FunctionEnvironment functionEnvironment) {
        return StringClassNative.newString(functionEnvironment, SCANNER.next());
    }

    @Function
    public static ClassValue readLine(FunctionEnvironment functionEnvironment) {
        return StringClassNative.newString(functionEnvironment, SCANNER.nextLine());
    }

    @Function
    public static IntValue readInt() {
        return new IntValue(SCANNER.nextInt());
    }

    @Function
    public static DoubleValue readDouble() {
        return new DoubleValue(SCANNER.nextDouble());
    }
}
