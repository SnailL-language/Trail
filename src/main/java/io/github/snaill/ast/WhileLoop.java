package io.github.snaill.ast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import io.github.snaill.bytecode.BytecodeUtils;

/**
 * Представляет цикл while в AST.
 */
public class WhileLoop extends AbstractNode implements Statement {
    public WhileLoop(Expression condition, Scope body) {
        super(List.of(condition, body));
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        try {
            return visitor.visit(this);
        } catch (IOException e) {
            throw new RuntimeException(e); 
        }
    }

    public Expression getCondition() {
        return (Expression) children.getFirst();
    }

    public Scope getBody() {
        return (Scope) children.get(1);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WhileLoop) {
            return super.equals(obj);
        }
        return false;
    }

    @Override
    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context) throws java.io.IOException, io.github.snaill.exception.FailedCheckException {
        emitBytecode(out, context, null);
    }

    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context, FunctionDeclaration currentFunction) throws java.io.IOException, io.github.snaill.exception.FailedCheckException {
        int start = out.size();
        getCondition().emitBytecode(out, context, currentFunction);
        out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.JMP_IF_FALSE);
        int jmpIfFalseOffset = out.size();
        io.github.snaill.bytecode.BytecodeUtils.writeU16(out, 0); // Placeholder for jump offset
        getBody().emitBytecode(out, context, currentFunction);
        out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.JMP);
        io.github.snaill.bytecode.BytecodeUtils.writeU16(out, start - out.size() - 2);
        int end = out.size();
        // Обновляем значение jump offset
        byte[] bytes = out.toByteArray();
        io.github.snaill.bytecode.BytecodeUtils.writeU16(new java.io.ByteArrayOutputStream() { @Override public void write(int b) { bytes[jmpIfFalseOffset + (count++)] = (byte)b; } private int count = 0; }, end - jmpIfFalseOffset - 2);
        out.reset();
        out.write(bytes);
    }

    @Override
    public java.util.List<io.github.snaill.result.Result> checkDeadCode() {
        return super.checkDeadCode();
    }

    @Override
    public void check(Scope scope) throws io.github.snaill.exception.FailedCheckException {
        Type conditionType = getCondition().getType(scope);
        if (!(conditionType instanceof PrimitiveType pt) || !pt.getName().equals("bool")) {
            String before = getCondition().getSource() != null ?
                io.github.snaill.ast.SourceBuilder.toSourceLine(getCondition().getSource(), getCondition().getLine(), getCondition().getCharPosition(), getCondition().toString().length()) :
                io.github.snaill.ast.SourceBuilder.toSourceCode(getCondition());
            throw new io.github.snaill.exception.FailedCheckException(
                new io.github.snaill.result.CompilationError(
                    io.github.snaill.result.ErrorType.TYPE_MISMATCH,
                    before,
                    "While loop condition must be of type bool, got " + conditionType,
                    ""
                ).toString()
            );
        }
        getBody().check(scope);
    }
}
