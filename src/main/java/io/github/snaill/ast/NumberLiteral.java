package io.github.snaill.ast;

import java.io.IOException;
import java.util.List; // For super constructor

// Unused imports commented out or to be removed by IDE
// import io.github.snaill.bytecode.BytecodeConstants;
// import io.github.snaill.bytecode.BytecodeContext;
// import io.github.snaill.bytecode.BytecodeUtils;
// import java.io.ByteArrayOutputStream;

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
        try {
            return visitor.visit(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public long getValue() {
        return value;
    }

    @Override
    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context) throws java.io.IOException, io.github.snaill.exception.FailedCheckException {
        emitBytecode(out, context, null);
    }

    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context, FunctionDeclaration currentFunction) throws java.io.IOException, io.github.snaill.exception.FailedCheckException {
        int constIndex = context.addConstant(getValue());
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
