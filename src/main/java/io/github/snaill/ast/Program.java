package io.github.snaill.ast;

import java.util.List;

public class Program extends AbstractNode {

    public Program(List<GlobalDeclaration> children) {
        super(List.copyOf(children));
    }
}
