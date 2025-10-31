package me.itzisonn_.meazy_addon.runtime.environment;

import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.FunctionDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.value.FunctionValue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class FunctionDeclarationEnvironmentImpl extends EnvironmentImpl implements FunctionDeclarationEnvironment {
    protected final Set<FunctionValue> functions;

    public FunctionDeclarationEnvironmentImpl(Environment parent, boolean isShared) {
        super(parent, isShared);
        functions = new HashSet<>();
    }

    @Override
    public void declareFunction(FunctionValue value) {
        List<ParameterExpression> parameters = value.getParameters();

        main:
        for (FunctionValue functionValue : functions) {
            if (functionValue.getId().equals(value.getId())) {
                List<ParameterExpression> otherParameters = functionValue.getParameters();
                if (parameters.size() != otherParameters.size()) continue;

                for (int i = 0; i < parameters.size(); i++) {
                    if (!otherParameters.get(i).getDataType().equals(parameters.get(i).getDataType())) continue main;
                }

                throw new RuntimeException("Function with id " + value.getId() + " already exists");
            }
        }

        functions.add(value);
    }

    @Override
    public Set<FunctionValue> getFunctions() {
        return new HashSet<>(functions);
    }
}