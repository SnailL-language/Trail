package io.github.snaill.ast;

public class NumberLiteral extends PrimaryExpression {
    private final long value;

    public NumberLiteral(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NumberLiteral other) {
            return value == other.value;
        }
        return false;
    }
}
