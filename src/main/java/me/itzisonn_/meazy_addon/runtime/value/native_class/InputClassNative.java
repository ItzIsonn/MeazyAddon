package me.itzisonn_.meazy_addon.runtime.value.native_class;

import me.itzisonn_.meazy.runtime.MeazyNativeClass;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import me.itzisonn_.meazy_addon.runtime.value.native_class.primitive.StringClassValue;
import me.itzisonn_.meazy_addon.runtime.value.number.DoubleValue;
import me.itzisonn_.meazy_addon.runtime.value.number.IntValue;

import java.util.Scanner;

@MeazyNativeClass("data/program/input.mea")
public class InputClassNative {
    private static final Scanner SCANNER = new Scanner(System.in);

    public static StringClassValue read(FunctionEnvironment functionEnvironment) {
        return new StringClassValue(functionEnvironment.getFileEnvironment(), SCANNER.next());
    }

    public static StringClassValue readLine(FunctionEnvironment functionEnvironment) {
        return new StringClassValue(functionEnvironment.getFileEnvironment(), SCANNER.nextLine());
    }

    public static IntValue readInt(FunctionEnvironment functionEnvironment) {
        return new IntValue(SCANNER.nextInt());
    }

    public static DoubleValue readDouble(FunctionEnvironment functionEnvironment) {
        return new DoubleValue(SCANNER.nextDouble());
    }
}
