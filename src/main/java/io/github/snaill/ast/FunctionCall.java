package io.github.snaill.ast;

import java.util.List;

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
}
