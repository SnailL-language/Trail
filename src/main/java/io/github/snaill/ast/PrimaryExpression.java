package io.github.snaill.ast;

import java.util.List;

public abstract class PrimaryExpression extends Expression {
    public PrimaryExpression(List<Node> children, Node parent) {
        super(children, parent);
    }
}
