package me.itzisonn_.meazy_addon.runtime.evaluation_function;

import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.expression.*;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.statement.*;

import java.lang.reflect.ParameterizedType;

/**
 * Addon evaluation functions registrar
 * @see Registries#EVALUATION_FUNCTIONS
 */
public final class AddonEvaluationFunctions {
    private static boolean hasRegistered = false;

    private AddonEvaluationFunctions() {}



    /**
     * Initializes {@link Registries#EVALUATION_FUNCTIONS} registry
     * <p>
     * <i>Don't use this method because it's called once at {@link Registries} initialization</i>
     *
     * @throws IllegalStateException If {@link Registries#EVALUATION_FUNCTIONS} registry has already been initialized
     */
    public static void REGISTER() {
        if (hasRegistered) throw new IllegalStateException("EvaluationFunctions have already been initialized");
        hasRegistered = true;

        register(new ProgramEvaluationFunction());
        register(new ImportStatementEvaluationFunction());
        register(new UsingStatementEvaluationFunction());
        register(new ClassDeclarationStatementEvaluationFunction());
        register(new FunctionDeclarationStatementEvaluationFunction());
        register(new VariableDeclarationStatementEvaluationFunction());
        register(new ConstructorDeclarationStatementEvaluationFunction());
        register(new BaseCallStatementEvaluationFunction());
        register(new IfStatementEvaluationFunction());
        register(new ForStatementEvaluationFunction());
        register(new WhileStatementEvaluationFunction());
        register(new ReturnStatementEvaluationFunction());
        register(new ContinueStatementEvaluationFunction());
        register(new BreakStatementEvaluationFunction());
        register(new AssignmentStatementEvaluationFunction());

        register(new ListCreationExpressionEvaluationFunction());
        register(new MapCreationExpressionEvaluationFunction());
        register(new NullCheckExpressionEvaluationFunction());
        register(new IsExpressionEvaluationFunction());
        register(new OperatorExpressionEvaluationFunction());
        register(new PostfixExpressionEvaluationFunction());
        register(new MemberExpressionEvaluationFunction());
        register(new CallExpressionEvaluationFunction());
        register(new IdentifierEvaluationFunction());
        register(new NullLiteralEvaluationFunction());
        register(new NumberLiteralEvaluationFunction());
        register(new StringLiteralEvaluationFunction());
        register(new BooleanLiteralEvaluationFunction());
        register(new ThisLiteralEvaluationFunction());
    }



    @SuppressWarnings("unchecked")
    private static <T extends Statement> void register(AbstractEvaluationFunction<T> evaluationFunction) {
        Registries.EVALUATION_FUNCTIONS.register(
                AddonMain.getIdentifier(evaluationFunction.getId()),
                (Class<T>) ((ParameterizedType) evaluationFunction.getClass().getGenericSuperclass()).getActualTypeArguments()[0],
                evaluationFunction);
    }
}