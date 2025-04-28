package io.github.snaill.ast;

import java.util.List;

public class VariableDeclaration extends AbstractNode {
    private final String name;

    public VariableDeclaration(VariableDeclarationStatement parent, String name, Type type, Expression value) {
        super(List.of(type, value), parent);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return (Type) children.getFirst();
    }

    public Expression getValue() {
        return (Expression) children.getLast();
    }
}
