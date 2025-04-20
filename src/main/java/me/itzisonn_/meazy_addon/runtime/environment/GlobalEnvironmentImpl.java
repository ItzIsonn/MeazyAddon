package me.itzisonn_.meazy_addon.runtime.environment;

import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.runtime.value.VariableValue;
import me.itzisonn_.meazy.runtime.value.function.FunctionValue;
import me.itzisonn_.meazy.runtime.environment.GlobalEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.classes.ClassValue;
import me.itzisonn_.meazy_addon.runtime.value.native_class.primitive.*;

import java.io.*;
import java.util.*;

public class GlobalEnvironmentImpl extends FunctionDeclarationEnvironmentImpl implements GlobalEnvironment {
    private final File parentFile;
    private final Set<GlobalEnvironment> relatedGlobalEnvironments;
    private final Set<Class<?>> nativeClasses;
    private final Set<ClassValue> classes;

    public GlobalEnvironmentImpl(File parentFile) {
        super(null, false);
        this.parentFile = parentFile;
        relatedGlobalEnvironments = new HashSet<>();
        nativeClasses = new HashSet<>();
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
    public void addNativeClass(Class<?> nativeClass) {
        nativeClasses.add(nativeClass);
    }

    @Override
    public Set<Class<?>> getNativeClasses() {
        return new HashSet<>(nativeClasses);
    }

    @Override
    public VariableValue getLocalVariable(String id) throws NullPointerException {
        VariableValue variableValue = super.getVariable(id);
        if (variableValue != null) return variableValue;

        for (GlobalEnvironment globalEnvironment : getRelatedGlobalEnvironments()) {
            variableValue = globalEnvironment.getVariable(id);
            if (variableValue != null) return variableValue;
        }

        return null;
    }

    @Override
    public FunctionValue getLocalFunction(String id, List<RuntimeValue<?>> args) throws NullPointerException {
        FunctionValue functionValue = super.getFunction(id, args);
        if (functionValue != null) return functionValue;

        for (GlobalEnvironment globalEnvironment : getRelatedGlobalEnvironments()) {
            functionValue = globalEnvironment.getFunction(id, args);
            if (functionValue != null) return functionValue;
        }

        return null;
    }

    @Override
    public ClassValue getLocalClass(String id) throws NullPointerException {
        if (id == null) throw new NullPointerException("Id can't be null");

        for (ClassValue classValue : getClasses()) {
            if (classValue.getId().equals(id)) return classValue;
        }

        for (GlobalEnvironment globalEnvironment : getRelatedGlobalEnvironments()) {
            ClassValue classValue = globalEnvironment.getClass(id);
            if (classValue != null) return classValue;
        }

        return null;
    }


    @Override
    public VariableValue getVariable(String id) {
        VariableValue variableValue = getLocalVariable(id);
        if (variableValue != null) return variableValue;

        for (GlobalEnvironment globalEnvironment : Registries.NATIVE_RELATED_GLOBAL_ENVIRONMENTS) {
            variableValue = globalEnvironment.getLocalVariable(id);
            if (variableValue != null) return variableValue;
        }

        return null;
    }

    @Override
    public FunctionValue getFunction(String id, List<RuntimeValue<?>> args) {
        FunctionValue functionValue = getLocalFunction(id, args);
        if (functionValue != null) return functionValue;

        for (GlobalEnvironment globalEnvironment : Registries.NATIVE_RELATED_GLOBAL_ENVIRONMENTS) {
            functionValue = globalEnvironment.getLocalFunction(id, args);
            if (functionValue != null) return functionValue;
        }

        return null;
    }



    @Override
    public void declareClass(ClassValue value) {
        for (ClassValue classValue : getClasses()) {
            if (classValue.getId().equals(value.getId())) throw new InvalidSyntaxException("Class with id " + value.getId() + " already exists");
        }

        classes.add(value);
    }

    @Override
    public Set<ClassValue> getClasses() {
        return new HashSet<>(classes);
    }

    @Override
    public ClassValue getClass(String id) {
        ClassValue classValue = getLocalClass(id);
        if (classValue != null) return classValue;

        for (GlobalEnvironment globalEnvironment : Registries.NATIVE_RELATED_GLOBAL_ENVIRONMENTS) {
            classValue = globalEnvironment.getLocalClass(id);
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
    }
}