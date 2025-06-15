package io.github.snaill.ast;

import io.github.snaill.bytecode.BytecodeConstants;
import io.github.snaill.bytecode.BytecodeContext;
import io.github.snaill.bytecode.BytecodeUtils;
import io.github.snaill.exception.FailedCheckException;
import io.github.snaill.result.CompilationError;
import io.github.snaill.result.ErrorType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Represents an assignment expression in the AST.
 */
public class AssignmentExpression extends Expression {
    private final String operator;

    public AssignmentExpression(Expression left, String operator, Expression right) {
        super(List.of(left, right));
        this.operator = operator;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public Expression getLeft() {
        return (Expression) children.getFirst();
    }

    public Expression getRight() {
        return (Expression) children.get(1);
    }

    public String getOperator() {
        return operator;
    }

    @Override
    public void emitBytecode(ByteArrayOutputStream out, BytecodeContext context) throws IOException, FailedCheckException {
        emitBytecode(out, context, null);
    }

    @Override
    public void emitBytecode(ByteArrayOutputStream out, BytecodeContext context, FunctionDeclaration currentFunction) throws IOException, FailedCheckException {
        Expression left = getLeft();

        if (left instanceof Identifier identifier) {
            if (operator.equals("=")) {
                emitSimpleAssignment(identifier, context, currentFunction, out);
            } else {
                emitCompoundAssignment(identifier, context, currentFunction, out);
            }
        } else if (left instanceof ArrayElement arrayElement) {
            if (operator.equals("=")) {
                emitSimpleAssignmentForArrayElement(arrayElement, context, currentFunction, out);
            } else {
                emitCompoundAssignmentForArrayElement(arrayElement, context, currentFunction, out);
            }
        } else {
            String before = getSource() != null ?
                SourceBuilder.toSourceLine(getSource(), getLine(), getCharPosition(), toString().length()) :
                SourceBuilder.toSourceCode(this);
            throw new FailedCheckException(
                new CompilationError(
                    ErrorType.INVALID_ASSIGNMENT,
                    before,
                    "Invalid left-hand side of assignment",
                    ""
                ).toString()
            );
        }
    }

    private void emitSimpleAssignment(Identifier identifier, BytecodeContext context, FunctionDeclaration currentFunction, ByteArrayOutputStream out) throws IOException, FailedCheckException {
        // 1. Evaluate the right-hand side value. After this step the stack is [value]
        getRight().emitBytecode(out, context, currentFunction);

        // 2. Store the value into the appropriate variable slot
        int localIndex = -1;
        if (currentFunction != null) {
            localIndex = context.getLocalVarIndex(currentFunction, identifier.getName());
        }

        if (localIndex != -1) {
            out.write(BytecodeConstants.Opcode.STORE_LOCAL);
            BytecodeUtils.writeU16(out, localIndex);
        } else {
            int globalIndex = context.getGlobalVarIndex(identifier.getName());
            if (globalIndex != -1) {
                out.write(BytecodeConstants.Opcode.STORE_GLOBAL);
                BytecodeUtils.writeU16(out, globalIndex);
            } else {
                throw new FailedCheckException("Unknown variable: " + identifier.getName());
            }
        }
    }

    private void emitCompoundAssignment(Identifier identifier, BytecodeContext context, FunctionDeclaration currentFunction, ByteArrayOutputStream out) throws IOException, FailedCheckException {
        int localIndex = -1;
        if (currentFunction != null) {
            localIndex = context.getLocalVarIndex(currentFunction, identifier.getName());
        }

        // 1. Push the old value of the variable onto the stack.
        if (localIndex != -1) {
            out.write(BytecodeConstants.Opcode.PUSH_LOCAL);
            BytecodeUtils.writeU16(out, localIndex);
        } else {
            int globalIndex = context.getGlobalVarIndex(identifier.getName());
            if (globalIndex != -1) {
                out.write(BytecodeConstants.Opcode.PUSH_GLOBAL);
                BytecodeUtils.writeU16(out, globalIndex);
            } else {
                throw new FailedCheckException("Unknown variable: " + identifier.getName());
            }
        }

        // 2. Push the right-hand side value.
        getRight().emitBytecode(out, context, currentFunction);

        // 3. Perform the operation.
        out.write(getOpcodeForOperator(operator));

        // 4. Store the result back into the variable.
        if (localIndex != -1) {
            out.write(BytecodeConstants.Opcode.STORE_LOCAL);
            BytecodeUtils.writeU16(out, localIndex);
        } else {
            int globalIndex = context.getGlobalVarIndex(identifier.getName());
            out.write(BytecodeConstants.Opcode.STORE_GLOBAL);
            BytecodeUtils.writeU16(out, globalIndex);
        }
    }

    private void emitSimpleAssignmentForArrayElement(ArrayElement left, BytecodeContext context, FunctionDeclaration currentFunction, ByteArrayOutputStream out) throws IOException, FailedCheckException {
        // For arr[d1]...[dn] = value the stack for SET_ARRAY must be [index, value, array]
    // (left-most element in the spec is the top of the stack).

        // 1. Push the array that will be modified: arr[d1]...[dn-1]
        left.getIdentifier().emitBytecode(out, context, currentFunction);
        List<Expression> dims = left.getDims();
        for (int i = 0; i < dims.size() - 1; i++) {
            dims.get(i).emitBytecode(out, context, currentFunction);
            out.write(BytecodeConstants.Opcode.GET_ARRAY);
        }

        // 2. Push the value to be assigned.
        getRight().emitBytecode(out, context, currentFunction);

        // 3. Push the final index, dn.
        dims.getLast().emitBytecode(out, context, currentFunction);

        // 4. Set the array element.
        out.write(BytecodeConstants.Opcode.SET_ARRAY);
    }

    private void emitCompoundAssignmentForArrayElement(ArrayElement left, BytecodeContext context, FunctionDeclaration currentFunction, ByteArrayOutputStream out) throws IOException, FailedCheckException {
        // For arr[d1]...[dn] op= value the stack for SET_ARRAY must be [index, value, array]

        // 1. Push the array that will be modified: arr[d1]...[dn-1]
        left.getIdentifier().emitBytecode(out, context, currentFunction);
        List<Expression> dims = left.getDims();
        for (int i = 0; i < dims.size() - 1; i++) {
            dims.get(i).emitBytecode(out, context, currentFunction);
            out.write(BytecodeConstants.Opcode.GET_ARRAY);
        }

        // 2. Calculate the new value. This is (old_value op right_value).
        // 2a. Get old_value by evaluating the full expression arr[d1]...[dn].
        left.emitBytecode(out, context, currentFunction);
        // 2b. Get right_value.
        getRight().emitBytecode(out, context, currentFunction);
        // 2c. Perform operation.
        out.write(getOpcodeForOperator(operator));

        // 3. Push the final index, dn.
        dims.getLast().emitBytecode(out, context, currentFunction);

        // 4. Set the array element.
        out.write(BytecodeConstants.Opcode.SET_ARRAY);
    }

    private byte getOpcodeForOperator(String op) {
        return switch (op) {
            case "+=" -> BytecodeConstants.Opcode.ADD;
            case "-=" -> BytecodeConstants.Opcode.SUB;
            case "*=" -> BytecodeConstants.Opcode.MUL;
            case "/=" -> BytecodeConstants.Opcode.DIV;
            default -> throw new IllegalArgumentException("Unsupported compound assignment operator: " + op);
        };
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;
        AssignmentExpression other = (AssignmentExpression) obj;
        return Objects.equals(operator, other.operator);
    }

    @Override
    public Type getType(Scope scope) throws FailedCheckException {
        Type leftType = getLeft().getType(scope);
        Type rightType = getRight().getType(scope);
        if (leftType instanceof PrimitiveType lt && lt.getName().equals("usize") && isUsableAsUsize(getRight(), scope)) {
            return leftType;
        }
        if (!leftType.equals(rightType)) {
            String before = getSource() != null ?
                SourceBuilder.toSourceLine(getSource(), getLine(), getCharPosition(), toString().length()) :
                SourceBuilder.toSourceCode(this);
            throw new FailedCheckException(
                new CompilationError(
                    ErrorType.TYPE_MISMATCH,
                    before,
                    "Type mismatch in assignment: cannot assign " + rightType + " to " + leftType,
                    ""
                ).toString()
            );
        }
        return leftType;
    }

    private boolean isUsableAsUsize(Expression expr, Scope scope) throws FailedCheckException {
        if (expr instanceof NumberLiteral nl) {
            return nl.isNonNegative();
        }
        try {
            Type type = expr.getType(scope);
            return type instanceof PrimitiveType pt && pt.getName().equals("usize");
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public void check(Scope scope) throws FailedCheckException {
        getLeft().check(scope);
        getRight().check(scope);

        Type leftType = getLeft().getType(scope);
        Type rightType = getRight().getType(scope);

        if (operator.equals("+=")) {
            boolean isLeftArray = !(leftType instanceof PrimitiveType);
            boolean isRightArray = !(rightType instanceof PrimitiveType);
            if (!isLeftArray && !isRightArray) {
                return;
            }
        }

        // For compound assignments, check if the operation is valid for the types.
        if (!operator.equals("=")) {
            boolean isLeftNumeric = leftType instanceof PrimitiveType pt && (pt.getName().equals("i32") || pt.getName().equals("usize"));
            boolean isRightNumeric = rightType instanceof PrimitiveType pt && (pt.getName().equals("i32") || pt.getName().equals("usize"));

            if (!isLeftNumeric || !isRightNumeric) {
                String before = SourceBuilder.toSourceLine(getSource(), getLine(), getCharPosition(), toString().length());
                throw new FailedCheckException(
                    new CompilationError(
                        ErrorType.TYPE_MISMATCH,
                        before,
                        "Operator '" + operator + "' cannot be applied to types '" + leftType + "' and '" + rightType + "'",
                        ""
                    ).toString()
                );
            }
        }

        // Check for type compatibility in assignment.
        if (leftType instanceof PrimitiveType lt && lt.getName().equals("usize") && isUsableAsUsize(getRight(), scope)) {
            return; // This is a valid assignment (e.g., usize = 5)
        }

        if (!leftType.equals(rightType)) {
            String before = SourceBuilder.toSourceLine(getSource(), getLine(), getCharPosition(), toString().length());
            throw new FailedCheckException(
                new CompilationError(
                    ErrorType.TYPE_MISMATCH,
                    before,
                    "Type mismatch in assignment: cannot assign " + rightType + " to " + leftType,
                    ""
                ).toString()
            );
        }
    }
}