package io.github.snaill.ast;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Представляет вызов функции в AST.
 */
public class FunctionCall extends Expression {
    private final String name;

    public FunctionCall(String name, List<Expression> arguments) {
        super(List.copyOf(arguments));
        this.name = name;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        try {
            return visitor.visit(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getName() {
        return name;
    }

    public List<Expression> getArguments() {
        return children.stream()
            .map(child -> (Expression) child)
            .toList();
    }

    @Override
    public void checkUnusedFunctions(Set<FunctionDeclaration> unused) {
        unused.removeIf(f -> f.getName().equals(name));
        for (Expression arg : getArguments()) {
            arg.checkUnusedFunctions(unused);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FunctionCall other) {
            return name.equals(other.name)
                && super.equals(other);
        }
        return false;
    }

    @Override
    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context) throws java.io.IOException, io.github.snaill.exception.FailedCheckException {
        emitBytecode(out, context, null);
    }

    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context, FunctionDeclaration currentFunction) throws java.io.IOException, io.github.snaill.exception.FailedCheckException {
        for (Expression arg : getArguments()) {
            arg.emitBytecode(out, context, currentFunction);
        }
        if (isBuiltInFunction(context)) {
            out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.INTRINSIC_CALL);
            io.github.snaill.bytecode.BytecodeUtils.writeU16(out, getBuiltInFunctionIndex(context));
        } else {
            int funcIndex = context.getFunctionIndex(getName());
            if (funcIndex == -1) {
                String before = getSource() != null ?
                    io.github.snaill.ast.SourceBuilder.toSourceLine(getSource(), getLine(), getCharPosition(), getName().length()) :
                    io.github.snaill.ast.SourceBuilder.toSourceCode(this);
                throw new io.github.snaill.exception.FailedCheckException(
                    new io.github.snaill.result.CompilationError(
                        io.github.snaill.result.ErrorType.UNKNOWN_VARIABLE,
                        before,
                        "Function not found: " + getName(),
                        ""
                    ).toString()
                );
            }
            out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.CALL);
            io.github.snaill.bytecode.BytecodeUtils.writeU16(out, funcIndex);
        }
    }

    private boolean isBuiltInFunction(io.github.snaill.bytecode.BytecodeContext context) {
        return getName().equals("println");
    }

    private int getBuiltInFunctionIndex(io.github.snaill.bytecode.BytecodeContext context) {
        return 0;
    }

    @Override
    public Type getType(Scope scope) throws io.github.snaill.exception.FailedCheckException {
        FunctionDeclaration decl = scope.resolveFunction(name);
        if (decl == null) {
            String before = getSource() != null ?
                io.github.snaill.ast.SourceBuilder.toSourceLine(getSource(), getLine(), getCharPosition(), name.length()) :
                io.github.snaill.ast.SourceBuilder.toSourceCode(this);
            throw new io.github.snaill.exception.FailedCheckException(
                new io.github.snaill.result.CompilationError(
                    io.github.snaill.result.ErrorType.UNKNOWN_VARIABLE,
                    before,
                    "Unknown function: " + name,
                    ""
                ).toString()
            );
        }
        return decl.getReturnType();
    }
}
