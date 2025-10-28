package me.itzisonn_.meazy_addon.parser.pasing_function.expression;

import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.UnexpectedTokenException;
import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import me.itzisonn_.meazy.parser.data_type.DataType;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.pasing_function.AbstractParsingFunction;
import me.itzisonn_.meazy_addon.parser.pasing_function.ParsingHelper;

public class ParameterExpressionParsingFunction extends AbstractParsingFunction<ParameterExpression> {
    public ParameterExpressionParsingFunction() {
        super("parameter_expression");
    }

    @Override
    public ParameterExpression parse(ParsingContext context, Object... extra) {
        Parser parser = context.getParser();

        if (!parser.getCurrent().getType().equals(AddonTokenTypes.VARIABLE()))
            throw new UnexpectedTokenException("Expected variable keyword at the beginning of parameter expression", parser.getCurrent().getLine());
        boolean isConstant = parser.getCurrentAndNext().getValue().equals("val");
        String id = parser.getCurrentAndNext(AddonTokenTypes.ID(), "Expected identifier after variable keyword in parameter expression").getValue();

        DataType dataType = ParsingHelper.parseDataType(context);
        return new ParameterExpression(id, dataType == null ? Registries.DATA_TYPE_FACTORY.getEntry().getValue().create() : dataType, isConstant);
    }
}
