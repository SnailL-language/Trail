package io.github.snaill.ast;

import java.io.IOException;
import java.util.List; // For super constructor

/**
 * Представляет числовой литерал в AST.
 */
public class NumberLiteral extends PrimaryExpression {
    private final long value;

    public NumberLiteral(long value) {
        super(List.of()); // Call to super constructor
        this.value = value;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public long getValue() {
        return value;
    }

    @Override
    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context) throws java.io.IOException {
        emitBytecode(out, context, null);
    }

    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context, FunctionDeclaration currentFunction) throws java.io.IOException {
        // Добавляем константу в пул констант и получаем её индекс
        Long longValue = getValue();
        int existingIndex = context.getConstantIndex(longValue);
        int constIndex;
        
        if (existingIndex != -1) {
            // Используем существующий индекс константы
            constIndex = existingIndex;
        } else {
            // Добавляем новую константу
            constIndex = context.addConstant(longValue);
        }
        
        // Генерируем инструкцию PUSH_CONST с индексом константы
        out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.PUSH_CONST);
        io.github.snaill.bytecode.BytecodeUtils.writeU16(out, constIndex);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NumberLiteral other) {
            return value == other.value && super.equals(other); // Added super.equals for consistency
        }
        return false;
    }

    @Override
    public Type getType(Scope scope) {
        if (value >= 0) {
            return new PrimitiveType("usize");
        }
        return new PrimitiveType("i32");
    }

    @Override
    public String toString() {
        return Long.toString(value);
    }

    public boolean isNonNegative() {
        return value >= 0;
    }
}
