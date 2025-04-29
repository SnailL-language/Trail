package io.github.snaill.ast;

import java.util.List;

public class UnaryExpression extends Expression {
    private final String operator;

    public UnaryExpression(String operator, Expression argument) {
        super(List.of(argument));
        this.operator = operator;
    }

    public Expression getArgument() {
        return (Expression) super.getChild(0);
    }

    public String getOperator() {
        return operator;
    }
}
