package io.github.snaill.ast;

import java.util.List;

public class Parameter extends AbstractNode {
    public Parameter(ParameterList parent, Type type) {
        super(List.of(type), parent);
    }

    public Type getType() {
        return (Type) getChild(0);
    }
}
