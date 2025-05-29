package io.github.snaill.ast;

import java.util.*;

import io.github.snaill.result.Result;

public abstract class AbstractNode implements Node {

    protected List<Node> children;
    protected int line = -1;
    protected int charPosition = -1;
    protected String source = null;
    protected transient boolean wasDeadCodeReported = false;

    protected AbstractNode(List<? extends Node> children) {
        this.children = new ArrayList<>(children);
    }

    @Override
    public Node getChild(int index) {
        return children.get(index);
    }

    @Override
    public List<Node> getChildren() {
        return children;
    }

    @Override
    public void setChildren(Collection<Node> children) {
        this.children = new ArrayList<>(children);
    }

    @Override
    public int getChildCount() {
        return children.size();
    }

    @Override
    public void setChild(int index, Node child) {
        this.children.set(index, child);
    }

    @Override
    public List<Result> checkDeadCode() {
        List<Result> results = new ArrayList<>();
        for (Node child : children) {
            if (child != null) {
                results.addAll(child.checkDeadCode());
            }
        }
        return results;
    }

    @Override
    public void checkUnusedFunctions(Set<FunctionDeclaration> unused) {
        for (Node child : children) {
            if (child != null) {
                child.checkUnusedFunctions(unused);
            }
        }
    }

    @Override
    public void checkUnusedVariables(Set<VariableDeclaration> unused) {
        for (Node child : children) {
            if (child != null) {
                child.checkUnusedVariables(unused);
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AbstractNode other) {
            return children.equals(other.children);
        }
        return false;
    }

    public void setSourceInfo(int line, int charPosition, String source) {
        this.line = line;
        this.charPosition = charPosition;
        this.source = source;
    }

    public int getLine() { return line; }
    public int getCharPosition() { return charPosition; }
    public String getSource() { return source; }

    @Override
    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context) throws java.io.IOException, io.github.snaill.exception.FailedCheckException {
        throw new UnsupportedOperationException("emitBytecode не реализован для " + getClass().getSimpleName());
    }

    @Override
    public void check() throws io.github.snaill.exception.FailedCheckException {
        // Только Scope отвечает за печать UNUSED
        List<io.github.snaill.result.Result> results = checkDeadCode();
        results.stream().filter(r -> r instanceof io.github.snaill.result.CompilationError).forEach(r -> System.err.println(r));
        // Рекурсивно вызываем check() у всех детей
        for (Node child : children) {
            if (child != null) child.check();
        }
    }

    public Scope getEnclosingScope() {
        Node p = this;
        while (p != null && !(p instanceof Scope)) p = p instanceof AbstractNode ? ((AbstractNode)p).getParentNode() : null;
        return (Scope)p;
    }

    public Node getParentNode() {
        return null; // По умолчанию, переопределяется в Scope и других нужных местах
    }
}
