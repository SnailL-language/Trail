package io.github.snaill.ast;

import java.util.List;

public class GlobalDeclaration extends AbstractNode {
    public GlobalDeclaration(List<Node> children, Program parent) {
        super(children, parent);
    }
}
