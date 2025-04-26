package me.itzisonn_.meazy_addon;

import me.itzisonn_.meazy.runtime.interpreter.InvalidValueException;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.runtime.value.number.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class AddonUtils {
    private AddonUtils() {}



    /**
     * Iterates over given collection and uses {@link RuntimeValue#getFinalValue()} function on all elements.
     *
     * @param collection Collection of RuntimeValues
     * @return Unpacked list
     */
    public static List<RuntimeValue<?>> unpackRuntimeValuesCollection(Collection<RuntimeValue<?>> collection) {
        List<RuntimeValue<?>> unpackedList = new ArrayList<>();
        for (RuntimeValue<?> runtimeValue : collection) {
            unpackedList.add(runtimeValue.getFinalRuntimeValue());
        }
        return unpackedList;
    }

    /**
     * Generates name with prefix:<br>
     * - If given name is uppercase, returns value in format PREFIX_NAME<br>
     * - Else returns value in format prefixName
     *
     * @param prefix Prefix
     * @param name Name
     * @return Generated name
     */
    public static String generatePrefixedName(String prefix, String name) {
        if (name.equals(name.toUpperCase())) return prefix.toUpperCase() + "_" + name;
        return prefix + name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static NumberValue<?> optimalNumberValue(double value) {
        if (value % 1 == 0) {
            if (value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE) return new IntValue((int) value);
            if (value >= Long.MIN_VALUE && value <= Long.MAX_VALUE) return new LongValue((long) value);
        }
        else {
            if (value >= -Float.MAX_VALUE && value <= Float.MAX_VALUE) return new FloatValue((float) value);
            if (value >= -Double.MAX_VALUE && value <= Double.MAX_VALUE) return new DoubleValue(value);
        }
        throw new InvalidValueException("Resulted value " + value + " is out of bounds");
    }
}
