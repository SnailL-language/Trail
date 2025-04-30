package io.github.snaill.ast;

public class Identifier extends PrimaryExpression {
    final private String name;

    public Identifier(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Identifier other) {
            return name.equals(other.name);
        }
        return false;
    }
}
