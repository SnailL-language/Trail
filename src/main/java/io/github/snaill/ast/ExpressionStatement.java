package io.github.snaill.ast;

import java.util.List;

/**
 * Обёртка для выражения, чтобы оно стало Statement (выражение-стейтмент).
 */
public class ExpressionStatement extends AbstractNode implements Statement {
    public ExpressionStatement(Expression expr) {
        super(List.of(expr));
    }

    public Expression getExpression() {
        return (Expression) getChild(0);
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        // Можно добавить visit(ExpressionStatement), если нужно
        Expression expr = getExpression();
        // System.out.println("[EXPR_STMT_ACCEPT] Contained expression type: " + (expr != null ? expr.getClass().getName() : "null") + ", value: " + (expr != null ? expr.toString() : "null"));
        return expr.accept(visitor);
    }

    @Override
    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context) throws java.io.IOException, io.github.snaill.exception.FailedCheckException {
        emitBytecode(out, context, null);
    }

    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context, FunctionDeclaration currentFunction) throws java.io.IOException, io.github.snaill.exception.FailedCheckException {
        Expression expr = getExpression();
        expr.emitBytecode(out, context, currentFunction);
        
        // Не добавляем POP после выражений, которые сами не оставляют значение на стеке
        if (!(expr instanceof AssignmentExpression && ((AssignmentExpression)expr).getLeft() instanceof ArrayElement)) {
            out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.POP); // Удаляем результат выражения из стека
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ExpressionStatement other) {
            return getExpression().equals(other.getExpression());
        }
        return false;
    }

    @Override
    public void check(Scope scope) throws io.github.snaill.exception.FailedCheckException {
        getExpression().check(scope);
    }
} 