package io.github.snaill.ast;

import java.util.List;

public abstract class Expression extends AbstractNode{
    public Expression(List<Node> children, Node parent) {
        super(children, parent);
    }
}
