package io.github.snaill.ast;

import java.util.List;

/**
 * Базовый класс для первичных выражений в AST.
 */
public abstract class PrimaryExpression extends Expression {
    public PrimaryExpression(List<Node> children) {
        super(children);
    }
}
