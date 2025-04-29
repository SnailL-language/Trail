package io.github.snaill.ast;

public class StringLiteral extends PrimaryExpression {
    private final String value;

    public StringLiteral(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
