package me.itzisonn_.meazy_addon.runtime.native_class;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.addon.Addon;
import me.itzisonn_.meazy.addon.AddonInfo;
import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.native_annotation.Function;
import me.itzisonn_.meazy.runtime.native_annotation.NativeContainer;
import me.itzisonn_.meazy.runtime.environment.FileEnvironment;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.ClassValue;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.EvaluationHelper;
import me.itzisonn_.meazy_addon.runtime.native_class.collection.ListClassNative;
import me.itzisonn_.meazy_addon.runtime.native_class.primitive.StringClassNative;

import java.util.ArrayList;
import java.util.List;

@NativeContainer("data/program/meazy/meazy.mea")
public final class MeazyClassNative {
    private MeazyClassNative() {}

    @Function
    public static ClassValue getVersion(FunctionEnvironment functionEnvironment) {
        return StringClassNative.newString(functionEnvironment.getFileEnvironment(), MeazyMain.VERSION.toString());
    }

    @Function
    public static ClassValue getAddons(RuntimeContext context, FunctionEnvironment functionEnvironment, Environment callEnvironment) {
        FileEnvironment fileEnvironment = functionEnvironment.getFileEnvironment();

        List<RuntimeValue<?>> addons = new ArrayList<>();
        for (Addon addon : MeazyMain.ADDON_MANAGER.getAddons()) {
            AddonInfo addonInfo = addon.getAddonInfo();
            ClassValue addonClassValue = EvaluationHelper.callUninitializedClassValue(context, functionEnvironment.getFileEnvironment().getClass("Addon"), callEnvironment);

            addonClassValue.getEnvironment().assignVariable("id", StringClassNative.newString(fileEnvironment, addonInfo.getId()));
            addonClassValue.getEnvironment().assignVariable("version", StringClassNative.newString(fileEnvironment, addonInfo.getVersion().toString()));
            addonClassValue.getEnvironment().assignVariable("description", StringClassNative.newString(fileEnvironment, addonInfo.getDescription()));

            List<RuntimeValue<?>> authors = new ArrayList<>();
            for (String author : addonInfo.getAuthors()) {
                authors.add(StringClassNative.newString(fileEnvironment, author));
            }
            addonClassValue.getEnvironment().assignVariable("authors", ListClassNative.newList(functionEnvironment, context, authors));

            addons.add(addonClassValue);
        }

        return ListClassNative.newList(functionEnvironment, context, addons);
    }
}
