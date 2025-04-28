package io.github.snaill.ast;

import java.util.List;

public class LiteralPrimaryExpression extends PrimaryExpression {
    public LiteralPrimaryExpression(Literal literal, PrimaryExpression parent) {
        super(List.of(literal), parent);
    }
}
