package io.github.snaill.ast;

import java.util.ArrayList;
import java.util.List;

public class FunctionDeclaration extends AbstractNode implements GlobalDeclaration {
    private final String name;

    public FunctionDeclaration(String name, List<Parameter> parameters, Type returnType, Scope scope) {
        List<Node> children = new ArrayList<>(parameters);
        children.add(returnType);
        children.add(scope);
        super(children);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Parameter> getParameterList() {
        return this.getChildren().stream().filter(Parameter.class::isInstance).map(Parameter.class::cast).toList();
    }

    public Type getReturnType() {
        return (Type) this.getChild(this.getChildren().size() - 2);
    }

    public Scope getScope() {
        return (Scope) this.getChild(this.getChildren().size() - 1);
    }
}
