package io.github.snaill.ast;

import java.util.List;

public class ForLoop extends AbstractNode implements Statement {
    public ForLoop(VariableDeclaration declaration, Expression condition, Expression step, Scope body) {
        super(List.of(declaration, condition, step, body));
    }

    public VariableDeclaration getDeclaration() {
        return (VariableDeclaration) children.getFirst();
    }

    public Expression getCondition() {
        return (Expression) children.get(1);
    }

    public Expression getStep() {
        return (Expression) children.get(2);
    }

    public Scope getBody() {
        return (Scope) children.get(3);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ForLoop) {
            return super.equals(obj);
        }
        return false;
    }
}
