package io.github.snaill.ast;

import java.util.List;

/**
 * Represents a binary expression in the AST.
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
     * Generates bytecode for the binary expression.
     * Implements short-circuit evaluation for logical AND and OR operators.
     */
    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context, FunctionDeclaration currentFunction) throws java.io.IOException, io.github.snaill.exception.FailedCheckException {
        // Debug information for bytecode generation of binary expression
        // System.out.println("DEBUG: Generating bytecode for binary expression: " + getLeft() + " " + getOperator() + " " + getRight());
        // For logical AND and OR operators, use implementation with short-circuit evaluation
        switch (operator) {
            case "&&" -> {
                // Logical AND (&&) with short-circuit evaluation
                // 1. Evaluate the left operand
                getLeft().emitBytecode(out, context, currentFunction);

                // Duplicate it so that after JMP_IF_FALSE consumes one copy, another remains on the stack
                out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.DUP);

                // Write JMP_IF_FALSE and remember position for jump offset
                out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.JMP_IF_FALSE);
                int jumpPosIfFalse = out.size();
                io.github.snaill.bytecode.BytecodeUtils.writeU16(out, 0); // Temporary 0, update later
                
                // 2. If the left operand is true, discard it and evaluate the right operand
                out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.POP); // удалить левый операнд со стека
                getRight().emitBytecode(out, context, currentFunction); // Вычисляем правый операнд
                
                // 3. Target destination for JMP_IF_FALSE (when left operand is false)
                int endPos = out.size();
                
                // 4. Update the offset for JMP_IF_FALSE
                // We need the jump size from the position after JMP_IF_FALSE + 2 bytes to endPos
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
                // Logical OR (||) with short-circuit evaluation
                // 1. Вычисляем левый операнд
                getLeft().emitBytecode(out, context, currentFunction);

                // Дублируем, чтобы значение осталось на стеке после того, как JMP_IF_TRUE его потребит
                out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.DUP);

                // Сохраняем позицию для вставки JMP_IF_TRUE
                out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.JMP_IF_TRUE);
                int jumpPosIfTrue = out.size();
                io.github.snaill.bytecode.BytecodeUtils.writeU16(out, 0); // Temporary 0, update later

                // 2. If the left operand is false, discard it and evaluate the right operand
                out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.POP); // удалить левый операнд со стека
                getRight().emitBytecode(out, context, currentFunction); // Evaluate the right operand

                // 3. After evaluating the right operand, we end up here
                int endPos = out.size();
                
                // 4. Update the offset for JMP_IF_TRUE
                // We need the jump size from the position after JMP_IF_TRUE + 2 bytes to endPos
                int jumpSize = endPos - jumpPosIfTrue - 2;
                
                // Update the jump position in the bytecode
                byte[] code = out.toByteArray();
                io.github.snaill.bytecode.BytecodeUtils.writeU16(new java.io.ByteArrayOutputStream() {
                    @Override
                    public void write(int b) {
                        code[jumpPosIfTrue + (count++)] = (byte) b;
                    }
                    private int count = 0;
                }, jumpSize);
                
                // Write the updated bytecode
                out.reset();
                out.write(code);
            }
            case "==", "!=", "<", "<=", ">", ">=", "+", "-", "*", "/", "%" -> {
                // Arithmetic and comparison operations
                getLeft().emitBytecode(out, context, currentFunction);
                getRight().emitBytecode(out, context, currentFunction);
                out.write(getOpcodeForOperator());
            }
            default -> {
                // Unknown operator
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

        // 1. Handle comparison and logical operators: they always result in bool if types are compatible.
        if (operator.equals("&&") || operator.equals("||") || operator.equals("==") || operator.equals("!=") ||
            operator.equals("<") || operator.equals("<=") || operator.equals(">") || operator.equals(">=")) {
            // Type compatibility for comparison/logical ops is typically checked by the Check visitor or later in this method.
            // For now, if it's a comparison/logical operator, the intended result type is bool.
            // We still need to ensure leftType and rightType are compatible for the operation.
            if (leftType instanceof PrimitiveType lt && rightType instanceof PrimitiveType rt) {
                // Basic check: allow comparison if types are the same, or one is usize and other is compatible literal for comparison
                boolean usizeLiteralMix = (lt.getName().equals("usize") && getRight() instanceof NumberLiteral nl && nl.isNonNegative()) ||
                                          (rt.getName().equals("usize") && getLeft() instanceof NumberLiteral nl2 && nl2.isNonNegative());
                boolean i32UsizeMix = (lt.getName().equals("i32") && rt.getName().equals("usize")) || 
                                      (lt.getName().equals("usize") && rt.getName().equals("i32"));

                if (lt.getName().equals(rt.getName()) || usizeLiteralMix || i32UsizeMix) {
                     // Further checks in Check.visit(BinaryExpression) will ensure numeric types for <, <=, >, >=
                     // and compatible types for ==, !=, &&, ||.
                    return new PrimitiveType("bool");
                }
            }
            // If not primitive types or not compatible as per above, fall through to general type mismatch error.
        }

        // 2. Handle arithmetic operators with specific usize/literal and i32/usize promotion.
        if (leftType instanceof PrimitiveType lt && rightType instanceof PrimitiveType rt) {
            String lName = lt.getName();
            String rName = rt.getName();

            if (isArithmeticOperator()) {
                if ((lName.equals("usize") && getRight() instanceof NumberLiteral nl && nl.isNonNegative()) ||
                    (rName.equals("usize") && getLeft() instanceof NumberLiteral nl2 && nl2.isNonNegative())) {
                    return new PrimitiveType("usize");
                }
                if ((lName.equals("i32") && rName.equals("usize")) || (lName.equals("usize") && rName.equals("i32"))) {
                     // Arithmetic between i32 and usize could result in i32 if we allow implicit conversion,
                     // or be an error, or result in usize if usize is considered larger.
                     // For now, let's assume it might be intended to be i32 or requires explicit cast.
                     // This part of logic might need refinement based on language spec for i32/usize arithmetic.
                     // Let's default to i32 for now if mixed, or stick to the common type if they are the same.
                    return new PrimitiveType("i32"); // Or handle as error / require cast
                }
                if (lName.equals(rName)) { // Both are same type (e.g., i32 + i32, usize + usize)
                    return lt; // Result is of the same type
                }
            }

            // All primitives can be added like strings
            if (operator.equals("+")) {
                if (lName.equals("string")) {
                    return lt;
                }
                if (rName.equals("string")) {
                    return rt;
                }
            }
        }

        // 3. General type mismatch error if no specific rule applied or types are incompatible.
        // This check is crucial and covers cases not caught by the specific logic above.
        if (!leftType.equals(rightType)) {
             // A more nuanced check might be needed if we allow implicit conversions for some ops
             // For example, if i32 and usize were not handled above for an arithmetic op, this would catch it.
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
        
        // If types are equal and it's an arithmetic op not caught by usize/literal or i32/usize logic:
        // e.g. string + string (if allowed), or other future types
        isArithmeticOperator();

        // Fallback, should ideally be covered by specific logic or error out.
        // This might be reached if operator is not comparison/logical and not arithmetic, or types are equal but not primitive.
        // For safety, if types are equal, return leftType. Otherwise, it's an unhandled case.
        // Consider if this path is truly reachable or indicates a logic gap.
        return leftType; 
    }

    private boolean isArithmeticOperator() {
        return operator.equals("+") || operator.equals("-") || operator.equals("*") || operator.equals("/") || operator.equals("%");
    }
}
