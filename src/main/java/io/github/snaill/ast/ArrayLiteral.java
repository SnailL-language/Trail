package io.github.snaill.ast;

import java.util.ArrayList;
import java.util.List;

public class ArrayLiteral extends PrimaryExpression {
    // elements are already children via super(elements)

    public ArrayLiteral(List<Expression> elements) {
        super(new ArrayList<>(elements)); // Pass elements to super constructor as List<Node>
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
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
        byte typeId = getTypeId(elements);

        // 1. Create the new array. Its reference is now on the stack.
        out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.NEW_ARRAY);
        io.github.snaill.bytecode.BytecodeUtils.writeU16(out, size);
        out.write(typeId);

        // 2. Iterate through elements to populate the array.
        for (int i = 0; i < size; i++) {
            // a. Duplicate the array reference. Stack: [..., array_ref, array_ref]
            out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.DUP);

            // b. Push the index. Stack: [..., array_ref, array_ref, index]
            out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.PUSH_CONST);
            io.github.snaill.bytecode.BytecodeUtils.writeU16(out, context.addConstant((long)i));

            // c. Push the value. Stack: [..., array_ref, array_ref, index, value]
            elements.get(i).emitBytecode(out, context, currentFunction);

            // d. Set the array element. This consumes the top 3 stack values.
            //    Stack is now back to: [..., array_ref]
            out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.SET_ARRAY);
        }
        // 3. After the loop, the original array reference is left on the stack,
        //    ready to be consumed by the parent expression (e.g., a VariableDeclaration).
    }

    private static byte getTypeId(List<Expression> elements) {
        String typeName = "i32";
        if (!elements.isEmpty()) {
            var first = elements.getFirst();
            if (first instanceof NumberLiteral) typeName = "i32";
            else if (first instanceof StringLiteral) typeName = "string";
            else if (first instanceof BooleanLiteral) typeName = "i32";
        }
        return switch (typeName) {
            case "i32" -> io.github.snaill.bytecode.BytecodeConstants.TypeId.I32;
            case "string" -> io.github.snaill.bytecode.BytecodeConstants.TypeId.STRING;
            default -> io.github.snaill.bytecode.BytecodeConstants.TypeId.I32;
        };
    }

    @Override
    public Type getType(Scope scope) throws io.github.snaill.exception.FailedCheckException {
        var elements = getElements();
        if (elements.isEmpty()) return new ArrayType(new PrimitiveType("i32"), new NumberLiteral(0));
        Type elemType = elements.getFirst().getType(scope);
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
