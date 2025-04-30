package io.github.snaill.ast;

import java.util.List;

public class ReturnStatement extends AbstractNode implements Statement {
    public ReturnStatement(Expression returnable) {
        super(returnable == null ? List.of() : List.of(returnable));
    }

    public Expression getReturnable() {
        return children.size() > 0 ? (Expression) getChild(0) : null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ReturnStatement) {
            return super.equals(obj);
        }
        return false;
    }
}
