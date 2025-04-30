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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UnaryExpression other) {
            return operator.equals(other.operator)
                && super.equals(other);
        }
        return false;
    }
}
