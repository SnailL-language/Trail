package io.github.snaill.ast;

import java.util.List;

public class BreakStatement extends AbstractNode implements Statement {
    protected BreakStatement() {
        super(List.of());
    }
}
