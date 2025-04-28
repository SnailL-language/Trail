package io.github.snaill.ast;

import java.util.List;

public class Scope extends AbstractNode {
    public Scope(List<Node> children, Node parent) {
        super(children, parent);
    }
}
