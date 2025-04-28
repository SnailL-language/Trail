package io.github.snaill.ast;

import java.util.List;

public class Scope extends AbstractNode {
    public Scope(List<Statement> children, Node parent) {
        super(List.copyOf(children), parent);
    }
}
