package io.github.snaill.ast;

import java.util.List;

public class VariableDeclaration extends AbstractNode implements Statement {
    private final String name;

    public VariableDeclaration(String name, Type type, Expression value) {
        super(List.of(type, value));
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
