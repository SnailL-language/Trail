package io.github.snaill.ast;

public class PrimitiveType extends Type {
    private final String name;

    public PrimitiveType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PrimitiveType other) {
            return name.equals(other.name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        try {
            return visitor.visit(this);
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
