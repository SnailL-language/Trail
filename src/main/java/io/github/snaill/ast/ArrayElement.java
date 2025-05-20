package io.github.snaill.ast;

import java.util.List;

public class ArrayElement extends Expression {
    private final Expression identifier;
    private final List<Expression> dims;

    public ArrayElement(Expression identifier, List<Expression> dims) {
        super(List.copyOf(dims));
        this.identifier = identifier;
        this.dims = dims;
    }

    public Expression getIdentifier() {
        return identifier;
    }

    public List<Expression> getDims() {
        return dims;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ArrayElement other) {
            return identifier.equals(other.identifier) &&
                    dims.equals(other.dims);
        }
        return false;
    }
}