package io.github.snaill.ast;

public class Identifier extends PrimaryExpression {
    final private String name;

    public Identifier(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
