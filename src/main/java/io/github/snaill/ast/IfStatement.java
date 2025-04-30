package io.github.snaill.ast;

import java.util.List;

public class IfStatement extends AbstractNode implements Statement {
    public IfStatement(Expression condition, Scope body) {
        super(List.of(condition, body));
    }

    public Expression getCondition() {
        return (Expression) children.getFirst();
    }

    public Scope getBody() {
        return (Scope) children.get(1);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IfStatement) {
            return super.equals(obj);
        }
        return false;
    }
}
