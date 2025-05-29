package io.github.snaill.ast;

import io.github.snaill.bytecode.BytecodeConstants;
import io.github.snaill.bytecode.BytecodeContext;
import io.github.snaill.exception.FailedCheckException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Представляет доступ к элементу массива в AST.
 * Генерирует байткод для получения элемента массива по индексу.
 */
public class ArrayAccess extends Expression {
    public ArrayAccess(Expression array, Expression index) {
        super(List.of(array, index));
    }

    public Expression getArray() {
        return (Expression) children.getFirst();
    }

    public Expression getIndex() {
        return (Expression) children.get(1);
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        try {
            return visitor.visit(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void emitBytecode(ByteArrayOutputStream out, BytecodeContext context) throws IOException, FailedCheckException {
        emitBytecode(out, context, null);
    }

    public void emitBytecode(ByteArrayOutputStream out, BytecodeContext context, FunctionDeclaration currentFunction) throws IOException, FailedCheckException {
        getArray().emitBytecode(out, context, currentFunction);
        getIndex().emitBytecode(out, context, currentFunction);
        out.write(BytecodeConstants.Opcode.GET_ARRAY);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ArrayAccess) {
            return super.equals(obj);
        }
        return false;
    }

    @Override
    public Type getType(Scope scope) throws io.github.snaill.exception.FailedCheckException {
        Type arrayType = getArray().getType(scope);
        if (!(arrayType instanceof ArrayType at)) {
            String before = getArray().getSource() != null ?
                io.github.snaill.ast.SourceBuilder.toSourceLine(getArray().getSource(), getArray().getLine(), getArray().getCharPosition(), getArray().toString().length()) :
                io.github.snaill.ast.SourceBuilder.toSourceCode(getArray());
            throw new io.github.snaill.exception.FailedCheckException(
                new io.github.snaill.result.CompilationError(
                    io.github.snaill.result.ErrorType.NOT_AN_ARRAY,
                    before,
                    "Expression is not an array, got " + arrayType,
                    ""
                ).toString()
            );
        }
        Type indexType = getIndex().getType(scope);
        if (!(indexType instanceof PrimitiveType pt) || (!pt.getName().equals("usize") && !pt.getName().equals("i32"))) {
            String before = getIndex().getSource() != null ?
                io.github.snaill.ast.SourceBuilder.toSourceLine(getIndex().getSource(), getIndex().getLine(), getIndex().getCharPosition(), getIndex().toString().length()) :
                io.github.snaill.ast.SourceBuilder.toSourceCode(getIndex());
            throw new io.github.snaill.exception.FailedCheckException(
                new io.github.snaill.result.CompilationError(
                    io.github.snaill.result.ErrorType.TYPE_MISMATCH,
                    before,
                    "Array index must be of type usize or i32, got " + indexType,
                    ""
                ).toString()
            );
        }
        return at.getElementType();
    }
} 