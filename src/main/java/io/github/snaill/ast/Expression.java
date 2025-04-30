package io.github.snaill.ast;

import java.util.List;

public abstract class Expression extends AbstractNode {
    protected Expression(List<Expression> children) {
        super(children);
    }
}
