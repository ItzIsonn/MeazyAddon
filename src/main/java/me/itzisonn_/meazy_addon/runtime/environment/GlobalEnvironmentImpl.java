package me.itzisonn_.meazy_addon.runtime.environment;

import lombok.Getter;
import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.FileEnvironment;
import me.itzisonn_.meazy.runtime.value.VariableValue;
import me.itzisonn_.meazy.runtime.environment.GlobalEnvironment;
import me.itzisonn_.meazy_addon.parser.ast.statement.VariableDeclarationStatement;
import me.itzisonn_.meazy_addon.runtime.value.native_class.primitive.*;

import java.util.*;

public class GlobalEnvironmentImpl extends EnvironmentImpl implements GlobalEnvironment {
    private final RuntimeContext context;
    private final Set<FileEnvironment> fileEnvironments;
    @Getter
    private final LinkedHashMap<VariableDeclarationStatement.VariableDeclarationInfo, Environment> variableQueue = new LinkedHashMap<>();

    public GlobalEnvironmentImpl(RuntimeContext context) {
        super(null, false);

        this.context = context;

        fileEnvironments = new HashSet<>();
        init();
    }



    @Override
    public GlobalEnvironment getGlobalEnvironment() {
        return this;
    }

    @Override
    public RuntimeContext getContext() {
        return context;
    }

    @Override
    public void addFileEnvironment(FileEnvironment fileEnvironment) {
        fileEnvironments.add(fileEnvironment);
    }

    @Override
    public Set<FileEnvironment> getFileEnvironments() {
        return new HashSet<>(fileEnvironments);
    }



    @Override
    public VariableValue getVariable(String id) {
        for (FileEnvironment fileEnvironment : fileEnvironments) {
            VariableValue variableValue = fileEnvironment.getLocalVariable(id);
            if (variableValue != null) return variableValue;
        }

        return null;
    }



    public void init() {
        FileEnvironmentImpl fileEnvironment = new FileEnvironmentImpl(this, null);

        fileEnvironment.declareClass(new AnyClassValue(fileEnvironment));
        fileEnvironment.declareClass(new BooleanClassValue(fileEnvironment));
        fileEnvironment.declareClass(new NumberClassValue(fileEnvironment));
        fileEnvironment.declareClass(new IntClassValue(fileEnvironment));
        fileEnvironment.declareClass(new LongClassValue(fileEnvironment));
        fileEnvironment.declareClass(new FloatClassValue(fileEnvironment));
        fileEnvironment.declareClass(new DoubleClassValue(fileEnvironment));
        fileEnvironment.declareClass(new CharClassValue(fileEnvironment));
        fileEnvironment.declareClass(new StringClassValue(fileEnvironment));

        addFileEnvironment(fileEnvironment);
    }
}