package io.github.snaill.ast;

import java.util.List;

public class BinaryExpression extends Expression {
    private final String operator;

    public BinaryExpression(Expression left, String operator, Expression right) {
        super(List.of(left, right));
        this.operator = operator;
    }

    public Expression getLeft() {
        return (Expression) super.getChild(0);
    }

    public Expression getRight() {
        return (Expression) super.getChild(1);
    }

    public String getOperator() {
        return operator;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BinaryExpression other) {
            return operator.equals(other.operator)
                && super.equals(other);
        }
        return false;
    }
}
