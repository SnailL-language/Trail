package io.github.snaill.ast;

import java.util.List;

public abstract class Statement extends AbstractNode {
    public Statement(List<Node> children, Node parent) {
        super(children, parent);
    }
}
