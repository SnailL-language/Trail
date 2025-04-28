package io.github.snaill.ast;

import java.util.List;

public class PrimaryExpression extends Expression {
    public PrimaryExpression(List<PrimaryExpression> children, Node parent) {
        super(List.copyOf(children), parent);
    }
}
