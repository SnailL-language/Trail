package io.github.snaill.ast;

import java.util.List;

public class ReturnStatement extends AbstractNode implements Statement {
    public ReturnStatement(Expression returnable) {
        super(List.of(returnable));
    }

    public Expression getReturnable() {
        return (Expression) getChild(0);
    }
}
