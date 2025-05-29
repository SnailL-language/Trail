package io.github.snaill.ast;

import java.io.IOException;
import java.util.List;

/**
 * Представляет оператор return в AST.
 */
public class ReturnStatement extends AbstractNode implements Statement {
    public ReturnStatement(Expression returnable) {
        super(returnable == null ? List.of() : List.of(returnable));
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        try {
            return visitor.visit(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Expression getReturnable() {
        return children.size() > 0 ? (Expression) getChild(0) : null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ReturnStatement) {
            return super.equals(obj);
        }
        return false;
    }

    @Override
    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context) throws java.io.IOException, io.github.snaill.exception.FailedCheckException {
        emitBytecode(out, context, null);
    }

    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context, FunctionDeclaration currentFunction) throws java.io.IOException, io.github.snaill.exception.FailedCheckException {
        Expression retExpr = getReturnable();
        if (retExpr != null) {
            retExpr.emitBytecode(out, context, currentFunction);
        } else {
            out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.PUSH_CONST);
            io.github.snaill.bytecode.BytecodeUtils.writeU16(out, 0); // Предполагается, что 0 - это индекс константы для null
        }
        out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.RET);
    }

    public void checkType(Type expectedType, Scope scope) throws io.github.snaill.exception.FailedCheckException {
        Expression retExpr = getReturnable();
        if (retExpr == null) {
            if (!(expectedType instanceof PrimitiveType pt) || !pt.getName().equals("void")) {
                String before = getSource() != null ?
                    io.github.snaill.ast.SourceBuilder.toSourceLine(getSource(), getLine(), getCharPosition(), toString().length()) :
                    io.github.snaill.ast.SourceBuilder.toSourceCode(this);
                throw new io.github.snaill.exception.FailedCheckException(
                    new io.github.snaill.result.CompilationError(
                        io.github.snaill.result.ErrorType.TYPE_MISMATCH,
                        before,
                        "Return statement must return a value of type " + expectedType + ", got void",
                        ""
                    ).toString()
                );
            }
            return;
        }
        Type actualType = retExpr.getType(scope);
        // Разрешаем возвращать неотрицательный литерал как usize
        if (expectedType instanceof PrimitiveType et && et.getName().equals("usize") && isUsableAsUsize(retExpr, scope)) {
            return;
        }
        if (!actualType.equals(expectedType)) {
            String before = getSource() != null ?
                io.github.snaill.ast.SourceBuilder.toSourceLine(getSource(), getLine(), getCharPosition(), retExpr.toString().length()) :
                io.github.snaill.ast.SourceBuilder.toSourceCode(retExpr);
            throw new io.github.snaill.exception.FailedCheckException(
                new io.github.snaill.result.CompilationError(
                    io.github.snaill.result.ErrorType.TYPE_MISMATCH,
                    before,
                    "Return statement type mismatch: expected " + expectedType + ", got " + actualType,
                    ""
                ).toString()
            );
        }
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
            return isUsableAsUsize(be.getLeft(), scope) && isUsableAsUsize(be.getRight(), scope);
        }
        try {
            Type t = expr.getType(scope);
            if (t instanceof PrimitiveType pt && pt.getName().equals("usize")) return true;
        } catch (Exception ignored) {}
        return false;
    }
}
