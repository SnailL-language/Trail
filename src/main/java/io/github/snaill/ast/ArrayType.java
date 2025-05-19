package io.github.snaill.ast;

import java.util.Objects;

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
        if (obj instanceof ArrayType other) {
            return other.elementType.equals(elementType) 
                && other.size.equals(size);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(elementType, size);
    }
}
