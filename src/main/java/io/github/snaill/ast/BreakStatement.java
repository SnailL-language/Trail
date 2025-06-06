package io.github.snaill.ast;

import java.io.IOException;
import java.util.List;

/**
 * Представляет оператор break в AST.
 */
public class BreakStatement extends AbstractNode implements Statement {
    public BreakStatement() {
        super(List.of());
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BreakStatement) {
            return super.equals(obj); // Should compare based on AbstractNode's children (empty list here)
        }
        return false;
    }

    @Override
    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context) throws java.io.IOException {
        emitBytecode(out, context, null);
    }

    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context, FunctionDeclaration currentFunction) throws java.io.IOException {
        out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.JMP);
        io.github.snaill.bytecode.BytecodeUtils.writeU16(out, 0); // Placeholder, будет обновлено позже
    }
}
