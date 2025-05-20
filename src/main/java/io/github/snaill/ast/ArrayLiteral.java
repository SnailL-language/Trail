package io.github.snaill.ast;

import java.util.HashSet;
import java.util.List;

public class ArrayLiteral extends PrimaryExpression {
    private final List<Expression> elements;

    public ArrayLiteral(List<Expression> elements) {
        this.elements = elements;
    }

    public List<Expression> getElements() {
        return elements;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ArrayLiteral other) {
            return new HashSet<>(elements).containsAll(other.elements)
                && new HashSet<>(other.elements).containsAll(elements);
        }
        return false;
    }
}
