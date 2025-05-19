package io.github.snaill.ast;

import java.util.Set;

public class Identifier extends PrimaryExpression {
    final private String name;

    public Identifier(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void checkUnusedVariables(Set<VariableDeclaration> unused) {
        unused.removeAll(
            unused.stream()
            .filter(v -> v.getName().equals(name))
            .toList()
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Identifier other) {
            return name.equals(other.name);
        }
        return false;
    }
}
