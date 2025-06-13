package io.github.snaill.ast;

import io.github.snaill.result.ErrorType;

import java.util.List;

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
    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context) throws java.io.IOException, io.github.snaill.exception.FailedCheckException {
        emitBytecode(out, context, null);
    }

    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context, FunctionDeclaration currentFunction) throws java.io.IOException, io.github.snaill.exception.FailedCheckException {
        Expression left = getLeft();
        Expression right = getRight();

        if (left instanceof Identifier id) {
            // Если это составной оператор, сначала загружаем текущее значение переменной
            if (!"=".equals(operator)) {
                int localIndex = -1;
                if (currentFunction != null) {
                    localIndex = context.getLocalVarIndex(currentFunction, id.getName());
                }
                if (localIndex != -1) {
                    out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.PUSH_LOCAL);
                    io.github.snaill.bytecode.BytecodeUtils.writeU16(out, localIndex);
                } else {
                    int globalIndex = context.getGlobalVarIndex(id.getName());
                    if (globalIndex != -1) {
                        out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.PUSH_GLOBAL);
                        io.github.snaill.bytecode.BytecodeUtils.writeU16(out, globalIndex);
                    } else {
                        throw new io.github.snaill.exception.FailedCheckException(
                            new io.github.snaill.result.CompilationError(
                                io.github.snaill.result.ErrorType.UNKNOWN_VARIABLE,
                                io.github.snaill.ast.SourceBuilder.toSourceCode(this),
                                "Variable not found: " + id.getName(),
                                ""
                            ).toString()
                        );
                    }
                }
            }

            // Генерируем байткод для правой части
            right.emitBytecode(out, context, currentFunction);

            // Если это составной оператор, выполняем соответствующую операцию
            if (!"=".equals(operator)) {
                switch (operator) {
                    case "+=":
                        out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.ADD);
                        break;
                    case "-=":
                        out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.SUB);
                        break;
                    case "*=":
                        out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.MUL);
                        break;
                    case "/=":
                        out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.DIV);
                        break;
                }
            }

            // Сохраняем результат
            int localIndex = -1;
            if (currentFunction != null) {
                localIndex = context.getLocalVarIndex(currentFunction, id.getName());
            }
            if (localIndex != -1) {
                out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.STORE_LOCAL);
                io.github.snaill.bytecode.BytecodeUtils.writeU16(out, localIndex);
            } else {
                int globalIndex = context.getGlobalVarIndex(id.getName());
                if (globalIndex != -1) {
                    out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.STORE_GLOBAL);
                    io.github.snaill.bytecode.BytecodeUtils.writeU16(out, globalIndex);
                } else {
                     String before = getSource() != null ?
                        io.github.snaill.ast.SourceBuilder.toSourceLine(getSource(), getLine(), getCharPosition(), id.getName().length()) :
                        io.github.snaill.ast.SourceBuilder.toSourceCode(this);
                    throw new io.github.snaill.exception.FailedCheckException(
                        new io.github.snaill.result.CompilationError(
                            io.github.snaill.result.ErrorType.UNKNOWN_VARIABLE,
                            before,
                            "Variable not found: " + id.getName(),
                            ""
                        ).toString()
                    );
                }
            }
        } else if (left instanceof ArrayElement ae) {
            // Для составных присваиваний элементам массива требуется более сложная логика (например, DUP), 
            // которая на данный момент не поддерживается. Реализуем только для простого присваивания.
            if (!"=".equals(operator)) {
                throw new io.github.snaill.exception.FailedCheckException(
                    new io.github.snaill.result.CompilationError(
                        ErrorType.UNKNOWN_OPERATOR,
                        io.github.snaill.ast.SourceBuilder.toSourceCode(this),
                        "Compound assignment for array elements is not yet supported.",
                        ""
                    ).toString()
                );
            }
            
            // Простое присваивание для массивов
            ae.getIdentifier().emitBytecode(out, context, currentFunction); // Загружаем ссылку на массив
            for (Expression dim : ae.getDims()) {
                dim.emitBytecode(out, context, currentFunction); // Загружаем индекс
            }
            right.emitBytecode(out, context, currentFunction); // Загружаем значение для сохранения
            out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.SET_ARRAY);
        } else {
            String before = getSource() != null ?
                io.github.snaill.ast.SourceBuilder.toSourceLine(getSource(), getLine(), getCharPosition(), 1) :
                io.github.snaill.ast.SourceBuilder.toSourceCode(this);
            throw new io.github.snaill.exception.FailedCheckException(
                new io.github.snaill.result.CompilationError(
                    io.github.snaill.result.ErrorType.INVALID_ASSIGNMENT,
                    before,
                    "Invalid left-hand side of assignment",
                    ""
                ).toString()
            );
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;
        AssignmentExpression other = (AssignmentExpression) obj;
        return java.util.Objects.equals(operator, other.operator);
    }

    @Override
    public Type getType(Scope scope) throws io.github.snaill.exception.FailedCheckException {
        Type leftType = getLeft().getType(scope);
        Type rightType = getRight().getType(scope);
        // Разрешаем присваивание неотрицательного литерала к usize
        if (leftType instanceof PrimitiveType lt && lt.getName().equals("usize") && isUsableAsUsize(getRight(), scope)) {
            return leftType;
        }
        if (!leftType.equals(rightType)) {
            String before = getSource() != null ?
                io.github.snaill.ast.SourceBuilder.toSourceLine(getSource(), getLine(), getCharPosition(), toString().length()) :
                io.github.snaill.ast.SourceBuilder.toSourceCode(this);
            throw new io.github.snaill.exception.FailedCheckException(
                new io.github.snaill.result.CompilationError(
                    io.github.snaill.result.ErrorType.TYPE_MISMATCH,
                    before,
                    "Type mismatch in assignment: cannot assign " + rightType + " to " + leftType,
                    ""
                ).toString()
            );
        }
        return leftType;
    }

    private boolean isUsableAsUsize(Expression expr, Scope scope) throws io.github.snaill.exception.FailedCheckException {
        if (expr instanceof NumberLiteral nl) {
            return nl.isNonNegative();
        }
        if (expr instanceof Identifier id) {
            Type t = id.getType(scope);
            return t instanceof PrimitiveType pt && pt.getName().equals("usize");
        }
        if (expr instanceof BinaryExpression be) {
            // Проверяем оба операнда
            return isUsableAsUsize(be.getLeft(), scope) && isUsableAsUsize(be.getRight(), scope);
        }
        // Если тип выражения уже usize — разрешаем
        try {
            Type t = expr.getType(scope);
            if (t instanceof PrimitiveType pt && pt.getName().equals("usize")) return true;
        } catch (Exception ignored) {}
        return false;
    }

    public void check(Scope scope) throws io.github.snaill.exception.FailedCheckException {
        Type leftType = getLeft().getType(scope);
        Type rightType = getRight().getType(scope);
        // Разрешаем присваивание неотрицательного литерала к usize
        if (leftType instanceof PrimitiveType lt && lt.getName().equals("usize") && isUsableAsUsize(getRight(), scope)) {
            getLeft().check(scope);
            getRight().check(scope);
            return;
        }
        if (!leftType.equals(rightType)) {
            String before = io.github.snaill.ast.SourceBuilder.toSourceLine(getSource(), getLine(), getCharPosition(), toString().length());
            throw new io.github.snaill.exception.FailedCheckException(
                new io.github.snaill.result.CompilationError(
                    io.github.snaill.result.ErrorType.TYPE_MISMATCH,
                    before,
                    "Type mismatch in assignment: cannot assign " + rightType + " to " + leftType,
                    ""
                ).toString()
            );
        }
        // Проверяем рекурсивно выражения
        getLeft().check(scope);
        getRight().check(scope);
    }
} 