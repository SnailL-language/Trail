package io.github.snaill.ast;

public class ArrayType extends Type {
    private final Type elementType;
    private final NumberLiteral size;

    protected ArrayType(Type elementType, NumberLiteral size) {
        this.elementType = elementType;
        this.size = size;
    }

    public Type getElementType() {
        return elementType;
    }

    public NumberLiteral getSize() {
        return size;
    }
}
