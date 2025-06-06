package io.github.snaill.ast;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Представляет параметр функции в AST.
 */
public class Parameter extends AbstractNode {
    private final String name;
    private final Type type;

    public Parameter(String name, Type type) {
        super(List.of(type));
        this.name = name;
        this.type = type;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Parameter other) {
            return name.equals(other.name)
                && type.equals(other.type);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }
}
