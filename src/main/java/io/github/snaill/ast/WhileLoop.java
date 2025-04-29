package io.github.snaill.ast;

import java.util.List;

public class WhileLoop extends AbstractNode implements Statement {
    protected WhileLoop(Expression condition, Scope body) {
        super(List.of(condition, body));
    }

    public Expression getCondition() {
        return (Expression) children.getFirst();
    }

    public Scope getBody() {
        return (Scope) children.get(1);
    }
}
