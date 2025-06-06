package io.github.snaill.ast;


public class ArrayType extends Type {
    private final Type elementType;
    private final NumberLiteral size;

    public ArrayType(Type elementType, NumberLiteral size) {
        this.elementType = elementType;
        this.size = size;
    }

    public Type getElementType() {
        return elementType;
    }

    public NumberLiteral getSize() {
        return size;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ArrayType other = (ArrayType) obj;
        return elementType.equals(other.elementType) && size.equals(other.size);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(elementType, size);
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "[" + elementType + ";" + size + "]";
    }
}
