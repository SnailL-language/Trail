package io.github.snaill.ast;

import java.util.Collection;

public interface Node {
    Node getChild(int index);

    Collection<Node> getChildren();

    void setChildren(Collection<Node> children);

    int getChildCount();

    void setChild(int index, Node child);
}
