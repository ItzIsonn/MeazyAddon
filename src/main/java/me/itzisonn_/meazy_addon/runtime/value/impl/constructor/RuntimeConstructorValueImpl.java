package me.itzisonn_.meazy_addon.runtime.value.impl.constructor;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.ConstructorDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.value.constructor.RuntimeConstructorValue;

import java.util.List;
import java.util.Set;

/**
 * Implementation of {@link RuntimeConstructorValue}
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class RuntimeConstructorValueImpl extends ConstructorValueImpl implements RuntimeConstructorValue {
    private final List<Statement> body;

    /**
     * @param args Args
     * @param body = Body
     * @param parentEnvironment Parent environment
     * @param modifiers Modifiers
     *
     * @throws NullPointerException If either args, body, parentEnvironment or modifiers is null
     */
    public RuntimeConstructorValueImpl(List<CallArgExpression> args, List<Statement> body,
                                   ConstructorDeclarationEnvironment parentEnvironment, Set<Modifier> modifiers) throws NullPointerException {
        super(args, parentEnvironment, modifiers);

        if (body == null) throw new NullPointerException("Body can't be null");
        this.body = body;
    }
}
