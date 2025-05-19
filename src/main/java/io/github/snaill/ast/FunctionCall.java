package io.github.snaill.ast;

import java.util.List;
import java.util.Set;

public class FunctionCall extends Expression {
    String name;

    public FunctionCall(String functionName, List<Expression> arguments) {
        super(arguments);
        this.name = functionName;
    }

    public List<Expression> getArguments() {
        return super.getChildren().stream().map(Expression.class::cast).toList();
    }

    public String getName() {
        return name;
    }

    @Override
    public void checkUnusedFunctions(Set<FunctionDeclaration> unused) {
        unused.removeAll(unused.stream()
        .filter(fn -> fn.getName().equals(name) && fn.getParameterList().size() == getArguments().size())
        .toList());
        super.checkUnusedFunctions(unused);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FunctionCall other) {
            return name.equals(other.name)
                && super.equals(other);
        }
        return false;
    }
}
