package me.itzisonn_.meazy_addon.runtime.evaluation_function.statement;

import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.LoopEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.EvaluationException;
import me.itzisonn_.meazy.runtime.interpreter.Interpreter;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.VariableValue;
import me.itzisonn_.meazy.runtime.value.ClassValue;
import me.itzisonn_.meazy_addon.parser.ast.statement.*;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.AbstractEvaluationFunction;
import me.itzisonn_.meazy_addon.runtime.value.impl.VariableValueImpl;
import me.itzisonn_.meazy_addon.runtime.native_class.collection.InnerCollectionValue;
import me.itzisonn_.meazy_addon.runtime.value.statement_info.BreakInfoValue;
import me.itzisonn_.meazy_addon.runtime.value.statement_info.ContinueInfoValue;
import me.itzisonn_.meazy_addon.runtime.value.statement_info.ReturnInfoValue;

import java.util.HashSet;

public class ForeachStatementEvaluationFunction extends AbstractEvaluationFunction<ForeachStatement> {
    public ForeachStatementEvaluationFunction() {
        super("foreach_statement");
    }

    @Override
    public RuntimeValue<?> evaluate(ForeachStatement foreachStatement, RuntimeContext context, Environment environment, Object... extra) {
        LoopEnvironment foreachEnvironment = Registries.LOOP_ENVIRONMENT_FACTORY.getEntry().getValue().create(environment);
        Interpreter interpreter = context.getInterpreter();

        RuntimeValue<?> rawCollectionValue = interpreter.evaluate(foreachStatement.getCollection(), foreachEnvironment).getFinalRuntimeValue();
        if (!(rawCollectionValue instanceof ClassValue classValue && classValue.getBaseClasses().contains("Collection"))) {
            throw new EvaluationException(Text.translatable("meazy_addon:runtime.cant_apply_foreach"));
        }

        VariableValue variable = classValue.getEnvironment().getVariable("collection");
        if (variable == null) throw new EvaluationException(Text.translatable("meazy_addon:runtime.cant_apply_foreach"));
        if (!(variable.getValue() instanceof InnerCollectionValue<?> collectionValue)) throw new EvaluationException(Text.translatable("meazy_addon:runtime.cant_apply_foreach"));

        main:
        for (RuntimeValue<?> runtimeValue : collectionValue.getValue()) {
            foreachEnvironment.clearVariables();

            foreachEnvironment.declareVariable(new VariableValueImpl(
                    foreachStatement.getVariableDeclarationStatement().getDeclarationInfos().getFirst().getId(),
                    foreachStatement.getVariableDeclarationStatement().getDeclarationInfos().getFirst().getDataType(),
                    runtimeValue,
                    foreachStatement.getVariableDeclarationStatement().isConstant(),
                    new HashSet<>(),
                    false,
                    foreachEnvironment
            ));

            for (int i = 0; i < foreachStatement.getBody().size(); i++) {
                Statement statement = foreachStatement.getBody().get(i);
                RuntimeValue<?> result = interpreter.evaluate(statement, foreachEnvironment);

                if (statement instanceof ReturnStatement) {
                    if (i + 1 < foreachStatement.getBody().size()) throw new EvaluationException(Text.translatable("meazy_addon:runtime.statement_must_be_last", "return"));
                    return new ReturnInfoValue(result);
                }
                if (result instanceof ReturnInfoValue returnInfoValue) {
                    return returnInfoValue;
                }

                if (statement instanceof ContinueStatement) {
                    if (i + 1 < foreachStatement.getBody().size()) throw new EvaluationException(Text.translatable("meazy_addon:runtime.statement_must_be_last", "continue"));
                    break;
                }
                if (result instanceof ContinueInfoValue) {
                    break;
                }

                if (statement instanceof BreakStatement) {
                    if (i + 1 < foreachStatement.getBody().size()) throw new EvaluationException(Text.translatable("meazy_addon:runtime.statement_must_be_last", "break"));
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
