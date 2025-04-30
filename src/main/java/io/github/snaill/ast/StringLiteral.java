package io.github.snaill.ast;

public class StringLiteral extends PrimaryExpression {
    private final String value;

    public StringLiteral(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StringLiteral other) {
            return value.equals(other.value);
        }
        return false;
    }
}
