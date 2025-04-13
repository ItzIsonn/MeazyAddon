package me.itzisonn_.meazy_addon.runtime.value.native_class;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.addon.Addon;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.value.classes.NativeClassValue;
import me.itzisonn_.meazy_addon.parser.AddonModifiers;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.runtime.environment.ClassDeclarationEnvironment;
import me.itzisonn_.meazy_addon.runtime.environment.ClassEnvironmentImpl;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy_addon.runtime.value.native_class.collections.ListClassValue;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.VariableValue;
import me.itzisonn_.meazy.runtime.value.classes.constructor.NativeConstructorValue;
import me.itzisonn_.meazy.runtime.value.function.NativeFunctionValue;
import me.itzisonn_.meazy_addon.runtime.value.native_class.primitive.StringClassValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MeazyClassValue extends NativeClassValue {
    public MeazyClassValue(ClassDeclarationEnvironment parent) {
        super(new ClassEnvironmentImpl(parent, false, "Meazy"));
        setupEnvironment(getEnvironment());
    }

    @Override
    public void setupEnvironment(ClassEnvironment classEnvironment) {
        classEnvironment.declareVariable(new VariableValue(
                "VERSION",
                new DataType("String", false),
                new StringClassValue(MeazyMain.VERSION.toString()),
                true,
                Set.of(AddonModifiers.SHARED()),
                false,
                classEnvironment));


        classEnvironment.declareConstructor(new NativeConstructorValue(List.of(), classEnvironment, Set.of(AddonModifiers.PRIVATE())) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {}
        });


        classEnvironment.declareFunction(new NativeFunctionValue("getAddons", List.of(), new DataType("List", false), classEnvironment, Set.of(AddonModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                List<RuntimeValue<?>> addons = new ArrayList<>();
                for (Addon addon : MeazyMain.ADDON_MANAGER.getAddons()) {
                    addons.add(new StringClassValue(addon.getAddonInfo().getId()));
                }

                return new ListClassValue(addons);
            }
        });
    }
}
