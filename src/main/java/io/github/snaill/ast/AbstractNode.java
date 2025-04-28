package io.github.snaill.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractNode implements Node {
    List<Node> children;
    Node parent;

    public AbstractNode(List<Node> children, Node parent) {
        this.children = children;
        this.parent = parent;
    }

    @Override
    public Node getParent() {
        return parent;
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
    public int getChildCount() {
        return children.size();
    }

    @Override
    public void setParent(Node parent) {
        this.parent = parent;
    }

    @Override
    public void setChild(int index, Node child) {
        this.children.set(index, child);
    }

    @Override
    public void setChildren(Collection<Node> children) {
        this.children = new ArrayList<>(children);
    }
}
