package io.github.snaill.ast;

import java.util.List;

public class Parameter extends AbstractNode {
    public Parameter(Type type) {
        super(List.of(type));
    }

    public Type getType() {
        return (Type) getChild(0);
    }
}
