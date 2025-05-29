package io.github.snaill.ast;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Представляет цикл for в AST.
 */
public class ForLoop extends AbstractNode implements Statement {
    public ForLoop(Scope body, Expression condition, Expression step) {
        super(List.of(body, condition, step));
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        try {
            return visitor.visit(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Scope getBody() {
        return (Scope) children.get(0);
    }

    public Expression getCondition() {
        return (Expression) children.get(1);
    }

    public Expression getStep() {
        return (Expression) children.get(2);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ForLoop) {
            return super.equals(obj);
        }
        return false;
    }

    
    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context, FunctionDeclaration currentFunction) throws java.io.IOException, io.github.snaill.exception.FailedCheckException {
        getBody().emitBytecode(out, context, currentFunction);
        int start = out.size();
        getCondition().emitBytecode(out, context, currentFunction);
        out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.JMP_IF_FALSE);
        int jmpIfFalseOffset = out.size();
        io.github.snaill.bytecode.BytecodeUtils.writeU16(out, 0); // Placeholder for jump offset
        getStep().emitBytecode(out, context, currentFunction);
        out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.POP);
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
    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context) throws java.io.IOException, io.github.snaill.exception.FailedCheckException {
        emitBytecode(out, context, null);
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
                    "For loop condition must be of type bool, got " + conditionType,
                    ""
                ).toString()
            );
        }
        getStep().check(scope);
        getBody().check(scope);
    }

    @Override
    public void checkUnusedVariables(Set<VariableDeclaration> unused) {
        getCondition().checkUnusedVariables(unused);
        getStep().checkUnusedVariables(unused);
        getBody().checkUnusedVariables(unused);
    }
}
