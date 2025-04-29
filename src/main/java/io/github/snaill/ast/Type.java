package io.github.snaill.ast;

import java.util.List;

public class Type extends AbstractNode {
    private final String typeName;

    public Type(String typeName) {
        super(List.of());
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
}
