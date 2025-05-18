package io.github.snaill.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.github.snaill.result.CompilationError;
import io.github.snaill.result.ErrorType;
import io.github.snaill.result.Result;

public class Scope extends AbstractNode {
    public Scope(List<Statement> children) {
        super(children);
    }

    public List<Statement> getStatements() {
        return super.getChildren().stream().map(Statement.class::cast).toList();
    }

    @Override
    public List<Result> checkDeadCode() {
        List<Result> results = new ArrayList<>();
        for (int i = 0; i < children.size() - 1; i++) {
            if (children.get(i) instanceof ReturnStatement || children.get(i) instanceof BreakStatement) {
                results.add(new CompilationError(
                        ErrorType.DEAD_CODE,
                        SourceBuilder.toSourceCode(children.get(i)),
                        IntStream.range(i + 1, children.size())
                        .mapToObj(children::get)
                        .map(SourceBuilder::toSourceCode)
                        .collect(Collectors.joining(System.lineSeparator())),
                        ""
                    )
                );
                break;
            }
        }
        results.addAll(super.checkDeadCode());
        return results;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Scope) {
            return super.equals(obj);
        }
        return false;
    }
}
