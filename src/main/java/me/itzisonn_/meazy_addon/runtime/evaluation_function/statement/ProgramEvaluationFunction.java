package me.itzisonn_.meazy_addon.runtime.evaluation_function.statement;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.parser.ast.Program;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.interpreter.Interpreter;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.AbstractEvaluationFunction;

public class ProgramEvaluationFunction extends AbstractEvaluationFunction<Program> {
    public ProgramEvaluationFunction() {
        super("program");
    }

    @Override
    public RuntimeValue<?> evaluate(Program program, RuntimeContext context, Environment environment, Object... extra) {
        Interpreter interpreter = context.getInterpreter();

        for (Statement statement : program.getBody()) {
            interpreter.evaluate(statement, environment);
        }

        return null;
    }
}
