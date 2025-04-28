package io.github.snaill.ast;

import java.util.List;
import java.util.stream.Collectors;

public class ParameterList extends AbstractNode {
    public ParameterList(FunctionDeclaration parent, List<Parameter> children) {
        super(List.copyOf(children), parent);
    }

    public List<Parameter> getParameters() {
        return this.getChildren().stream().map(Parameter.class::cast).collect(Collectors.toList());
    }
}
