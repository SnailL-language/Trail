package io.github.snaill.ast;

import java.util.List;

public class ForLoop extends AbstractNode implements Statement {
    protected ForLoop(VariableDeclaration declaration, Expression condition, Expression step, Scope body) {
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
}
