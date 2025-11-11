package me.itzisonn_.meazy_addon.runtime.environment;

import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.runtime.environment.FileEnvironment;
import me.itzisonn_.meazy.runtime.environment.GlobalEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.EvaluationException;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.VariableValue;
import me.itzisonn_.meazy.runtime.value.ClassValue;
import me.itzisonn_.meazy.runtime.value.FunctionValue;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileEnvironmentImpl extends FunctionDeclarationEnvironmentImpl implements FileEnvironment {
    private final File parentFile;
    private final Set<Class<?>> nativeClasses;
    private final Set<VariableValue> variables;
    private final Set<ClassValue> classes;

    public FileEnvironmentImpl(GlobalEnvironment parent, File parentFile) {
        super(parent, false);
        this.parentFile = parentFile;
        nativeClasses = new HashSet<>();
        variables = new HashSet<>();
        classes = new HashSet<>();
    }



    @Override
    public GlobalEnvironment getParent() {
        return (GlobalEnvironment) parent;
    }

    @Override
    public FileEnvironment getFileEnvironment() {
        return this;
    }

    @Override
    public File getParentFile() {
        return parentFile;
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
    public void declareVariable(VariableValue value) {
        if (value.isArgument()) {
            if (getVariable(value.getId()) != null) {
                throw new EvaluationException(Text.translatable("meazy_addon:runtime.variable.already_exists", value.getId()));
            }
        }
        else if (getVariableDeclarationEnvironment(value.getId()) != null) {
            throw new EvaluationException(Text.translatable("meazy_addon:runtime.variable.already_exists", value.getId()));
        }

        variables.add(value);
    }

    @Override
    public Set<VariableValue> getVariables() {
        return new HashSet<>(variables);
    }



    @Override
    public VariableValue getLocalVariable(String id) throws NullPointerException {
        return FileEnvironment.super.getVariable(id);
    }

    @Override
    public FunctionValue getLocalFunction(String id, List<RuntimeValue<?>> args) throws NullPointerException {
        return super.getFunction(id, args);
    }

    @Override
    public ClassValue getLocalClass(String id) throws NullPointerException {
        if (id == null) throw new NullPointerException("Id can't be null");

        for (ClassValue classValue : getClasses()) {
            if (classValue.getId().equals(id)) return classValue;
        }

        return null;
    }



    @Override
    public VariableValue getVariable(String id) {
        VariableValue variableValue = getLocalVariable(id);
        if (variableValue != null) return variableValue;

        for (FileEnvironment fileEnvironment : getParent().getFileEnvironments()) {
            if (fileEnvironment == this) continue;
            variableValue = fileEnvironment.getLocalVariable(id);
            if (variableValue != null) return variableValue;
        }

        return null;
    }

    @Override
    public FunctionValue getFunction(String id, List<RuntimeValue<?>> args) {
        FunctionValue functionValue = getLocalFunction(id, args);
        if (functionValue != null) return functionValue;

        for (FileEnvironment fileEnvironment : getParent().getFileEnvironments()) {
            if (fileEnvironment == this) continue;
            functionValue = fileEnvironment.getLocalFunction(id, args);
            if (functionValue != null) return functionValue;
        }

        return null;
    }



    @Override
    public void declareClass(ClassValue value) {
        for (ClassValue classValue : getClasses()) {
            if (classValue.getId().equals(value.getId())) {
                throw new EvaluationException(Text.translatable("meazy_addon:runtime.class.already_exists", value.getId()));
            }
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

        for (FileEnvironment fileEnvironment : getParent().getFileEnvironments()) {
            if (fileEnvironment == this) continue;
            classValue = fileEnvironment.getLocalClass(id);
            if (classValue != null) return classValue;
        }

        return null;
    }
}