package me.itzisonn_.meazy_addon.parser.ast.expression.identifier;

import lombok.Getter;

@Getter
public class FunctionIdentifier extends Identifier {
    public FunctionIdentifier(String id) {
        super(id);
    }

    @Override
    public String toCodeString() {
        return id;
    }
}