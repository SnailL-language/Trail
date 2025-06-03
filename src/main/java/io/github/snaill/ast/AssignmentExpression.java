package io.github.snaill.ast;

import java.io.IOException;
import java.util.List;

// import io.github.snaill.bytecode.BytecodeConstants;
// import io.github.snaill.bytecode.BytecodeContext;
// import io.github.snaill.bytecode.BytecodeUtils;
// import java.io.ByteArrayOutputStream;

/**
 * Represents an assignment expression in the AST.
 */
public class AssignmentExpression extends Expression {
    public AssignmentExpression(Expression left, Expression right) {
        super(List.of(left, right));
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        try {
            return visitor.visit(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Expression getLeft() {
        return (Expression) children.getFirst();
    }

    public Expression getRight() {
        return (Expression) children.get(1);
    }

    @Override
    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context) throws java.io.IOException, io.github.snaill.exception.FailedCheckException {
        emitBytecode(out, context, null);
    }

    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context, FunctionDeclaration currentFunction) throws java.io.IOException, io.github.snaill.exception.FailedCheckException {
        Expression left = getLeft();
        Expression right = getRight();
        right.emitBytecode(out, context, currentFunction);
        if (left instanceof Identifier id) {
            System.err.println("Looking for variable: " + id.getName());
            if (currentFunction != null) {
                int localIndex = context.getLocalVarIndex(currentFunction, id.getName());
                System.err.println("Local index for " + id.getName() + ": " + localIndex);
                if (localIndex != -1) {
                    out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.STORE_LOCAL);
                    io.github.snaill.bytecode.BytecodeUtils.writeU16(out, localIndex);
                    return;
                }
            }
            int globalIndex = context.getGlobalVarIndex(id.getName());
            System.err.println("Global index for " + id.getName() + ": " + globalIndex);
            if (globalIndex != -1) {
                out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.STORE_GLOBAL);
                io.github.snaill.bytecode.BytecodeUtils.writeU16(out, globalIndex);
            } else {
                String before = getSource() != null ?
                    io.github.snaill.ast.SourceBuilder.toSourceLine(getSource(), getLine(), getCharPosition(), id.getName().length()) :
                    io.github.snaill.ast.SourceBuilder.toSourceCode(this);
                throw new io.github.snaill.exception.FailedCheckException(
                    new io.github.snaill.result.CompilationError(
                        io.github.snaill.result.ErrorType.UNKNOWN_VARIABLE,
                        before,
                        "Variable not found: " + id.getName(),
                        ""
                    ).toString()
                );
            }
        } else if (left instanceof ArrayElement ae) {
            ae.getIdentifier().emitBytecode(out, context, currentFunction);
            for (Expression dim : ae.getDims()) {
                dim.emitBytecode(out, context, currentFunction);
            }
            out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.SET_ARRAY);
        } else {
            String before = getSource() != null ?
                io.github.snaill.ast.SourceBuilder.toSourceLine(getSource(), getLine(), getCharPosition(), 1) :
                io.github.snaill.ast.SourceBuilder.toSourceCode(this);
            throw new io.github.snaill.exception.FailedCheckException(
                new io.github.snaill.result.CompilationError(
                    io.github.snaill.result.ErrorType.INVALID_ASSIGNMENT,
                    before,
                    "Invalid left-hand side of assignment",
                    ""
                ).toString()
            );
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AssignmentExpression other) {
            return super.equals(other);
        }
        return false;
    }

    @Override
    public Type getType(Scope scope) throws io.github.snaill.exception.FailedCheckException {
        Type leftType = getLeft().getType(scope);
        Type rightType = getRight().getType(scope);
        // Разрешаем присваивание неотрицательного литерала к usize
        if (leftType instanceof PrimitiveType lt && lt.getName().equals("usize") && isUsableAsUsize(getRight(), scope)) {
            return leftType;
        }
        if (!leftType.equals(rightType)) {
            String before = getSource() != null ?
                io.github.snaill.ast.SourceBuilder.toSourceLine(getSource(), getLine(), getCharPosition(), toString().length()) :
                io.github.snaill.ast.SourceBuilder.toSourceCode(this);
            throw new io.github.snaill.exception.FailedCheckException(
                new io.github.snaill.result.CompilationError(
                    io.github.snaill.result.ErrorType.TYPE_MISMATCH,
                    before,
                    "Type mismatch in assignment: cannot assign " + rightType + " to " + leftType,
                    ""
                ).toString()
            );
        }
        return leftType;
    }

    private boolean isUsableAsUsize(Expression expr, Scope scope) throws io.github.snaill.exception.FailedCheckException {
        if (expr instanceof NumberLiteral nl) {
            return nl.isNonNegative();
        }
        if (expr instanceof Identifier id) {
            Type t = id.getType(scope);
            return t instanceof PrimitiveType pt && pt.getName().equals("usize");
        }
        if (expr instanceof BinaryExpression be) {
            // Проверяем оба операнда
            return isUsableAsUsize(be.getLeft(), scope) && isUsableAsUsize(be.getRight(), scope);
        }
        // Если тип выражения уже usize — разрешаем
        try {
            Type t = expr.getType(scope);
            if (t instanceof PrimitiveType pt && pt.getName().equals("usize")) return true;
        } catch (Exception ignored) {}
        return false;
    }

    public void check(Scope scope) throws io.github.snaill.exception.FailedCheckException {
        Type leftType = getLeft().getType(scope);
        Type rightType = getRight().getType(scope);
        // Разрешаем присваивание неотрицательного литерала к usize
        if (leftType instanceof PrimitiveType lt && lt.getName().equals("usize") && isUsableAsUsize(getRight(), scope)) {
            getLeft().check(scope);
            getRight().check(scope);
            return;
        }
        if (!leftType.equals(rightType)) {
            String before = io.github.snaill.ast.SourceBuilder.toSourceLine(getSource(), getLine(), getCharPosition(), toString().length());
            throw new io.github.snaill.exception.FailedCheckException(
                new io.github.snaill.result.CompilationError(
                    io.github.snaill.result.ErrorType.TYPE_MISMATCH,
                    before,
                    "Type mismatch in assignment: cannot assign " + rightType + " to " + leftType,
                    ""
                ).toString()
            );
        }
        // Проверяем рекурсивно выражения
        getLeft().check(scope);
        getRight().check(scope);
    }
} 