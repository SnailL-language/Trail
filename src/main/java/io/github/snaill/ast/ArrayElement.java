package io.github.snaill.ast;

import java.util.ArrayList; // For casting List<Expression> to List<Node>
import java.util.List;
import java.util.Set;

public class ArrayElement extends Expression {
    private final Expression identifier; // This is the array variable itself
    // dims are already children via super(dims)

    public ArrayElement(Expression identifier, List<Expression> dims) {
        super(new ArrayList<>(dims)); // Pass dims to super constructor as List<Node>
        this.identifier = identifier;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public Expression getIdentifier() {
        return identifier;
    }

    @SuppressWarnings("unchecked")
    public List<Expression> getDims() {
        // Children (dims) are stored as List<Node>, cast them to List<Expression>
        return (List<Expression>)(List<? extends Node>) super.getChildren();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ArrayElement other) {
            // super.equals(other) will compare dims (children)
            return identifier.equals(other.identifier) && super.equals(other);
        }
        return false;
    }

    @Override
    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context) throws java.io.IOException, io.github.snaill.exception.FailedCheckException {
        emitBytecode(out, context, null);
    }

    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context, FunctionDeclaration currentFunction) throws java.io.IOException, io.github.snaill.exception.FailedCheckException {
        if (getIdentifier() == null) {
            throw new io.github.snaill.exception.FailedCheckException(
                "ERROR:^UNKNOWN_VARIABLE" +
                System.lineSeparator() +
                "================================" +
                "Unknown variable: <array>" + System.lineSeparator() +
                "================================"
            );
        }
        getIdentifier().emitBytecode(out, context, currentFunction);
        for (Expression dim : getDims()) {
            dim.emitBytecode(out, context, currentFunction);
            out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.GET_ARRAY);
        }
    }

    @Override
    public void checkUnusedVariables(Set<VariableDeclaration> unused) {
        // Using an array means using its identifier
        if (identifier instanceof Identifier id) {
            unused.removeIf(v -> v.getName().equals(id.getName()));
        } else if (identifier != null) {
            identifier.checkUnusedVariables(unused);
        }
        for (var dim : getDims()) {
            dim.checkUnusedVariables(unused);
        }
    }

    @Override
    public Type getType(Scope scope) throws io.github.snaill.exception.FailedCheckException {
        Type currentType = identifier.getType(scope);
        List<Expression> dims = getDims();

        for (int i = 0; i < dims.size(); i++) {
            Expression dimExpr = dims.get(i);
            if (!(currentType instanceof ArrayType arrayType)) {
                String before = getSource() != null ?
                    io.github.snaill.ast.SourceBuilder.toSourceLine(getSource(), getLine(), getCharPosition(), toString().length()) :
                    io.github.snaill.ast.SourceBuilder.toSourceCode(this);
                throw new io.github.snaill.exception.FailedCheckException(
                    new io.github.snaill.result.CompilationError(
                        io.github.snaill.result.ErrorType.NOT_AN_ARRAY,
                        before,
                        "Expression '" + identifier + "' is not an array or indexed too deeply at dimension " + (i + 1) + ". Expected array, got " + currentType,
                        ""
                    ).toString()
                );
            }

            Type indexType = dimExpr.getType(scope);
            if (!(indexType instanceof PrimitiveType pt) || (!pt.getName().equals("usize") && !pt.getName().equals("i32"))) {
                String before = dimExpr.getSource() != null ?
                    io.github.snaill.ast.SourceBuilder.toSourceLine(dimExpr.getSource(), dimExpr.getLine(), dimExpr.getCharPosition(), dimExpr.toString().length()) :
                    io.github.snaill.ast.SourceBuilder.toSourceCode(dimExpr);
                throw new io.github.snaill.exception.FailedCheckException(
                    new io.github.snaill.result.CompilationError(
                        io.github.snaill.result.ErrorType.TYPE_MISMATCH,
                        before,
                        "Array index for dimension " + (i + 1) + " must be of type usize or i32, got " + indexType,
                        ""
                    ).toString()
                );
            }
            currentType = arrayType.getElementType();
        }
        return currentType;
    }
}