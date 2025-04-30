package io.github.snaill.ast;

import java.util.List;

public class Scope extends AbstractNode {
    public Scope(List<Statement> children) {
        super(children);
    }

    public List<Statement> getStatements() {
        return super.getChildren().stream().map(Statement.class::cast).toList();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Scope) {
            return super.equals(obj);
        }
        return false;
    }
}
