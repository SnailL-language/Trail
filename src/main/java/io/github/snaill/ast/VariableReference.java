package io.github.snaill.ast;

import java.util.List;
import java.util.Set;

/**
 * Представляет ссылку на переменную в AST.
 * Генерирует байткод для загрузки значения переменной.
 */
public class VariableReference extends Expression {
    private final String name;

    public VariableReference(String name) {
        super(List.of());
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context) throws java.io.IOException, io.github.snaill.exception.FailedCheckException {
        emitBytecode(out, context, null);
    }

    @Override
    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context, FunctionDeclaration currentFunction) throws java.io.IOException, io.github.snaill.exception.FailedCheckException {
        if (currentFunction != null) {
            int localIndex = context.getLocalVarIndex(currentFunction, getName());
            if (localIndex != -1) {
                out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.PUSH_LOCAL);
                io.github.snaill.bytecode.BytecodeUtils.writeU16(out, localIndex);
                return;
            }
        }
        int globalIndex = context.getGlobalVarIndex(getName());
        if (globalIndex != -1) {
            out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.PUSH_GLOBAL);
            io.github.snaill.bytecode.BytecodeUtils.writeU16(out, globalIndex);
        } else {
            String before = getSource() != null ?
                io.github.snaill.ast.SourceBuilder.toSourceLine(getSource(), getLine(), getCharPosition(), getName().length()) :
                io.github.snaill.ast.SourceBuilder.toSourceCode(this);
            io.github.snaill.result.CompilationError err = new io.github.snaill.result.CompilationError(
                    io.github.snaill.result.ErrorType.UNKNOWN_VARIABLE,
                    before,
                    "Variable not found: " + getName(),
                    ""
                );
            throw new io.github.snaill.exception.FailedCheckException(java.util.List.of(err));
        }
    }

    @Override
    public void checkUnusedVariables(Set<VariableDeclaration> unused) {
        Scope enclosingScope = getEnclosingScope();
        if (enclosingScope != null) {
            VariableDeclaration resolvedDecl = enclosingScope.resolveVariable(this.name);
            if (resolvedDecl != null) {
                unused.remove(resolvedDecl);
            }
        }
        super.checkUnusedVariables(unused); // Process children, if any
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof VariableReference other) {
            return name.equals(other.name);
        }
        return false;
    }

    @Override
    public Type getType(Scope scope) throws io.github.snaill.exception.FailedCheckException {
        VariableDeclaration decl = scope.resolveVariable(name);
        if (decl == null) {
            String before = getSource() != null ?
                io.github.snaill.ast.SourceBuilder.toSourceLine(getSource(), getLine(), getCharPosition(), name.length()) :
                io.github.snaill.ast.SourceBuilder.toSourceCode(this);
            io.github.snaill.result.CompilationError err = new io.github.snaill.result.CompilationError(
                    io.github.snaill.result.ErrorType.UNKNOWN_VARIABLE,
                    before,
                    "Unknown variable: " + name,
                    ""
                );
            throw new io.github.snaill.exception.FailedCheckException(java.util.List.of(err));
        }
        return decl.getType();
    }

    // Вспомогательный метод для поиска statement-родителя
    private Node findParentStatement(Node scope, Node target) {
        for (Node child : scope.getChildren()) {
            if (child == target) return scope;
            Node found = findParentStatement(child, target);
            if (found != null) return found;
        }
        return null;
    }
} 