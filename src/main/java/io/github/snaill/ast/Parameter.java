package io.github.snaill.ast;

import java.util.List;

public class Parameter extends AbstractNode {
    public Parameter(Type type, ParameterList parent) {
        super(List.of(type), parent);
    }

    public Type getType() {
        return (Type) getChild(0);
    }
}
