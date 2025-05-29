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
        try {
            return visitor.visit(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context, FunctionDeclaration currentFunction) throws java.io.IOException, io.github.snaill.exception.FailedCheckException {
        getLeft().emitBytecode(out, context, currentFunction);
        getRight().emitBytecode(out, context, currentFunction);
        out.write(getOpcodeForOperator());
    }
    
    private byte getOpcodeForOperator() throws io.github.snaill.exception.FailedCheckException {
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
            case "&&" -> io.github.snaill.bytecode.BytecodeConstants.Opcode.AND;
            case "||" -> io.github.snaill.bytecode.BytecodeConstants.Opcode.OR;
            default -> {
                String before = getSource() != null ?
                    io.github.snaill.ast.SourceBuilder.toSourceLine(getSource(), getLine(), getCharPosition(), operator.length()) :
                    io.github.snaill.ast.SourceBuilder.toSourceCode(this);
                throw new io.github.snaill.exception.FailedCheckException(
                    new io.github.snaill.result.CompilationError(
                        io.github.snaill.result.ErrorType.UNKNOWN_OPERATOR,
                        before,
                        "Unknown operator: " + operator,
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
