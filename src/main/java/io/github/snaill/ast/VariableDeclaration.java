package io.github.snaill.ast;

import java.util.List;
import java.util.Set;

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

    @Override
    public void checkUnusedVariables(Set<VariableDeclaration> unused) {
        unused.add(this);
        super.checkUnusedVariables(unused);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof VariableDeclaration other) {
            return name.equals(other.name)
                && super.equals(other);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
