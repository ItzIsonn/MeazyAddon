package me.itzisonn_.meazy_addon.parser.pasing_function;

import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy_addon.parser.pasing_function.expression.*;
import me.itzisonn_.meazy_addon.parser.pasing_function.statement.*;

/**
 * Addon parsing functions registrar
 *
 * @see Registries#PARSING_FUNCTIONS
 */
public final class AddonParsingFunctions {
    private static boolean hasRegistered = false;

    private AddonParsingFunctions() {}



    /**
     * Initializes {@link Registries#PARSING_FUNCTIONS} registry
     * <p>
     * <i>Don't use this method because it's called once at {@link Registries} initialization</i>
     *
     * @throws IllegalStateException If {@link Registries#PARSING_FUNCTIONS} registry has already been initialized
     */
    public static void REGISTER() {
        if (hasRegistered) throw new IllegalStateException("ParsingFunctions have already been initialized");
        hasRegistered = true;

        register(new GlobalStatementParsingFunction());
        register(new ImportStatementParsingFunction());
        register(new UsingStatementParsingFunction());
        register(new ClassDeclarationStatementParsingFunction());
        register(new ClassBodyStatementParsingFunction());
        register(new FunctionDeclarationStatementParsingFunction());
        register(new ParameterExpressionParsingFunction());
        register(new VariableDeclarationStatementParsingFunction());
        register(new ConstructorDeclarationStatementParsingFunction());
        register(new BaseCallStatementParsingFunction());
        register(new StatementParsingFunction());
        register(new IfStatementParsingFunction());
        register(new ForStatementParsingFunction());
        register(new WhileStatementParsingFunction());
        register(new ReturnStatementParsingFunction());
        register(new ContinueStatementParsingFunction());
        register(new BreakStatementParsingFunction());

        register(new ExpressionParsingFunction());
        register(new AssignmentExpressionParsingFunction());
        register(new ListCreationExpressionParsingFunction());
        register(new MapCreationExpressionParsingFunction());
        register(new NullCheckExpressionParsingFunction());
        register(new LogicalExpressionParsingFunction());
        register(new ComparisonExpressionParsingFunction());
        register(new IsExpressionParsingFunction());
        register(new AdditionExpressionParsingFunction());
        register(new MultiplicationExpressionParsingFunction());
        register(new PowerExpressionParsingFunction());
        register(new InversionExpressionParsingFunction());
        register(new NegationExpressionParsingFunction());
        register(new PostfixExpressionParsingFunction());
        register(new MemberExpressionParsingFunction());
        register(new CallExpressionParsingFunction());
        register(new PrimaryExpressionParsingFunction());
    }

    private static void register(AbstractParsingFunction<? extends Statement> parsingFunction) {
        Registries.PARSING_FUNCTIONS.register(AddonMain.getIdentifier(parsingFunction.getId()), parsingFunction);
    }
}