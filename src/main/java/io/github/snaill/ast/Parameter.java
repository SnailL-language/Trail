package io.github.snaill.ast;

import java.util.List;
import java.util.Objects;

public class Parameter extends AbstractNode {
    private final String name;

    public Parameter(String name, Type type) {
        super(List.of(type));
        this.name = name;
    }

    public Type getType() {
        return (Type) getChild(0);
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Parameter other) {
            return name.equals(other.name)
                && super.equals(other);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, getType());
    }
}
