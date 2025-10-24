package me.itzisonn_.meazy_addon.runtime.evaluation_function.statement;

import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.LoopEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.Interpreter;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.parser.ast.statement.*;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.AbstractEvaluationFunction;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.EvaluationHelper;
import me.itzisonn_.meazy_addon.runtime.value.statement_info.BreakInfoValue;
import me.itzisonn_.meazy_addon.runtime.value.statement_info.ContinueInfoValue;
import me.itzisonn_.meazy_addon.runtime.value.statement_info.ReturnInfoValue;

public class WhileStatementEvaluationFunction extends AbstractEvaluationFunction<WhileStatement> {
    public WhileStatementEvaluationFunction() {
        super("while_statement");
    }

    @Override
    public RuntimeValue<?> evaluate(WhileStatement whileStatement, RuntimeContext context, Environment environment, Object... extra) {
        LoopEnvironment whileEnvironment = Registries.LOOP_ENVIRONMENT_FACTORY.getEntry().getValue().create(environment);
        Interpreter interpreter = context.getInterpreter();

        main:
        while (EvaluationHelper.parseCondition(context, whileStatement.getCondition(), environment)) {
            whileEnvironment.clearVariables();

            for (int i = 0; i < whileStatement.getBody().size(); i++) {
                Statement statement = whileStatement.getBody().get(i);
                RuntimeValue<?> result = interpreter.evaluate(statement, whileEnvironment);

                if (statement instanceof ReturnStatement) {
                    if (i + 1 < whileStatement.getBody().size()) throw new InvalidSyntaxException("Return statement must be last in body");
                    return new ReturnInfoValue(result);
                }
                if (result instanceof ReturnInfoValue returnInfoValue) {
                    return returnInfoValue;
                }

                if (statement instanceof ContinueStatement) {
                    if (i + 1 < whileStatement.getBody().size()) throw new InvalidSyntaxException("Continue statement must be last in body");
                    break;
                }
                if (result instanceof ContinueInfoValue) {
                    break;
                }

                if (statement instanceof BreakStatement) {
                    if (i + 1 < whileStatement.getBody().size()) throw new InvalidSyntaxException("Break statement must be last in body");
                    break main;
                }
                if (result instanceof BreakInfoValue) {
                    break main;
                }
            }
        }

        return null;
    }
}
