package me.itzisonn_.meazy_addon.runtime.environment;

import me.itzisonn_.meazy.runtime.value.VariableValue;
import me.itzisonn_.meazy.runtime.value.function.FunctionValue;
import me.itzisonn_.meazy_addon.parser.AddonModifiers;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.CallArgExpression;
import me.itzisonn_.meazy_addon.runtime.value.native_class.*;
import me.itzisonn_.meazy_addon.runtime.value.native_class.collections.CollectionClassValue;
import me.itzisonn_.meazy_addon.runtime.value.native_class.collections.ListClassValue;
import me.itzisonn_.meazy_addon.runtime.value.native_class.collections.MapClassValue;
import me.itzisonn_.meazy_addon.runtime.value.native_class.collections.SetClassValue;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.GlobalEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.Interpreter;
import me.itzisonn_.meazy.runtime.interpreter.InvalidArgumentException;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy_addon.runtime.value.native_class.file.FileClassValue;
import me.itzisonn_.meazy_addon.runtime.value.native_class.file.FileReaderClassValue;
import me.itzisonn_.meazy_addon.runtime.value.native_class.file.FileWriterClassValue;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.classes.ClassValue;
import me.itzisonn_.meazy.runtime.value.function.NativeFunctionValue;
import me.itzisonn_.meazy_addon.runtime.value.number.*;
import me.itzisonn_.meazy_addon.runtime.value.native_class.primitive.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GlobalEnvironmentImpl extends FunctionDeclarationEnvironmentImpl implements GlobalEnvironment {
    private final File parentFile;
    private final Set<GlobalEnvironment> relatedGlobalEnvironments;
    private final Set<ClassValue> classes;

    public GlobalEnvironmentImpl(File parentFile) {
        super(null, false);
        this.parentFile = parentFile;
        relatedGlobalEnvironments = new HashSet<>();
        classes = new HashSet<>();
        init();
    }



    @Override
    public File getParentFile() {
        return parentFile;
    }

    @Override
    public GlobalEnvironment getGlobalEnvironment() {
        return this;
    }

    @Override
    public void addRelatedGlobalEnvironment(GlobalEnvironment globalEnvironment) {
        relatedGlobalEnvironments.add(globalEnvironment);
    }

    @Override
    public Set<GlobalEnvironment> getRelatedGlobalEnvironments() {
        return new HashSet<>(relatedGlobalEnvironments);
    }



    @Override
    public VariableValue getVariable(String id) {
        VariableValue variableValue = super.getVariable(id);
        if (variableValue != null) return variableValue;

        for (GlobalEnvironment globalEnvironment : relatedGlobalEnvironments) {
            variableValue = globalEnvironment.getVariable(id);
            if (variableValue != null) return variableValue;
        }

        return null;
    }

    @Override
    public FunctionValue getFunction(String id, List<RuntimeValue<?>> args) {
        FunctionValue functionValue = super.getFunction(id, args);
        if (functionValue != null) return functionValue;

        for (GlobalEnvironment globalEnvironment : relatedGlobalEnvironments) {
            functionValue = globalEnvironment.getFunction(id, args);
            if (functionValue != null) return functionValue;
        }

        return null;
    }



    @Override
    public void declareClass(ClassValue value) {
        ClassValue classValue = getClass(value.getId());
        if (classValue != null) throw new InvalidSyntaxException("Class with id " + value.getId() + " already exists");
        classes.add(value);
    }

    @Override
    public Set<ClassValue> getClasses() {
        return new HashSet<>(classes);
    }

    @Override
    public ClassValue getClass(String id) {
        if (id == null) throw new NullPointerException("Id can't be null");

        for (ClassValue classValue : getClasses()) {
            if (classValue.getId().equals(id)) return classValue;
        }

        for (GlobalEnvironment globalEnvironment : relatedGlobalEnvironments) {
            ClassValue classValue = globalEnvironment.getClass(id);
            if (classValue != null) return classValue;
        }

        return null;
    }



    public void init() {
        declareClass(new AnyClassValue(this));
        declareClass(new BooleanClassValue(this));
        declareClass(new IntClassValue(this));
        declareClass(new LongClassValue(this));
        declareClass(new FloatClassValue(this));
        declareClass(new DoubleClassValue(this));
        declareClass(new CharClassValue(this));
        declareClass(new StringClassValue(this));

        declareClass(new CollectionClassValue(this));
        declareClass(new ListClassValue(this));
        declareClass(new SetClassValue(this));
        declareClass(new MapClassValue(this));

        declareClass(new FileClassValue(this));
        declareClass(new FileReaderClassValue(this));
        declareClass(new FileWriterClassValue(this));

        declareClass(new InputClassValue(this));
        declareClass(new MathClassValue(this));
        declareClass(new MeazyClassValue(this));
        declareClass(new RandomClassValue(this));


        declareFunction(new NativeFunctionValue("print", List.of(
                new CallArgExpression("value", new DataType("Any", true), true)),
                null, this, Set.of(AddonModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionArgs.getFirst().getFinalRuntimeValue();
                Interpreter.OUTPUT.append(value);
                System.out.print(value);
                return null;
            }
        });

        declareFunction(new NativeFunctionValue("println", List.of(
                new CallArgExpression("value", new DataType("Any", true), true)),
                null, this, Set.of(AddonModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionArgs.getFirst().getFinalRuntimeValue();
                Interpreter.OUTPUT.append(value).append("\n");
                System.out.println(value);
                return null;
            }
        });

        declareFunction(new NativeFunctionValue("range",
                List.of(new CallArgExpression("begin", new DataType("Int", false), true), new CallArgExpression("end", new DataType("Int", false), true)),
                new DataType("List", false), this, Set.of()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof IntValue beginValue)) throw new InvalidArgumentException("Begin must be int");
                if (!(functionArgs.get(1).getFinalRuntimeValue() instanceof IntValue endValue)) throw new InvalidArgumentException("End must be int");

                List<RuntimeValue<?>> list = range(beginValue.getValue(), endValue.getValue(), 1);
                return new ListClassValue(list);
            }
        });

        declareFunction(new NativeFunctionValue("range",
                List.of(new CallArgExpression("begin", new DataType("Int", false), true), new CallArgExpression("end", new DataType("Int", false), true), new CallArgExpression("step", new DataType("Int", false), true)),
                new DataType("List", false), this, Set.of()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof IntValue beginValue)) throw new InvalidArgumentException("Begin must be int");
                if (!(functionArgs.get(1).getFinalRuntimeValue() instanceof IntValue endValue)) throw new InvalidArgumentException("End must be int");
                if (!(functionArgs.get(2).getFinalRuntimeValue() instanceof IntValue stepValue)) throw new InvalidArgumentException("Step must be int");

                if (stepValue.getValue() <= 0) throw new InvalidArgumentException("Step must be positive int");

                List<RuntimeValue<?>> list = range(beginValue.getValue(),  endValue.getValue(), stepValue.getValue());
                return new ListClassValue(list);
            }
        });
    }

    private static List<RuntimeValue<?>> range(int begin, int end, int step) {
        List<RuntimeValue<?>> list = new ArrayList<>();

        if (begin < end) {
            for (int i = begin; i < end; i += step) {
                list.add(new IntValue(i));
            }
        }
        else {
            for (int i = begin; i > end; i -= step) {
                list.add(new IntValue(i));
            }
        }

        return list;
    }
}