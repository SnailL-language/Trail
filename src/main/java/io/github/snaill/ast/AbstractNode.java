package io.github.snaill.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    public boolean equals(Object obj) {
        if (obj instanceof AbstractNode other) {
            return children.containsAll(other.children) && other.children.containsAll(children);
        }
        return false;
    }
}
