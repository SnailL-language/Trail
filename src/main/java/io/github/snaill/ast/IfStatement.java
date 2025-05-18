package io.github.snaill.ast;

import java.util.List;

public class IfStatement extends AbstractNode implements Statement {

    private final boolean hasElse;

    public IfStatement(Expression condition, Scope body, Scope elseBody) {
        super(elseBody == null ? List.of(condition, body) : List.of(condition, body, elseBody));
        this.hasElse = elseBody != null;
    }

    public Expression getCondition() {
        return (Expression) children.getFirst();
    }

    public Scope getBody() {
        return (Scope) children.get(1);
    }

    public Scope getElseBody() {
        return hasElse ? (Scope) children.get(2) : null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IfStatement) {
            return super.equals(obj);
        }
        return false;
    }
}
