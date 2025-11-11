package me.itzisonn_.meazy_addon;

import me.itzisonn_.meazy.runtime.value.RuntimeValue;

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
}
