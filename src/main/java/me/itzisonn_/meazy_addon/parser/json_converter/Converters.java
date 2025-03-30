package me.itzisonn_.meazy_addon.parser.json_converter;

import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.json_converter.Converter;
import me.itzisonn_.meazy.parser.json_converter.ExpressionConverter;
import me.itzisonn_.meazy.parser.json_converter.StatementConverter;
import me.itzisonn_.meazy_addon.parser.json_converter.expression.*;
import me.itzisonn_.meazy_addon.parser.json_converter.expression.call_expression.ClassCallExpressionConverter;
import me.itzisonn_.meazy_addon.parser.json_converter.expression.call_expression.FunctionCallExpressionConverter;
import me.itzisonn_.meazy_addon.parser.json_converter.expression.collection_creation.ListCreationExpressionConverter;
import me.itzisonn_.meazy_addon.parser.json_converter.expression.collection_creation.MapCreationExpressionConverter;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy_addon.parser.json_converter.expression.identifier.ClassIdentifierConverter;
import me.itzisonn_.meazy_addon.parser.json_converter.expression.identifier.FunctionIdentifierConverter;
import me.itzisonn_.meazy_addon.parser.json_converter.expression.identifier.VariableIdentifierConverter;
import me.itzisonn_.meazy_addon.parser.json_converter.expression.literal.*;
import me.itzisonn_.meazy_addon.parser.json_converter.statement.*;

import java.lang.reflect.ParameterizedType;

/**
 * All basic Converters
 *
 * @see Registries#CONVERTERS
 */
public final class Converters {
    private static boolean isInit = false;

    private Converters() {}



    @SuppressWarnings("unchecked")
    private static <T extends Statement> void register(Converter<T> converter) {
        Registries.CONVERTERS.register(
                converter.getIdentifier(),
                (Class<T>) ((ParameterizedType) converter.getClass().getGenericSuperclass()).getActualTypeArguments()[0],
                converter);
    }

    /**
     * Initializes {@link Registries#CONVERTERS} registry
     * <p>
     * <i>Don't use this method because it's called once at {@link Registries} initialization</i>
     *
     * @throws IllegalStateException If {@link Registries#CONVERTERS} registry has already been initialized
     */
    public static void INIT() {
        if (isInit) throw new IllegalStateException("Converters have already been initialized!");
        isInit = true;

        register(new ProgramConverter());
        register(new ClassDeclarationStatementConverter());
        register(new FunctionDeclarationStatementConverter());
        register(new VariableDeclarationConverter());
        register(new ConstructorDeclarationStatementConverter());
        register(new BaseCallStatementConverter());
        register(new IfStatementConverter());
        register(new ForStatementConverter());
        register(new ForeachStatementConverter());
        register(new WhileStatementConverter());
        register(new ReturnStatementConverter());
        register(new ContinueStatementConverter());
        register(new BreakStatementConverter());

        register(new AssignmentExpressionConverter());
        register(new ListCreationExpressionConverter());
        register(new MapCreationExpressionConverter());
        register(new NullCheckExpressionConverter());
        register(new IsExpressionConverter());
        register(new OperatorExpressionConverter());
        register(new ClassCallExpressionConverter());
        register(new MemberExpressionConverter());
        register(new FunctionCallExpressionConverter());
        register(new CallArgExpressionConverter());
        register(new ClassIdentifierConverter());
        register(new FunctionIdentifierConverter());
        register(new VariableIdentifierConverter());

        register(new NullLiteralConverter());
        register(new NumberLiteralConverter());
        register(new StringLiteralConverter());
        register(new BooleanLiteralConverter());
        register(new ThisLiteralConverter());

        register(new StatementConverter());
        register(new ExpressionConverter());

        Registries.updateGson();
    }
}
