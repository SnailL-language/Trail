package io.github.snaill.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ArrayElement extends PrimaryExpression {
    
    private final Identifier name;
    private final List<NumberLiteral> dims = new ArrayList<>();

    public ArrayElement(Identifier name) {
        this.name = name;
    }

    public Identifier getIdentifier() {
        return name;
    }

    public void addDim(NumberLiteral dim) {
        dims.add(dim);
    }

    public List<NumberLiteral> getDims() {
        return dims;
    }

    @Override
    public void checkUnusedVariables(Set<VariableDeclaration> unused) {
        name.checkUnusedVariables(unused);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ArrayElement other) {
            return name.equals(other.name) && dims.equals(other.dims);
        }
        return false;
    }
}
