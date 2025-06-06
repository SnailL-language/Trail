package io.github.snaill.ast;

import java.io.IOException;
import java.util.List;

/**
 * Представляет бинарное выражение в AST.
 */
public class BinaryExpression extends Expression {
    private final String operator;

    public BinaryExpression(Expression left, String operator, Expression right) {
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
    public boolean equals(Object obj) {
        if (obj instanceof BinaryExpression other) {
            return operator.equals(other.operator)
                && super.equals(other);
        }
        return false;
    }

    @Override
    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context) throws java.io.IOException, io.github.snaill.exception.FailedCheckException {
        emitBytecode(out, context, null);
    }

    /**
     * Генерирует байткод для бинарного выражения.
     * Для логических операторов AND и OR реализует короткое вычисление (short-circuit evaluation).
     */
    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context, FunctionDeclaration currentFunction) throws java.io.IOException, io.github.snaill.exception.FailedCheckException {
        // Отладочная информация о генерации байткода для бинарного выражения
        //System.out.println("DEBUG: Генерация байткода для бинарного выражения: " + getLeft() + " " + getOperator() + " " + getRight());
        // Для логических операторов AND и OR используем реализацию с коротким вычислением
        switch (operator) {
            case "&&" -> {
                // Логическое И (&&) с коротким вычислением
                // 1. Вычисляем левый операнд
                getLeft().emitBytecode(out, context, currentFunction);
                
                // Сохраняем позицию для вставки JMP_IF_FALSE
                out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.JMP_IF_FALSE);
                int jumpPosIfFalse = out.size();
                io.github.snaill.bytecode.BytecodeUtils.writeU16(out, 0); // Временный 0, обновим позже
                
                // 2. Если левый операнд true, удаляем его и вычисляем правый операнд
                out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.POP); // Удаляем результат левого операнда
                getRight().emitBytecode(out, context, currentFunction); // Вычисляем правый операнд
                
                // 3. Точка назначения для JMP_IF_FALSE (когда левый операнд false)
                int endPos = out.size();
                
                // 4. Обновляем смещение для JMP_IF_FALSE
                // Нам нужен размер прыжка от позиции после JMP_IF_FALSE + 2 байта до endPos
                int jumpSize = endPos - jumpPosIfFalse - 2;
                
                // Обновляем позицию прыжка в байт-коде
                byte[] code = out.toByteArray();
                io.github.snaill.bytecode.BytecodeUtils.writeU16(new java.io.ByteArrayOutputStream() {
                    @Override
                    public void write(int b) {
                        code[jumpPosIfFalse + (count++)] = (byte) b;
                    }
                    private int count = 0;
                }, jumpSize);
                
                // Записываем обновленный байткод
                out.reset();
                out.write(code);
            }
            case "||" -> {
                // Логическое ИЛИ (||) с коротким вычислением
                // 1. Вычисляем левый операнд
                getLeft().emitBytecode(out, context, currentFunction);

                // Сохраняем позицию для вставки JMP_IF_TRUE
                out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.JMP_IF_TRUE);
                int jumpPosIfTrue = out.size();
                io.github.snaill.bytecode.BytecodeUtils.writeU16(out, 0); // Временный 0, обновим позже

                // 2. Если левый операнд false, удаляем его и вычисляем правый операнд
                out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.POP); // Удаляем результат левого операнда
                getRight().emitBytecode(out, context, currentFunction); // Вычисляем правый операнд

                // 3. После вычисления правого операнда мы попадаем сюда
                int endPos = out.size();
                
                // 4. Обновляем смещение для JMP_IF_TRUE
                // Нам нужен размер прыжка от позиции после JMP_IF_TRUE + 2 байта до endPos
                int jumpSize = endPos - jumpPosIfTrue - 2;
                
                // Обновляем позицию прыжка в байт-коде
                byte[] code = out.toByteArray();
                io.github.snaill.bytecode.BytecodeUtils.writeU16(new java.io.ByteArrayOutputStream() {
                    @Override
                    public void write(int b) {
                        code[jumpPosIfTrue + (count++)] = (byte) b;
                    }
                    private int count = 0;
                }, jumpSize);
                
                // Записываем обновленный байткод
                out.reset();
                out.write(code);
            }
            case "==", "!=", "<", "<=", ">", ">=", "+", "-", "*", "/", "%" -> {
                // Для арифметических и операторов сравнения
                getLeft().emitBytecode(out, context, currentFunction);
                getRight().emitBytecode(out, context, currentFunction);
                out.write(getOpcodeForOperator());
            }
            default -> {
                // Неизвестный оператор
                String before = getSource() != null ?
                        SourceBuilder.toSourceLine(getSource(), getLine(), getCharPosition(), operator.length()) :
                        SourceBuilder.toSourceCode(this);
                throw new io.github.snaill.exception.FailedCheckException(
                        new io.github.snaill.result.CompilationError(
                                io.github.snaill.result.ErrorType.UNKNOWN_OPERATOR,
                                before,
                                "Unknown operator: " + operator,
                                ""
                        ).toString()
                );
            }
        }
    }
    
    private byte getOpcodeForOperator() throws io.github.snaill.exception.FailedCheckException {
        // Если это логический оператор, то он обрабатывается отдельно в emitBytecode
        if (operator.equals("&&") || operator.equals("||")) {
            throw new io.github.snaill.exception.FailedCheckException(
                new io.github.snaill.result.CompilationError(
                    io.github.snaill.result.ErrorType.UNKNOWN_OPERATOR,
                    "",
                    "Логические операторы обрабатываются отдельно: " + operator,
                    ""
                ).toString()
            );
        }
        
        // Для всех остальных операторов возвращаем соответствующий опкод
        return switch (operator) {
            case "+" -> io.github.snaill.bytecode.BytecodeConstants.Opcode.ADD;
            case "-" -> io.github.snaill.bytecode.BytecodeConstants.Opcode.SUB;
            case "*" -> io.github.snaill.bytecode.BytecodeConstants.Opcode.MUL;
            case "/" -> io.github.snaill.bytecode.BytecodeConstants.Opcode.DIV;
            case "%" -> io.github.snaill.bytecode.BytecodeConstants.Opcode.MOD;
            case "==" -> io.github.snaill.bytecode.BytecodeConstants.Opcode.EQ;
            case "!=" -> io.github.snaill.bytecode.BytecodeConstants.Opcode.NEQ;
            case "<" -> io.github.snaill.bytecode.BytecodeConstants.Opcode.LT;
            case "<=" -> io.github.snaill.bytecode.BytecodeConstants.Opcode.LTE;
            case ">" -> io.github.snaill.bytecode.BytecodeConstants.Opcode.GT;
            case ">=" -> io.github.snaill.bytecode.BytecodeConstants.Opcode.GTE;
            default -> {
                String before = getSource() != null ?
                        io.github.snaill.ast.SourceBuilder.toSourceLine(getSource(), getLine(), getCharPosition(), operator.length()) :
                        io.github.snaill.ast.SourceBuilder.toSourceCode(this);
                throw new io.github.snaill.exception.FailedCheckException(
                    new io.github.snaill.result.CompilationError(
                        io.github.snaill.result.ErrorType.UNKNOWN_OPERATOR,
                        before,
                        "Неизвестный оператор: " + operator,
                        ""
                    ).toString()
                );
            }
        };
    }

    @Override
    public Type getType(Scope scope) throws io.github.snaill.exception.FailedCheckException {
        Type leftType = getLeft().getType(scope);
        Type rightType = getRight().getType(scope);
        // Если один из операндов — usize, а второй — неотрицательный литерал, считаем оба usize
        if (leftType instanceof PrimitiveType lt && rightType instanceof PrimitiveType rt) {
            String l = lt.getName();
            String r = rt.getName();
            if ((l.equals("usize") && getRight() instanceof NumberLiteral nl && nl.isNonNegative()) ||
                (r.equals("usize") && getLeft() instanceof NumberLiteral nl2 && nl2.isNonNegative())) {
                return new PrimitiveType("usize");
            }
            if ((l.equals("i32") && r.equals("usize")) || (l.equals("usize") && r.equals("i32"))) {
                return new PrimitiveType("i32");
            }
            if (l.equals(r)) {
                // Для логических операторов результат всегда bool
                if (operator.equals("&&") || operator.equals("||") || operator.equals("==") || operator.equals("!=") ||
                    operator.equals("<") || operator.equals("<=") || operator.equals(">") || operator.equals(">=")) {
                    return new PrimitiveType("bool");
                }
                return lt;
            }
        }
        if (!leftType.equals(rightType)) {
            String before = getSource() != null ?
                io.github.snaill.ast.SourceBuilder.toSourceLine(getSource(), getLine(), getCharPosition(), toString().length()) :
                io.github.snaill.ast.SourceBuilder.toSourceCode(this);
            throw new io.github.snaill.exception.FailedCheckException(
                new io.github.snaill.result.CompilationError(
                    io.github.snaill.result.ErrorType.TYPE_MISMATCH,
                    before,
                    "Type mismatch in binary expression: cannot apply '" + operator + "' to " + leftType + " and " + rightType,
                    ""
                ).toString()
            );
        }
        // Для логических операторов результат всегда bool
        if (operator.equals("&&") || operator.equals("||") || operator.equals("==") || operator.equals("!=") ||
            operator.equals("<") || operator.equals("<=") || operator.equals(">") || operator.equals(">=")) {
            return new PrimitiveType("bool");
        }
        return leftType;
    }
}
