package me.itzisonn_.meazy_addon.runtime.value.native_class;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.addon.Addon;
import me.itzisonn_.meazy.addon.addon_info.AddonInfo;
import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.runtime.MeazyNativeClass;
import me.itzisonn_.meazy.runtime.environment.FileEnvironment;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.classes.ClassValue;
import me.itzisonn_.meazy_addon.runtime.AddonEvaluationFunctions;
import me.itzisonn_.meazy_addon.runtime.value.native_class.collection.ListClassNative;
import me.itzisonn_.meazy_addon.runtime.value.native_class.primitive.StringClassValue;

import java.util.ArrayList;
import java.util.List;

@MeazyNativeClass("data/program/meazy/meazy.mea")
public class MeazyClassNative {
    public static StringClassValue getVersion(FunctionEnvironment functionEnvironment) {
        return new StringClassValue(functionEnvironment.getFileEnvironment(), MeazyMain.VERSION.toString());
    }

    public static ClassValue getAddons(RuntimeContext context, FunctionEnvironment functionEnvironment) {
        FileEnvironment fileEnvironment = functionEnvironment.getFileEnvironment();

        List<RuntimeValue<?>> addons = new ArrayList<>();
        for (Addon addon : MeazyMain.ADDON_MANAGER.getAddons()) {
            AddonInfo addonInfo = addon.getAddonInfo();
            ClassValue addonClassValue = AddonEvaluationFunctions.callEmptyClassValue(context, functionEnvironment.getFileEnvironment().getClass("Addon"));

            addonClassValue.getEnvironment().assignVariable("id", new StringClassValue(fileEnvironment, addonInfo.getId()));
            addonClassValue.getEnvironment().assignVariable("version", new StringClassValue(fileEnvironment, addonInfo.getVersion().toString()));
            addonClassValue.getEnvironment().assignVariable("description", new StringClassValue(fileEnvironment, addonInfo.getDescription()));
            List<RuntimeValue<?>> authors = new ArrayList<>();
            for (String author : addonInfo.getAuthors()) {
                authors.add(new StringClassValue(fileEnvironment, author));
            }
            addonClassValue.getEnvironment().assignVariable("authors", ListClassNative.newList(functionEnvironment, context, authors));

            addons.add(addonClassValue);
        }

        return ListClassNative.newList(functionEnvironment, context, addons);
    }
}
