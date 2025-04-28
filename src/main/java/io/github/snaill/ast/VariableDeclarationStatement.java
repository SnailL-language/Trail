package io.github.snaill.ast;

import java.util.List;

public class VariableDeclarationStatement extends Statement {
    public VariableDeclarationStatement(VariableDeclaration variableDeclaration, Node parent) {
        super(List.of(variableDeclaration), parent);
    }

    public VariableDeclaration getVariableDeclaration() {
        return (VariableDeclaration) getChild(0);
    }
}
