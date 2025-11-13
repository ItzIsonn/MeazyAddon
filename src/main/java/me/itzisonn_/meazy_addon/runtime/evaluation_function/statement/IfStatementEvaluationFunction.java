package me.itzisonn_.meazy_addon.runtime.evaluation_function.statement;

import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.interpreter.Interpreter;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.parser.ast.statement.*;
import me.itzisonn_.meazy_addon.runtime.EvaluationException;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.AbstractEvaluationFunction;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.EvaluationHelper;
import me.itzisonn_.meazy_addon.runtime.value.statement_info.BreakInfoValue;
import me.itzisonn_.meazy_addon.runtime.value.statement_info.ContinueInfoValue;
import me.itzisonn_.meazy_addon.runtime.value.statement_info.ReturnInfoValue;

public class IfStatementEvaluationFunction extends AbstractEvaluationFunction<IfStatement> {
    public IfStatementEvaluationFunction() {
        super("if_statement");
    }

    @Override
    public RuntimeValue<?> evaluate(IfStatement ifStatement, RuntimeContext context, Environment environment, Object... extra) {
        Interpreter interpreter = context.getInterpreter();

        while (ifStatement != null) {
            if (ifStatement.getCondition() != null) {
                if (!EvaluationHelper.parseCondition(context, ifStatement.getCondition(), environment)) {
                    ifStatement = ifStatement.getElseStatement();
                    continue;
                }
            }

            Environment ifEnvironment = Registries.ENVIRONMENT_FACTORY.getEntry().getValue().create(environment);

            for (int i = 0; i < ifStatement.getBody().size(); i++) {
                Statement statement = ifStatement.getBody().get(i);
                RuntimeValue<?> result = interpreter.evaluate(statement, ifEnvironment);

                if (statement instanceof ReturnStatement) {
                    if (i + 1 < ifStatement.getBody().size()) throw new EvaluationException(Text.translatable("meazy_addon:runtime.statement_must_be_last", "return"));
                    return new ReturnInfoValue(result);
                }
                if (result instanceof ReturnInfoValue returnInfoValue) {
                    return returnInfoValue;
                }

                if (statement instanceof ContinueStatement) {
                    if (i + 1 < ifStatement.getBody().size()) throw new EvaluationException(Text.translatable("meazy_addon:runtime.statement_must_be_last", "continue"));
                    return new ContinueInfoValue();
                }
                if (result instanceof ContinueInfoValue continueInfoValue) {
                    return continueInfoValue;
                }

                if (statement instanceof BreakStatement) {
                    if (i + 1 < ifStatement.getBody().size()) throw new EvaluationException(Text.translatable("meazy_addon:runtime.statement_must_be_last", "break"));
                    return new BreakInfoValue();
                }
                if (result instanceof BreakInfoValue breakInfoValue) {
                    return breakInfoValue;
                }
            }
            break;
        }

        return null;
    }
}
