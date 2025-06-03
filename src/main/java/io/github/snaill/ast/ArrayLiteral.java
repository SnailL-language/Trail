package io.github.snaill.ast;

import java.io.IOException; // For accept method
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ArrayLiteral extends PrimaryExpression {
    // elements are already children via super(elements)

    public ArrayLiteral(List<Expression> elements) {
        super(new ArrayList<Node>(elements)); // Pass elements to super constructor as List<Node>
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        try {
            return visitor.visit(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Expression> getElements() {
        // Children are stored as List<Node>, cast them to List<Expression>
        return (List<Expression>)(List<? extends Node>) super.getChildren(); 
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ArrayLiteral other) {
            // For arrays, order typically matters, and super.equals already compares children.
            return super.equals(other);
        }
        return false;
    }

    @Override
    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context) throws java.io.IOException, io.github.snaill.exception.FailedCheckException {
        emitBytecode(out, context, null);
    }

    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context, FunctionDeclaration currentFunction) throws java.io.IOException, io.github.snaill.exception.FailedCheckException {
        var elements = getElements();
        int size = elements.size();
        String typeName = "i32";
        if (!elements.isEmpty()) {
            var first = elements.getFirst();
            if (first instanceof NumberLiteral) typeName = "i32";
            else if (first instanceof StringLiteral) typeName = "string";
            else if (first instanceof BooleanLiteral) typeName = "i32";
        }
        byte typeId = switch (typeName) {
            case "i32" -> io.github.snaill.bytecode.BytecodeConstants.TypeId.I32;
            case "string" -> io.github.snaill.bytecode.BytecodeConstants.TypeId.STRING;
            default -> io.github.snaill.bytecode.BytecodeConstants.TypeId.I32;
        };
        out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.NEW_ARRAY);
        io.github.snaill.bytecode.BytecodeUtils.writeU16(out, size);
        out.write(typeId);
        // Сохраняем массив во временную переменную
        String tmpName = "__tmp_array_" + System.identityHashCode(this);
        int tmpIdx;
        if (currentFunction != null) {
            // Локальная временная переменная
            tmpIdx = context.getLocalVarIndex(currentFunction, tmpName);
            if (tmpIdx < 0) {
                throw new RuntimeException("Temporary local for array literal not found: " + tmpName);
            }
            out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.STORE_LOCAL);
            io.github.snaill.bytecode.BytecodeUtils.writeU16(out, tmpIdx);
            for (int i = 0; i < size; i++) {
                out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.PUSH_LOCAL);
                io.github.snaill.bytecode.BytecodeUtils.writeU16(out, tmpIdx);
                // Используем константы для индексов массива, чтобы гарантировать правильную индексацию
                out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.PUSH_CONST);
                io.github.snaill.bytecode.BytecodeUtils.writeU16(out, context.addConstant((long)i));
                elements.get(i).emitBytecode(out, context, currentFunction);
                out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.SET_ARRAY);
            }
            // В конце вернуть массив на стек
            out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.PUSH_LOCAL);
            io.github.snaill.bytecode.BytecodeUtils.writeU16(out, tmpIdx);
        } else {
            // Глобальная временная переменная (маловероятно, но для совместимости)
            tmpIdx = context.getGlobalVarIndex(tmpName);
            if (tmpIdx < 0) {
                tmpIdx = context.addGlobalVariable(tmpName);
            }
            out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.STORE_GLOBAL);
            io.github.snaill.bytecode.BytecodeUtils.writeU16(out, tmpIdx);
            for (int i = 0; i < size; i++) {
                out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.PUSH_GLOBAL);
                io.github.snaill.bytecode.BytecodeUtils.writeU16(out, tmpIdx);
                // Используем константы для индексов массива, чтобы гарантировать правильную индексацию
                out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.PUSH_CONST);
                io.github.snaill.bytecode.BytecodeUtils.writeU16(out, context.addConstant((long)i));
                elements.get(i).emitBytecode(out, context, currentFunction);
                out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.SET_ARRAY);
            }
            out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.PUSH_GLOBAL);
            io.github.snaill.bytecode.BytecodeUtils.writeU16(out, tmpIdx);
        }
    }

    @Override
    public Type getType(Scope scope) throws io.github.snaill.exception.FailedCheckException {
        var elements = getElements();
        if (elements.isEmpty()) return new ArrayType(new PrimitiveType("i32"), new NumberLiteral(0));
        Type elemType = elements.get(0).getType(scope);
        for (Expression e : elements) {
            Type t = e.getType(scope);
            if (!t.equals(elemType)) {
                String before = getSource() != null ?
                    io.github.snaill.ast.SourceBuilder.toSourceLine(getSource(), getLine(), getCharPosition(), toString().length()) :
                    io.github.snaill.ast.SourceBuilder.toSourceCode(this);
                throw new io.github.snaill.exception.FailedCheckException(
                    new io.github.snaill.result.CompilationError(
                        io.github.snaill.result.ErrorType.TYPE_MISMATCH,
                        before,
                        "Array elements must have same type",
                        ""
                    ).toString()
                );
            }
        }
        return new ArrayType(elemType, new NumberLiteral(elements.size()));
    }

    @Override
    public String toString() {
        return getElements().stream().map(Object::toString).collect(java.util.stream.Collectors.joining(",", "[", "]"));
    }
}
