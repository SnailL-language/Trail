package io.github.snaill.ast;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import io.github.snaill.exception.FailedCheckException;
import io.github.snaill.bytecode.BytecodeConstants;
import io.github.snaill.bytecode.BytecodeUtils;
import io.github.snaill.result.CompilationError;
import io.github.snaill.result.ErrorType;

public class Identifier extends PrimaryExpression {
    final private String name;

    public Identifier(String name) {
        super(List.of());
        this.name = name;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public String getName() {
        return name;
    }

    @Override
    public void checkUnusedVariables(Set<VariableDeclaration> unused) {
        if (this.getEnclosingScope() == null) {
            LOGGER.warn("DEBUG_UNUSED: Identifier '{}' (Hash: {}) has no enclosing scope during checkUnusedVariables. Source: {}", this.getName(), System.identityHashCode(this), this.getSourceInfo()); // Cannot resolve without a scope
            return; // Cannot resolve without a scope
        }
        LOGGER.debug("DEBUG_UNUSED: Identifier '{}' checking unused. Current unused set: {}", name, unused);
        VariableDeclaration resolvedDecl = this.getEnclosingScope().resolveVariable(this.name, this);
        LOGGER.debug("DEBUG_UNUSED: Identifier '{}' resolved to: {}. (Hash: {})", name, resolvedDecl, resolvedDecl != null ? resolvedDecl.hashCode() : "null");

        if (resolvedDecl != null) {
            LOGGER.debug("DEBUG_UNUSED: Resolved variable for identifier '{}': {}. Removing from unused set.", name, resolvedDecl);
            boolean removed = unused.remove(resolvedDecl);
            LOGGER.debug("DEBUG_UNUSED: Identifier '{}' removal status: {}. Unused set after removal: {}", name, removed, unused);
        } else {
            LOGGER.warn("DEBUG_UNUSED: Identifier '{}' could not resolve variable declaration.", name);
        }
        // Identifiers typically don't have children that would declare/reference other variables,
        // but calling super is good practice if the class hierarchy changes.
        super.checkUnusedVariables(unused);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Identifier other) {
            return name.equals(other.name) && super.equals(other);
        }
        return false;
    }

    @Override
    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context) throws java.io.IOException, FailedCheckException {
        emitBytecode(out, context, null);
    }

    @Override
    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context, FunctionDeclaration currentFunction) throws java.io.IOException, FailedCheckException {
        if (currentFunction != null) {
            int localIndex = context.getLocalVarIndex(currentFunction, getName());
            if (localIndex != -1) {
                out.write(BytecodeConstants.Opcode.PUSH_LOCAL);
                BytecodeUtils.writeU16(out, localIndex);
                return;
            }
        }
        int globalIndex = context.getGlobalVarIndex(getName());
        if (globalIndex != -1) {
            out.write(BytecodeConstants.Opcode.PUSH_GLOBAL);
            BytecodeUtils.writeU16(out, globalIndex);
        } else {
            String before = getSource() != null ?
                io.github.snaill.ast.SourceBuilder.toSourceLine(getSource(), getLine(), getCharPosition(), getName().length()) :
                io.github.snaill.ast.SourceBuilder.toSourceCode(this);
            throw new FailedCheckException(
                new CompilationError(
                    ErrorType.UNKNOWN_VARIABLE,
                    before,
                    "Variable not found: " + getName(),
                    ""
                ).toString()
            );
        }
    }

    @Override
    public Type getType(Scope scope) throws FailedCheckException {
        // Записываем отладочное сообщение в файл
        try {
            java.io.PrintWriter debugLog = new java.io.PrintWriter(new java.io.FileWriter("debug_identifier_log.txt", true));
            debugLog.println("Looking for variable in check phase: " + name);
            debugLog.close();
        } catch (java.io.IOException e) {
            // Игнорируем ошибку записи
        }
        // Добавляем отладочный вывод в stderr
        System.err.println("DEBUG: Checking variable in getType: " + name);
        // Проверяем, не обращаемся ли к переменной в её собственной инициализации
        VariableDeclaration decl = scope.resolveVariable(name, this);
        if (decl == null) {
            String before = getSource() != null ?
                io.github.snaill.ast.SourceBuilder.toSourceLine(getSource(), getLine(), getCharPosition(), name.length()) :
                io.github.snaill.ast.SourceBuilder.toSourceCode(this);
            throw new FailedCheckException(
                new CompilationError(
                    ErrorType.UNKNOWN_VARIABLE,
                    before,
                    "Unknown variable: " + name,
                    ""
                ).toString()
            );
        }
        return decl.getType();
    }

}
