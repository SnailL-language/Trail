package io.github.snaill.ast;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import io.github.snaill.exception.FailedCheckException;

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
        return visitor.visit(this);
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
        Scope enclosingScope = getEnclosingScope();
        if (enclosingScope != null) {
            // Attempt to resolve the function declaration
            FunctionDeclaration resolvedDecl = enclosingScope.resolveFunction(this.name);
            if (resolvedDecl != null) {
                unused.remove(resolvedDecl); // Remove the specific instance
            }
            // Note: If resolveFunction returns null, it means the function is not found in the current scope hierarchy.
            // This would be an 'Unknown function' error, caught by the semantic checker (getType method or similar checks).
            // For the purpose of 'unused' tracking, if we can't resolve it, we can't mark it as used from here.
        }
        // Important: Also check arguments for any function calls they might contain.
        super.checkUnusedFunctions(unused);
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
                throw new FailedCheckException(
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
    public Type getType(Scope scope) throws FailedCheckException {
        // Handle built-in println specially
        if (getName().equals("println")) {
            if (getArguments().size() != 1) { // println expects one argument
                String before = getSource() != null ?
                    io.github.snaill.ast.SourceBuilder.toSourceLine(getSource(), getLine(), getCharPosition(), getName().length()) :
                    io.github.snaill.ast.SourceBuilder.toSourceCode(this);
                throw new FailedCheckException(
                    new io.github.snaill.result.CompilationError(
                        io.github.snaill.result.ErrorType.TYPE_MISMATCH, // Or a more specific error like ARGUMENT_COUNT_MISMATCH
                        before,
                        "println expects 1 argument, but got " + getArguments().size(),
                        ""
                    ).toString()
                );
            }
            // Type-check the argument. This will throw if the argument itself has an issue (e.g., unknown variable)
            Expression arg = getArguments().getFirst();
            Type argType = arg.getType(scope); // This is the crucial part - uses the correct scope

            // Check if the argument type is printable
            if (argType instanceof PrimitiveType primitiveArgType) {
                String typeName = primitiveArgType.getName();
                if (!("i32".equals(typeName) || "string".equals(typeName) || "bool".equals(typeName))) {
                    String before = getSource() != null ?
                        io.github.snaill.ast.SourceBuilder.toSourceLine(getSource(), getLine(), getCharPosition(), getName().length()) :
                        io.github.snaill.ast.SourceBuilder.toSourceCode(this);
                    throw new FailedCheckException(
                        new io.github.snaill.result.CompilationError(
                            io.github.snaill.result.ErrorType.TYPE_MISMATCH,
                            before,
                            "println argument must be i32, string, or bool, but got " + typeName,
                            ""
                        ).toString()
                    );
                }
            } else {
                // If it's not a PrimitiveType, it's not printable by println's current definition
                 String before = getSource() != null ?
                    io.github.snaill.ast.SourceBuilder.toSourceLine(getSource(), getLine(), getCharPosition(), getName().length()) :
                    io.github.snaill.ast.SourceBuilder.toSourceCode(this);
                 throw new FailedCheckException(
                    new io.github.snaill.result.CompilationError(
                        io.github.snaill.result.ErrorType.TYPE_MISMATCH,
                        before,
                        "println argument must be a primitive type (i32, string, or bool), but got " + argType.getClass().getSimpleName(),
                        ""
                    ).toString()
                );
            }

            return new PrimitiveType("void"); // println effectively returns void
        }

        // Existing logic for user-defined functions
        FunctionDeclaration decl = scope.resolveFunction(name);
        if (decl == null) {
            String before = getSource() != null ?
                io.github.snaill.ast.SourceBuilder.toSourceLine(getSource(), getLine(), getCharPosition(), name.length()) :
                io.github.snaill.ast.SourceBuilder.toSourceCode(this);
            throw new FailedCheckException(
                new io.github.snaill.result.CompilationError(
                    io.github.snaill.result.ErrorType.TYPE_MISMATCH, // Corrected ErrorType
                    before,
                    "Unknown function: " + name,
                    ""
                ).toString()
            );
        }
        // Also, check argument count and types against declaration for user-defined functions
        List<Expression> callArgs = getArguments();
        List<Parameter> funcParams = decl.getParameters();
        if (callArgs.size() != funcParams.size()) {
            String before = getSource() != null ?
                io.github.snaill.ast.SourceBuilder.toSourceLine(getSource(), getLine(), getCharPosition(), name.length()) :
                io.github.snaill.ast.SourceBuilder.toSourceCode(this);
            throw new FailedCheckException(
                new io.github.snaill.result.CompilationError(
                    io.github.snaill.result.ErrorType.TYPE_MISMATCH,
                    before,
                    "Function '" + name + "' expects " + funcParams.size() + " arguments, but got " + callArgs.size(),
                    ""
                ).toString()
            );
        }

        for (int i = 0; i < callArgs.size(); i++) {
            Type argType = callArgs.get(i).getType(scope); // Use current scope
            Type paramType = funcParams.get(i).getType();
            if (!argType.equals(paramType)) {
                 String before = callArgs.get(i).getSource() != null ?
                    io.github.snaill.ast.SourceBuilder.toSourceLine(callArgs.get(i).getSource(), callArgs.get(i).getLine(), callArgs.get(i).getCharPosition(), io.github.snaill.ast.SourceBuilder.toSourceCode(callArgs.get(i)).length()) :
                    io.github.snaill.ast.SourceBuilder.toSourceCode(callArgs.get(i));
                throw new FailedCheckException(
                    new io.github.snaill.result.CompilationError(
                        io.github.snaill.result.ErrorType.TYPE_MISMATCH,
                        before,
                        "Type mismatch for argument " + (i + 1) + " of function '" + name + "'. Expected " + paramType + ", got " + argType,
                        ""
                    ).toString()
                );
            }
        }

        return decl.getReturnType();
    }
}
