package io.github.snaill.ast;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class Identifier extends PrimaryExpression {
    final private String name;

    public Identifier(String name) {
        super(List.of());
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

    @Override
    public void checkUnusedVariables(Set<VariableDeclaration> unused) {
        unused.removeIf(v -> v.getName().equals(name));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Identifier other) {
            return name.equals(other.name) && super.equals(other);
        }
        return false;
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
            throw new io.github.snaill.exception.FailedCheckException(
                new io.github.snaill.result.CompilationError(
                    io.github.snaill.result.ErrorType.UNKNOWN_VARIABLE,
                    before,
                    "Variable not found: " + getName(),
                    ""
                ).toString()
            );
        }
    }

    @Override
    public Type getType(Scope scope) throws io.github.snaill.exception.FailedCheckException {
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
            throw new io.github.snaill.exception.FailedCheckException(
                new io.github.snaill.result.CompilationError(
                    io.github.snaill.result.ErrorType.UNKNOWN_VARIABLE,
                    before,
                    "Unknown variable: " + name,
                    ""
                ).toString()
            );
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
