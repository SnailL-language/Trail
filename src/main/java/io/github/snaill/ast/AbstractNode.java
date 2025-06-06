package io.github.snaill.ast;

import io.github.snaill.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public abstract class AbstractNode implements Node {
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractNode.class);

    protected List<Node> children;
    protected int line = -1;
    protected int charPosition = -1;
    protected String source = null;
    protected transient boolean wasDeadCodeReported = false;
    protected Scope enclosingScope; // Added for all nodes

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

    @Override
    public void setSourceInfo(int line, int charPosition, String source) {
        this.line = line;
        this.charPosition = charPosition;
        this.source = source;
    }

    @Override
    public int getLine() { return line; }
    @Override
    public int getCharPosition() { return charPosition; }
    @Override
    public String getSource() { return source; }

    @Override
    public String getSourceInfo() {
        if (source == null && line == -1 && charPosition == -1) {
            return "<unknown source info>";
        }
        return String.format("line %d, char %d in %s", line, charPosition, source != null ? source : "<unknown file>");
    }

    public Scope getEnclosingScope() { // Added for all nodes
        return enclosingScope;
    }

    public void setEnclosingScope(Scope enclosingScope) {
        this.enclosingScope = enclosingScope;
        if (this instanceof Identifier && enclosingScope == null) {
            LOGGER.warn("SCOPE_SET_NULL_FOR_IDENTIFIER: Node type: {}, ID: {}, Source: {}",
                    this.getClass().getSimpleName(),
                    ((Identifier)this).getName(),
                    this.getSourceInfo());
        } else if (this instanceof Identifier) {
             LOGGER.trace("SCOPE_SET_FOR_IDENTIFIER: Node type: {}, ID: {}, Scope Hash: {}, Source: {}",
                    this.getClass().getSimpleName(),
                    ((Identifier)this).getName(),
                    System.identityHashCode(enclosingScope),
                    this.getSourceInfo());
        }
    }

    @Override
    public void check() throws io.github.snaill.exception.FailedCheckException {
        // Только Scope отвечает за печать UNUSED
        List<io.github.snaill.result.Result> results = checkDeadCode();
        results.stream().filter(r -> r instanceof io.github.snaill.result.CompilationError).forEach(System.err::println);
        // Рекурсивно вызываем check() у всех детей
        for (Node child : children) {
            child.check();
        }
    }

    public Node getParentNode() {
        return null; // По умолчанию, переопределяется в Scope и других нужных местах
    }
}
