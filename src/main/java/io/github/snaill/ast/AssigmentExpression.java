package io.github.snaill.ast;

import java.util.List;

public class AssigmentExpression extends Expression implements Statement {
    private final String variableName;
    private final String operator;

    public AssigmentExpression(String variableName, String operator, Expression expression) {
        super(List.of(expression));
        this.variableName = variableName;
        this.operator = operator;
    }

    public String getVariableName() {
        return variableName;
    }

    public String getOperator() {
        return operator;
    }

    public Expression getExpression() {
        return (Expression) super.getChild(0);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AssigmentExpression other) {
            return variableName.equals(other.variableName)
                && operator.equals(other.operator)
                && super.equals(other);
        }
        return false;
    }
}
