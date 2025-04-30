package io.github.snaill.ast;

import java.util.List;

public class BreakStatement extends AbstractNode implements Statement {
    public BreakStatement() {
        super(List.of());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BreakStatement) {
            return true;
        }
        return false;
    }
}
