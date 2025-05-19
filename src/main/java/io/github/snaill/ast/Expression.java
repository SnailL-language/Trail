package io.github.snaill.ast;

import java.util.List;

public abstract class Expression extends AbstractNode implements Statement {
    protected Expression(List<Expression> children) {
        super(children);
    }
}
