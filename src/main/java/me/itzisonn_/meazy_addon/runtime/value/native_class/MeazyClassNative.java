package me.itzisonn_.meazy_addon.runtime.value.native_class;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.addon.Addon;
import me.itzisonn_.meazy.runtime.MeazyNativeClass;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.classes.ClassValue;
import me.itzisonn_.meazy_addon.runtime.value.native_class.collection.ListClassNative;
import me.itzisonn_.meazy_addon.runtime.value.native_class.primitive.StringClassValue;

import java.util.ArrayList;
import java.util.List;

@MeazyNativeClass("datagen/meazy.mea")
public class MeazyClassNative {
    public static StringClassValue getVersion(FunctionEnvironment functionEnvironment) {
        return new StringClassValue(MeazyMain.VERSION.toString());
    }

    public static ClassValue getAddons(FunctionEnvironment functionEnvironment) {
        List<RuntimeValue<?>> addons = new ArrayList<>();
        for (Addon addon : MeazyMain.ADDON_MANAGER.getAddons()) {
            addons.add(new StringClassValue(addon.getAddonInfo().getId()));
        }

        return ListClassNative.newList(functionEnvironment, addons);
    }
}
