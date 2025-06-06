package io.github.snaill.ast;

import java.io.IOException;
import java.util.List;

/**
 * Представляет унарное выражение в AST.
 */
public class UnaryExpression extends Expression {
    private final String operator;

    public UnaryExpression(String operator, Expression argument) {
        super(List.of(argument));
        this.operator = operator;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public Expression getArgument() {
        return (Expression) children.getFirst();
    }

    public String getOperator() {
        return operator;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UnaryExpression other) {
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
        getArgument().emitBytecode(out, context, currentFunction);
        if (getOperator().equals("-")) {
            out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.SUB);
        } else if (getOperator().equals("!")) {
            out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.NOT);
        } else {
            throw new io.github.snaill.exception.FailedCheckException("Unsupported unary operator: " + getOperator());
        }
    }

    @Override
    public Type getType(Scope scope) throws io.github.snaill.exception.FailedCheckException {
        Type argType = getArgument().getType(scope);
        return switch (operator) {
            case "!" -> {
                if (!(argType instanceof PrimitiveType pt) || !pt.getName().equals("bool")) {
                    String before = getSource() != null ?
                        io.github.snaill.ast.SourceBuilder.toSourceLine(getSource(), getLine(), getCharPosition(), operator.length()) :
                        io.github.snaill.ast.SourceBuilder.toSourceCode(this);
                    throw new io.github.snaill.exception.FailedCheckException(
                        new io.github.snaill.result.CompilationError(
                            io.github.snaill.result.ErrorType.TYPE_MISMATCH,
                            before,
                            "Operator '!' can only be applied to bool type, got " + argType,
                            ""
                        ).toString()
                    );
                }
                yield new PrimitiveType("bool");
            }
            case "-" -> {
                if (!(argType instanceof PrimitiveType pt) || (!pt.getName().equals("i32") && !pt.getName().equals("usize"))) {
                    String before = getSource() != null ?
                        io.github.snaill.ast.SourceBuilder.toSourceLine(getSource(), getLine(), getCharPosition(), operator.length()) :
                        io.github.snaill.ast.SourceBuilder.toSourceCode(this);
                    throw new io.github.snaill.exception.FailedCheckException(
                        new io.github.snaill.result.CompilationError(
                            io.github.snaill.result.ErrorType.TYPE_MISMATCH,
                            before,
                            "Operator '-' can only be applied to i32 or usize type, got " + argType,
                            ""
                        ).toString()
                    );
                }
                yield new PrimitiveType("i32");
            }
            default -> throw new io.github.snaill.exception.FailedCheckException(
                new io.github.snaill.result.CompilationError(
                    io.github.snaill.result.ErrorType.UNKNOWN_OPERATOR,
                    io.github.snaill.ast.SourceBuilder.toSourceCode(this),
                    "Unknown unary operator: " + operator,
                    ""
                ).toString()
            );
        };
    }
}
