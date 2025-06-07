package io.github.snaill.ast;

// Scope and Type are in the same package (io.github.snaill.ast), no import needed.
import io.github.snaill.exception.FailedCheckException; // Keep: Used by getType
import java.util.List; // For List.of


public class ParenthesizedExpression extends Expression {

    // --- Начало добавленного кода ---
    @Override
    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context, FunctionDeclaration currentFunction) throws java.io.IOException, io.github.snaill.exception.FailedCheckException {
        if (this.innerExpression != null) {
            this.innerExpression.emitBytecode(out, context, currentFunction);
        } else {
            // Если innerExpression null, getType тоже вызовет исключение, так что это согласуется
            throw new io.github.snaill.exception.FailedCheckException("Inner expression of ParenthesizedExpression is null during bytecode emission");
        }
    }
    // --- Конец добавленного кода ---
    private final Expression innerExpression;

    public ParenthesizedExpression(Expression innerExpression) {
        super(innerExpression != null ? List.of(innerExpression) : List.of());
        this.innerExpression = innerExpression;
        if (innerExpression instanceof AbstractNode) {
            this.setSourceInfo(((AbstractNode) innerExpression).getLine(), ((AbstractNode) innerExpression).getCharPosition(), ((AbstractNode) innerExpression).getSource());
        } else if (innerExpression != null) {
            // Если innerExpression не AbstractNode, но имеет свои методы для получения информации о позиции,
            // их можно было бы вызвать здесь. Пока оставляем так, или можно установить значения по умолчанию.
            // Например: this.setSourceInfo(-1, -1, null);
        }
    }

    public Expression getInnerExpression() {
        return innerExpression;
    }

    @Override
    public Type getType(Scope scope) throws FailedCheckException {
        if (this.innerExpression == null) {
            throw new FailedCheckException("Inner expression of ParenthesizedExpression is null");
        }
        return this.innerExpression.getType(scope);
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        // TODO: Add visitParenthesizedExpression(this) to ASTVisitor and implement it.
        // For now, delegate to inner expression to allow compilation and basic functionality.
        // Node.accept does not throw FailedCheckException, so this method cannot either.
        // If innerExpression.accept can throw, it needs to be handled here or Node.accept needs to change.
        // For now, assuming innerExpression.accept also doesn't throw or it's a compatible unchecked exception.
        try {
            if (this.innerExpression != null) {
                return this.innerExpression.accept(visitor);
            }
        } catch (Exception e) {
            // This is a fallback. Ideally, FailedCheckException would be handled consistently.
            // If FailedCheckException is a RuntimeException, this catch is not strictly needed
            // unless we want to wrap it or handle it specifically here.
            // If it's a checked exception, this try-catch is needed if innerExpression.accept throws it
            // and this method cannot re-throw it.
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            // Wrap other checked exceptions if necessary, or log. For now, rethrow as Runtime to avoid changing signature.
            throw new RuntimeException("Error during visitor acceptance in ParenthesizedExpression", e);
        }
        return null; 
    }

    @Override
    public String toString() {
        return "Parenthesized{" + innerExpression + "}";
    }
}
