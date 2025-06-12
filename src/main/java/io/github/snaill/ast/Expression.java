package io.github.snaill.ast;

import java.util.List;

/**
 * Базовый класс для выражений в AST.
 */
public abstract class Expression extends AbstractNode {
    public Expression(List<Node> children) {
        super(children);
    }

    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context, FunctionDeclaration currentFunction) throws java.io.IOException, io.github.snaill.exception.FailedCheckException {
        emitBytecode(out, context, currentFunction);
    }

    public abstract Type getType(Scope scope) throws io.github.snaill.exception.FailedCheckException;
}
