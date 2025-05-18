package io.github.snaill.ast;

import java.util.ArrayList;
import java.util.List;

import io.github.snaill.result.CompilationError;
import io.github.snaill.result.ErrorType;
import io.github.snaill.result.Result;

public class IfStatement extends AbstractNode implements Statement {

    private final boolean hasElse;

    public IfStatement(Expression condition, Scope body, Scope elseBody) {
        super(elseBody == null ? List.of(condition, body) : List.of(condition, body, elseBody));
        this.hasElse = elseBody != null;
    }

    public Expression getCondition() {
        return (Expression) children.getFirst();
    }

    public Scope getBody() {
        return (Scope) children.get(1);
    }

    public Scope getElseBody() {
        return hasElse ? (Scope) children.get(2) : null;
    }

    @Override
    public List<Result> checkDeadCode() {
        List<Result> results = new ArrayList<>();
        if (getCondition() instanceof BooleanLiteral literal) {
            if (literal.getValue() && hasElse) {
                results.add(new CompilationError(
                        ErrorType.DEAD_CODE, 
                        "else {",
                        SourceBuilder.toSourceCode(getElseBody()),
                        "}"
                    )
                );
            } else if (!literal.getValue()) {
                results.add(new CompilationError(
                        ErrorType.DEAD_CODE, 
                        "if(%s) {",
                        SourceBuilder.toSourceCode(getBody()),
                        "}"
                    )
                );
            }
        }
        results.addAll(super.checkDeadCode());
        return results;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IfStatement) {
            return super.equals(obj);
        }
        return false;
    }
}
