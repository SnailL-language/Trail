package io.github.snaill.ast;

import java.util.List;

public class ArrayLiteral extends PrimaryExpression {
    final private List<Expression> elements;

    public ArrayLiteral(List<Expression> elements) {
        this.elements = elements;
    }

    public List<Expression> getElements() {
        return elements;
    }
}
