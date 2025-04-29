package io.github.snaill.ast;

import java.util.List;

public class FunctionDeclaration extends AbstractNode implements GlobalDeclaration {
    private final String name;

    public FunctionDeclaration(String name, ParameterList parameters, Type returnType, Scope scope) {
        super(List.of(parameters, returnType, scope));
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ParameterList getParameterList() {
        return (ParameterList) this.getChild(0);
    }

    public Type getReturnType() {
        return (Type) this.getChild(1);
    }

    public Scope getScope() {
        return (Scope) this.getChild(2);
    }
}
