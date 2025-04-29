package io.github.snaill.ast;

public class PrimitiveType extends Type {
    private final String name;

    protected PrimitiveType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
