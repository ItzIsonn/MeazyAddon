package me.itzisonn_.meazy_addon.runtime.evaluation_function.statement;

import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.LoopEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.Interpreter;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.VariableValue;
import me.itzisonn_.meazy_addon.parser.ast.statement.*;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.AbstractEvaluationFunction;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.EvaluationHelper;
import me.itzisonn_.meazy_addon.runtime.value.impl.VariableValueImpl;
import me.itzisonn_.meazy_addon.runtime.value.statement_info.BreakInfoValue;
import me.itzisonn_.meazy_addon.runtime.value.statement_info.ContinueInfoValue;
import me.itzisonn_.meazy_addon.runtime.value.statement_info.ReturnInfoValue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ForStatementEvaluationFunction extends AbstractEvaluationFunction<ForStatement> {
    public ForStatementEvaluationFunction() {
        super("for_statement");
    }

    @Override
    public RuntimeValue<?> evaluate(ForStatement forStatement, RuntimeContext context, Environment environment, Object... extra) {
        LoopEnvironment forEnvironment = Registries.LOOP_ENVIRONMENT_FACTORY.getEntry().getValue().create(environment);
        Interpreter interpreter = context.getInterpreter();

        forStatement.getVariableDeclarationStatement().getDeclarationInfos().forEach(variableDeclarationInfo ->
                forEnvironment.declareVariable(new VariableValueImpl(
                        variableDeclarationInfo.getId(),
                        variableDeclarationInfo.getDataType(),
                        variableDeclarationInfo.getValue() == null ?
                                null :
                                interpreter.evaluate(variableDeclarationInfo.getValue(), environment),
                        forStatement.getVariableDeclarationStatement().isConstant(),
                        Set.of(),
                        false,
                        forEnvironment
                ))
        );

        main:
        while (EvaluationHelper.parseCondition(context, forStatement.getCondition(), forEnvironment)) {
            for (int i = 0; i < forStatement.getBody().size(); i++) {
                Statement statement = forStatement.getBody().get(i);
                RuntimeValue<?> result = interpreter.evaluate(statement, forEnvironment);

                if (statement instanceof ReturnStatement) {
                    if (i + 1 < forStatement.getBody().size()) throw new RuntimeException("Return statement must be last in body");
                    return new ReturnInfoValue(result);
                }
                if (result instanceof ReturnInfoValue returnInfoValue) {
                    return returnInfoValue;
                }

                if (statement instanceof ContinueStatement) {
                    if (i + 1 < forStatement.getBody().size()) throw new RuntimeException("Continue statement must be last in body");
                    break;
                }
                if (result instanceof ContinueInfoValue) {
                    break;
                }

                if (statement instanceof BreakStatement) {
                    if (i + 1 < forStatement.getBody().size()) throw new RuntimeException("Break statement must be last in body");
                    break main;
                }
                if (result instanceof BreakInfoValue) {
                    break main;
                }
            }

            List<VariableValue> variableValues = new ArrayList<>();
            forStatement.getVariableDeclarationStatement().getDeclarationInfos().forEach(variableDeclarationInfo ->
                    variableValues.add(forEnvironment.getVariable(variableDeclarationInfo.getId())));

            forEnvironment.clearVariables();
            for (VariableValue variableValue : variableValues) {
                forEnvironment.declareVariable(new VariableValueImpl(
                        variableValue.getId(),
                        variableValue.getDataType(),
                        variableValue.getValue(),
                        variableValue.isConstant(),
                        new HashSet<>(),
                        false,
                        forEnvironment
                ));
            }
            EvaluationHelper.evaluateAssignmentExpression(context, forStatement.getAssignmentExpression(), forEnvironment);
        }

        return null;
    }
}
