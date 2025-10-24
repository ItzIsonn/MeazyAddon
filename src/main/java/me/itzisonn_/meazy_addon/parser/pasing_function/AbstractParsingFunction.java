package me.itzisonn_.meazy_addon.parser.pasing_function;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ParsingFunction;
import me.itzisonn_.meazy.parser.ast.Statement;

@Getter
public abstract class AbstractParsingFunction<T extends Statement> implements ParsingFunction<T> {
    private final String id;

    protected AbstractParsingFunction(String id) {
        this.id = id;
    }
}
