package io.github.snaill.ast;

public class Literal extends PrimaryExpression {
    private final Object value;

    public Literal(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}
