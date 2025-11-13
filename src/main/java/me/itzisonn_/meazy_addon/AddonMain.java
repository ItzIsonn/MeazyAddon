package me.itzisonn_.meazy_addon;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.addon.Addon;
import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.logging.LogLevel;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.Program;
import me.itzisonn_.meazy.runtime.environment.*;
import me.itzisonn_.meazy.runtime.interpreter.Interpreter;
import me.itzisonn_.meazy.runtime.value.FunctionValue;
import me.itzisonn_.meazy.runtime.value.VariableValue;
import me.itzisonn_.meazy.runtime.value.ClassValue;
import me.itzisonn_.meazy.version.Version;
import me.itzisonn_.meazy_addon.parser.modifier.AddonModifiers;
import me.itzisonn_.meazy_addon.parser.operator.AddonOperators;
import me.itzisonn_.meazy_addon.parser.pasing_function.AddonParsingFunctions;
import me.itzisonn_.meazy_addon.parser.data_type.DataTypeFactoryImpl;
import me.itzisonn_.meazy_addon.parser.json_converter.AddonConverters;
import me.itzisonn_.meazy_addon.runtime.EvaluationException;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.AddonEvaluationFunctions;
import me.itzisonn_.meazy_addon.runtime.environment.factory.*;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.EvaluationHelper;
import me.itzisonn_.registry.RegistryIdentifier;

import java.util.*;

public class AddonMain extends Addon {
    public static final String NAMESPACE = "meazy_addon";

    @Override
    public void onEnable() {
        AddonModifiers.REGISTER();
        AddonOperators.REGISTER();
        AddonParsingFunctions.REGISTER();
        AddonEvaluationFunctions.REGISTER();
        AddonConverters.REGISTER();

        Registries.PARSE_TOKENS_FUNCTION.register(getIdentifier("parse_tokens"), (file, tokens) -> {
            if (tokens == null) throw new NullPointerException("Tokens can't be null");
            ParsingContext parsingContext = new ParsingContext(tokens);

            Parser parser = parsingContext.getParser();
            return parser.parse(getIdentifier("program"), Program.class, file);
        });

        Registries.EVALUATE_PROGRAM_FUNCTION.register(getIdentifier("evaluate_program"), (program, globalEnvironment) -> {
            for (String addonId : program.getRequiredAddons().keySet()) {
                Addon addon = MeazyMain.ADDON_MANAGER.getAddon(addonId);
                if (addon == null) throw new EvaluationException(Text.translatable("meazy_addon:addons.cant_find", addonId));

                Version addonVersion = program.getRequiredAddons().get(addonId);
                if (addonVersion != null && !addon.getAddonInfo().getVersion().equals(addonVersion)) {
                    throw new EvaluationException(Text.translatable("meazy_addon:addons.cant_find_version", addonId, addonVersion, addon.getAddonInfo().getVersion()));
                }
            }

            RuntimeContext context = globalEnvironment.getContext();
            Interpreter interpreter = context.getInterpreter();

            FileEnvironment fileEnvironment = Registries.FILE_ENVIRONMENT_FACTORY.getEntry().getValue().create(globalEnvironment, program.getFile());
            interpreter.evaluate(program, fileEnvironment);
            globalEnvironment.addFileEnvironment(fileEnvironment);

            return fileEnvironment;
        });

        Registries.RUN_PROGRAM_FUNCTION.register(getIdentifier("run_program"), program -> {
            RuntimeContext context = new RuntimeContext();

            GlobalEnvironment globalEnvironment = context.getGlobalEnvironment();
            FileEnvironment fileEnvironment = Registries.EVALUATE_PROGRAM_FUNCTION.getEntry().getValue().evaluate(program, globalEnvironment);

            for (ClassValue classValue : fileEnvironment.getClasses()) {
                if (EvaluationHelper.hasRepeatedBaseClasses(classValue.getBaseClasses(), new ArrayList<>(), fileEnvironment)) {
                    throw new EvaluationException(Text.translatable("meazy_addon:runtime.class.repeated.base_classes", classValue.getId()));
                }
                if (EvaluationHelper.hasRepeatedVariables(
                        classValue.getBaseClasses(),
                        new ArrayList<>(classValue.getEnvironment().getVariables().stream().map(VariableValue::getId).toList()),
                        fileEnvironment)) {
                    throw new EvaluationException(Text.translatable("meazy_addon:runtime.class.repeated.variables", classValue.getId()));
                }
            }

            FunctionValue function = fileEnvironment.getFunction("main", List.of());
            if (function == null) {
                MeazyMain.LOGGER.log(LogLevel.WARNING, Text.translatable("meazy_addon:runtime.file_doesnt_contain_main_function"));
                return fileEnvironment;
            }

            EvaluationHelper.callFunction(context, fileEnvironment, function, List.of());

            return fileEnvironment;
        });

        Registries.DATA_TYPE_FACTORY.register(getIdentifier("data_type_factory"), new DataTypeFactoryImpl());
        Registries.GLOBAL_ENVIRONMENT_FACTORY.register(getIdentifier("global_environment_factory"), new GlobalEnvironmentFactoryImpl());
        Registries.FILE_ENVIRONMENT_FACTORY.register(getIdentifier("file_environment_factory"), new FileEnvironmentFactoryImpl());
        Registries.CLASS_ENVIRONMENT_FACTORY.register(getIdentifier("class_environment_factory"), new ClassEnvironmentFactoryImpl());
        Registries.FUNCTION_ENVIRONMENT_FACTORY.register(getIdentifier("function_environment_factory"), new FunctionEnvironmentFactoryImpl());
        Registries.CONSTRUCTOR_ENVIRONMENT_FACTORY.register(getIdentifier("constructor_environment_factory"), new ConstructorEnvironmentFactoryImpl());
        Registries.LOOP_ENVIRONMENT_FACTORY.register(getIdentifier("loop_environment_factory"), new LoopEnvironmentFactoryImpl());
        Registries.ENVIRONMENT_FACTORY.register(getIdentifier("environment_factory"), new EnvironmentFactoryImpl());
    }

    public static RegistryIdentifier getIdentifier(String id) {
        return RegistryIdentifier.of(NAMESPACE, id);
    }
}