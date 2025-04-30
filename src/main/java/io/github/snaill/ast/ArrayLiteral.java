package io.github.snaill.ast;

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
            return elements.containsAll(other.elements)
                && other.elements.containsAll(elements);
        }
        return false;
    }
}
