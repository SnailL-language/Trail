package io.github.snaill.ast;

public class BooleanLiteral extends PrimaryExpression {

    private final boolean value;

    public BooleanLiteral(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }
}
