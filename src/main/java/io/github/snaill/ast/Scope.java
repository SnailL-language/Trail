package io.github.snaill.ast;

import java.util.List;
import java.util.stream.Collectors;

public class Scope extends AbstractNode {
    public Scope(List<Statement> children) {
        super(List.copyOf(children));
    }

    public List<Statement> getStatements() {
        return super.getChildren().stream().map(Statement.class::cast).collect(Collectors.toList());
    }
}
