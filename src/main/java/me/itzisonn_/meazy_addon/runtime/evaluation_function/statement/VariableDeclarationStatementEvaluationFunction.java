package me.itzisonn_.meazy_addon.runtime.evaluation_function.statement;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.runtime.environment.*;
import me.itzisonn_.meazy.runtime.interpreter.EvaluationException;
import me.itzisonn_.meazy.runtime.interpreter.Interpreter;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.VariableValue;
import me.itzisonn_.meazy_addon.parser.ast.statement.VariableDeclarationStatement;
import me.itzisonn_.meazy_addon.parser.modifier.AddonModifiers;
import me.itzisonn_.meazy_addon.runtime.environment.FileEnvironmentImpl;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.AbstractEvaluationFunction;
import me.itzisonn_.meazy_addon.runtime.value.impl.VariableValueImpl;

import java.util.HashSet;
import java.util.Set;

public class VariableDeclarationStatementEvaluationFunction extends AbstractEvaluationFunction<VariableDeclarationStatement> {
    public VariableDeclarationStatementEvaluationFunction() {
        super("variable_declaration_statement");
    }

    @Override
    public RuntimeValue<?> evaluate(VariableDeclarationStatement variableDeclarationStatement, RuntimeContext context, Environment environment, Object... extra) {
        if (!(environment instanceof VariableDeclarationEnvironment variableDeclarationEnvironment)) {
            throw new EvaluationException(Text.translatable("meazy_addon:runtime.cant_use_statement", "variable_declaration"));
        }

        for (Modifier modifier : variableDeclarationStatement.getModifiers()) {
            if (!modifier.canUse(variableDeclarationStatement, context, environment)) {
                throw new EvaluationException(Text.translatable("meazy_addon:runtime.cant_use_modifier", modifier.getId()));
            }
        }

        Set<Modifier> modifiers = new HashSet<>(variableDeclarationStatement.getModifiers());
        if (!(environment instanceof ClassEnvironment) && environment.isShared() &&
                !variableDeclarationStatement.getModifiers().contains(AddonModifiers.SHARED())) modifiers.add(AddonModifiers.SHARED());

        Interpreter interpreter = context.getInterpreter();
        for (VariableDeclarationStatement.VariableDeclarationInfo variableDeclarationInfo : variableDeclarationStatement.getDeclarationInfos()) {
            RuntimeValue<?> value = null;
            VariableValue variableValue = null;

            if (variableDeclarationInfo.getValue() != null && !(environment instanceof ClassEnvironment && environment.isShared() &&
                    !variableDeclarationStatement.getModifiers().contains(AddonModifiers.SHARED()))) {
                boolean placed = false;

                if ((environment instanceof FileEnvironment || environment instanceof ClassEnvironment) && environment.isShared()) {
                    if (environment.getFileEnvironment() instanceof FileEnvironmentImpl) {
                        variableValue = new VariableValueImpl(
                                variableDeclarationInfo.getId(),
                                variableDeclarationInfo.getDataType(),
                                variableDeclarationInfo.getValue(),
                                variableDeclarationStatement.isConstant(),
                                modifiers,
                                false,
                                variableDeclarationEnvironment
                        );
                        placed = true;
                    }
                }

                if (!placed) value = interpreter.evaluate(variableDeclarationInfo.getValue(), environment);
            }

            if (variableValue == null) {
                variableValue = new VariableValueImpl(
                        variableDeclarationInfo.getId(),
                        variableDeclarationInfo.getDataType(),
                        value,
                        variableDeclarationStatement.isConstant(),
                        modifiers,
                        false,
                        variableDeclarationEnvironment
                );
            }

            variableDeclarationEnvironment.declareVariable(variableValue);
        }

        return null;
    }
}
