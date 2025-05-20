package io.github.snaill.ast;

import java.util.List;

public class AssigmentExpression extends Expression {
    private final Expression left;
    private final String operator;
    private final Expression expression;

    public AssigmentExpression(Expression left, String operator, Expression expression) {
        super(expression == null ? List.of(left) : List.of(left, expression));
        this.left = left;
        this.operator = operator;
        this.expression = expression;
    }

    public Expression getLeft() {
        return left;
    }

    public String getOperator() {
        return operator;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AssigmentExpression other) {
            return left.equals(other.left) &&
                    operator.equals(other.operator) &&
                    expression.equals(other.expression);
        }
        return false;
    }
}