package me.itzisonn_.meazy_addon.parser.pasing_function.expression;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.UnexpectedTokenException;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.parser.data_type.DataType;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.data_type.DataTypeImpl;
import me.itzisonn_.meazy_addon.parser.pasing_function.AbstractParsingFunction;
import me.itzisonn_.meazy_addon.parser.pasing_function.ParsingHelper;

public class FunctionArgExpressionParsingFunction extends AbstractParsingFunction<CallArgExpression> {
    public FunctionArgExpressionParsingFunction() {
        super("function_arg");
    }

    @Override
    public CallArgExpression parse(ParsingContext context, Object... extra) {
        Parser parser = context.getParser();

        if (!parser.getCurrent().getType().equals(AddonTokenTypes.VARIABLE()))
            throw new UnexpectedTokenException("Expected variable keyword at the beginning of function arg", parser.getCurrent().getLine());
        boolean isConstant = parser.getCurrentAndNext().getValue().equals("val");
        String id = parser.getCurrentAndNext(AddonTokenTypes.ID(), "Expected identifier after variable keyword in function arg").getValue();

        DataType dataType = ParsingHelper.parseDataType(context);
        return new CallArgExpression(id, dataType == null ? new DataTypeImpl("Any", true) : dataType, isConstant);
    }
}
