package io.github.snaill.ast;

public class NumberLiteral extends PrimaryExpression {
    private final long value;

    public NumberLiteral(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }
}
