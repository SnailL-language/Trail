package io.github.snaill.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import io.github.snaill.result.Result;

public abstract class AbstractNode implements Node {

    protected List<Node> children;

    protected AbstractNode(List<? extends Node> children) {
        this.children = List.copyOf(children);
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
        return children.stream().flatMap(child -> child.checkDeadCode().stream()).toList();
    }

    @Override
    public void checkUnusedFunctions(Set<FunctionDeclaration> unused) {
        children.stream().forEach(child -> child.checkUnusedFunctions(unused));
    }

    @Override
    public void checkUnusedVariables(Set<VariableDeclaration> unused) {
        children.stream().forEach(child -> child.checkUnusedVariables(unused));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AbstractNode other) {
            return children.containsAll(other.children) && other.children.containsAll(children);
        }
        return false;
    }
}
