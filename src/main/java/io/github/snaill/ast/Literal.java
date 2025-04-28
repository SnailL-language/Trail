package io.github.snaill.ast;

import java.util.List;

public class Literal extends AbstractNode {
    private final Object value;

    public Literal(Object value, LiteralPrimaryExpression parent) {
        super(List.of(), parent);
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}
