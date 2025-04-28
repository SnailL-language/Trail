package io.github.snaill.ast;

import java.util.List;

public class FunctionDeclarationStatement extends Statement {
    public FunctionDeclarationStatement(FunctionDeclaration functionDeclaration, Node parent) {
        super(List.of(functionDeclaration), parent);
    }

    public FunctionDeclaration getFunctionDeclaration() {
        return (FunctionDeclaration) getChild(0);
    }
}
