package io.github.snaill.ast;

import java.util.List;
import java.util.stream.Stream;

public class FunctionDeclaration extends AbstractNode implements Statement {
    private final String name;

    public FunctionDeclaration(String name, List<Parameter> parameters, Type returnType, Scope scope) {
        super(Stream.<Node>concat(parameters.stream(), Stream.of(returnType, scope)).toList());
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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FunctionDeclaration other) {
            return name.equals(other.name)
                && super.equals(other);
        }
        return false;
    }
}
