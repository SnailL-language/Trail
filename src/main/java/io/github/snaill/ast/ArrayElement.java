package io.github.snaill.ast;

import java.io.IOException; // For accept method
import java.util.ArrayList; // For casting List<Expression> to List<Node>
import java.util.List;
import java.util.Set;

public class ArrayElement extends Expression {
    private final Expression identifier; // This is the array variable itself
    // dims are already children via super(dims)

    public ArrayElement(Expression identifier, List<Expression> dims) {
        super(new ArrayList<Node>(dims)); // Pass dims to super constructor as List<Node>
        this.identifier = identifier;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        try {
            return visitor.visit(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        // Использование массива — это использование идентификатора
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
        Type arrType = identifier.getType(scope);
        if (!(arrType instanceof ArrayType at)) {
            String before = getSource() != null ?
                io.github.snaill.ast.SourceBuilder.toSourceLine(getSource(), getLine(), getCharPosition(), toString().length()) :
                io.github.snaill.ast.SourceBuilder.toSourceCode(this);
            throw new io.github.snaill.exception.FailedCheckException(
                new io.github.snaill.result.CompilationError(
                    io.github.snaill.result.ErrorType.NOT_AN_ARRAY,
                    before,
                    "Not an array: " + identifier,
                    ""
                ).toString()
            );
        }
        return at.getElementType();
    }
}