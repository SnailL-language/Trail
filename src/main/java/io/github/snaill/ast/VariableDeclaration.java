package io.github.snaill.ast;

import java.util.Set;
import io.github.snaill.exception.FailedCheckException;

/**
 * Представляет объявление переменной в AST.
 * Генерирует байткод для создания и инициализации переменной.
 */
public class VariableDeclaration extends AbstractNode implements Statement {
    private final String name;
    private Scope enclosingScope;

    public VariableDeclaration(String name, Type type, Expression value) {
        super(java.util.Arrays.asList(type, value));
        this.name = name;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);

    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return (Type) children.getFirst();
    }

    public Expression getValue() {
        return (Expression) children.get(1);
    }

    @Override
    public void checkUnusedVariables(Set<VariableDeclaration> unused) {
        // no-op: VariableDeclaration не должна удалять себя из unused
    }

    public Scope getEnclosingScope() {
        return this.enclosingScope;
    }

    public void setEnclosingScope(Scope scope) {
        this.enclosingScope = scope;
    }

    public Scope getParentScope() {
        return getEnclosingScope();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof VariableDeclaration other) {
            return name.equals(other.name)
                && super.equals(other);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public void check(Scope scope) throws FailedCheckException {
        scope.addDeclaration(this); // Add this variable to the current scope
        Type declared = getType();
        Expression value = getValue();
        if (value != null) {
            Type actual = value.getType(scope);
            if (declared instanceof PrimitiveType pt && pt.getName().equals("usize") && value instanceof NumberLiteral nl && nl.isNonNegative()) {
                super.check(scope);
                return;
            }
            if (!declared.equals(actual)) {
                String before = getSource() != null ?
                    io.github.snaill.ast.SourceBuilder.toSourceLine(getSource(), getLine(), getCharPosition(), name.length()) :
                    io.github.snaill.ast.SourceBuilder.toSourceCode(this);
                throw new FailedCheckException(
                    new io.github.snaill.result.CompilationError(
                        io.github.snaill.result.ErrorType.TYPE_MISMATCH,
                        before,
                        "Type mismatch: cannot assign " + actual + " to " + declared,
                        ""
                    ).toString()
                );
            }
        }
        super.check(scope);
    }

    @Override
    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context) throws java.io.IOException, io.github.snaill.exception.FailedCheckException {
        emitBytecode(out, context, null);
    }

    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context, FunctionDeclaration currentFunction) throws java.io.IOException, io.github.snaill.exception.FailedCheckException {
        if (getValue() != null || (getValue() == null && getType() instanceof ArrayType)) {
            if (getValue() != null) {
                getValue().emitBytecode(out, context, currentFunction);
            } else {
                // Автоматическая инициализация массива нулями
                ArrayType at = (ArrayType) getType();
                int arrSize = (int) at.getSize().getValue();
                byte elemTypeId = getElemTypeId(at);

                out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.NEW_ARRAY);
                io.github.snaill.bytecode.BytecodeUtils.writeI32(out, arrSize);
                out.write(elemTypeId);
            }
            if (currentFunction != null) {
                int localIndex = context.getLocalVarIndex(currentFunction, getName());
                if (localIndex == -1) {
                    throw new io.github.snaill.exception.FailedCheckException("Local variable not found: " + getName());
                }
                out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.STORE_LOCAL);
                io.github.snaill.bytecode.BytecodeUtils.writeU16(out, localIndex);
            } else {
                int globalIndex = context.getGlobalVarIndex(getName());
                if (globalIndex == -1) {
                    // This should not happen if initializeContext in BytecodeEmitter works correctly.
                    throw new FailedCheckException("Global variable '" + getName() + "' was not registered before bytecode emission.");
                }
                out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.STORE_GLOBAL);
                io.github.snaill.bytecode.BytecodeUtils.writeU16(out, globalIndex);
            }
        }
    }

    private static byte getElemTypeId(ArrayType at) {
        Type elemType = at.getElementType();
        byte elemTypeId;
        String tn = elemType.toString();
        elemTypeId = switch (tn) {
            case "i32" -> io.github.snaill.bytecode.BytecodeConstants.TypeId.I32;
            case "usize" -> io.github.snaill.bytecode.BytecodeConstants.TypeId.USIZE;
            case "string" -> io.github.snaill.bytecode.BytecodeConstants.TypeId.STRING;
            case null, default -> io.github.snaill.bytecode.BytecodeConstants.TypeId.I32;
        };
        return elemTypeId;
    }

    @Override
    public String toString() {
        return "let " + name + ": " + getType() + (getValue() != null ? (" = " + getValue()) : "") + ";";
    }
}
