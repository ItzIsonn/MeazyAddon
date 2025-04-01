package me.itzisonn_.meazy_addon.runtime.environment.default_classes;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.addon.Addon;
import me.itzisonn_.meazy_addon.parser.AddonModifiers;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.runtime.environment.ClassDeclarationEnvironment;
import me.itzisonn_.meazy_addon.runtime.environment.ClassEnvironmentImpl;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy_addon.runtime.environment.default_classes.collections.ListClassEnvironment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.runtime.value.StringValue;
import me.itzisonn_.meazy.runtime.value.VariableValue;
import me.itzisonn_.meazy.runtime.value.classes.DefaultClassValue;
import me.itzisonn_.meazy.runtime.value.classes.constructors.DefaultConstructorValue;
import me.itzisonn_.meazy.runtime.value.function.DefaultFunctionValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MeazyClassEnvironment extends ClassEnvironmentImpl {
    public MeazyClassEnvironment(ClassDeclarationEnvironment parent) {
        super(parent, true, "Meazy");


        declareVariable(new VariableValue(
                "VERSION",
                new DataType("String", false),
                new StringValue(MeazyMain.VERSION),
                true,
                Set.of(AddonModifiers.SHARED()),
                false));


        declareConstructor(new DefaultConstructorValue(List.of(), this, Set.of(AddonModifiers.PRIVATE())) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {}
        });


        declareFunction(new DefaultFunctionValue("getAddons", List.of(), new DataType("List", false), this, Set.of(AddonModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                List<RuntimeValue<?>> addons = new ArrayList<>();
                for (Addon addon : MeazyMain.ADDON_MANAGER.getAddons()) {
                    addons.add(new StringValue(addon.getAddonInfo().getId()));
                }

                return new DefaultClassValue(new ListClassEnvironment(Registries.GLOBAL_ENVIRONMENT.getEntry().getValue(), addons));
            }
        });
    }
}
