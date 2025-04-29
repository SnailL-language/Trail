package io.github.snaill.ast;

import java.util.Collection;

public interface Node {
    Node getChild(int index);
    Collection<Node> getChildren();
    int getChildCount();
    void setChild(int index, Node child);
    void setChildren(Collection<Node> children);
}
