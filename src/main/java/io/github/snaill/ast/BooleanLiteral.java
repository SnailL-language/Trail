package io.github.snaill.ast;

import java.util.List; // For super constructor

public class BooleanLiteral extends PrimaryExpression {

    private final boolean value;

    public BooleanLiteral(boolean value) {
        super(List.of()); // Call to super constructor
        this.value = value;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BooleanLiteral other) {
            return value == other.value && super.equals(other);
        }
        return false;
    }

    @Override
    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context) throws java.io.IOException {
        emitBytecode(out, context, null);
    }

    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context, FunctionDeclaration currentFunction) throws java.io.IOException {
        int constIndex = context.addConstant(getValue());
        out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.PUSH_CONST);
        io.github.snaill.bytecode.BytecodeUtils.writeU16(out, constIndex);
    }

    @Override
    public Type getType(Scope scope) {
        return new PrimitiveType("bool");
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }
}
