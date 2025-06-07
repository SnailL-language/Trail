package io.github.snaill.ast;

import java.util.ArrayList;
import java.util.List;

import io.github.snaill.result.CompilationError;
import io.github.snaill.result.ErrorType;
import io.github.snaill.result.Result;

/**
 * Представляет условный оператор if в AST.
 * Генерирует байткод для условного выполнения блока кода.
 */
public class IfStatement extends AbstractNode implements Statement /*, BytecodeEmittable */ {

    private final boolean hasElse;

    public IfStatement(Expression condition, Scope body, Scope elseBody) {
        super(elseBody == null ? List.of(condition, body) : List.of(condition, body, elseBody));
        this.hasElse = elseBody != null;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);

    }

    public Expression getCondition() {
        return (Expression) children.getFirst();
    }

    public Scope getBody() {
        return (Scope) children.get(1);
    }

    public Scope getElseBody() {
        return children.size() > 2 ? (Scope) children.get(2) : null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IfStatement) {
            return super.equals(obj);
        }
        return false;
    }

    @Override
    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context) throws java.io.IOException, io.github.snaill.exception.FailedCheckException {
        emitBytecode(out, context, null);
    }

    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context, FunctionDeclaration currentFunction) throws java.io.IOException, io.github.snaill.exception.FailedCheckException {
        // Продолжаем с генерацией байткода для if
        getCondition().emitBytecode(out, context, currentFunction);
        out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.JMP_IF_FALSE);
        int jmpIfFalseOffset = out.size();
        io.github.snaill.bytecode.BytecodeUtils.writeU16(out, 0); // Placeholder for jump offset
        getBody().emitBytecode(out, context, currentFunction);
        if (getElseBody() != null) {
            out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.JMP);
            int jmpOffset = out.size();
            io.github.snaill.bytecode.BytecodeUtils.writeU16(out, 0); // Placeholder for jump offset
            int elseStart = out.size();
            getElseBody().emitBytecode(out, context, currentFunction);
            int end = out.size();
            // Обновляем значение jump offset для JMP
            final byte[] bytes1 = out.toByteArray();
            io.github.snaill.bytecode.BytecodeUtils.writeU16(new java.io.ByteArrayOutputStream() { @Override public void write(int b) { bytes1[jmpOffset + (count++)] = (byte)b; } private int count = 0; }, end - jmpOffset - 2);
            out.reset();
            out.write(bytes1);
            // Обновляем значение jump offset для JMP_IF_FALSE
            final byte[] bytes2 = out.toByteArray();
            io.github.snaill.bytecode.BytecodeUtils.writeU16(new java.io.ByteArrayOutputStream() { @Override public void write(int b) { bytes2[jmpIfFalseOffset + (count++)] = (byte)b; } private int count = 0; }, elseStart - jmpIfFalseOffset - 2);
            out.reset();
            out.write(bytes2);
        } else {
            int end = out.size();
            // Обновляем значение jump offset для JMP_IF_FALSE
            final byte[] bytes3 = out.toByteArray();
            io.github.snaill.bytecode.BytecodeUtils.writeU16(new java.io.ByteArrayOutputStream() { @Override public void write(int b) { bytes3[jmpIfFalseOffset + (count++)] = (byte)b; } private int count = 0; }, end - jmpIfFalseOffset - 2);
            out.reset();
            out.write(bytes3);
        }
    }

    private boolean isAlwaysFalseCondition() {
        Expression condition = getCondition();
        if (condition instanceof BooleanLiteral) {
            return !((BooleanLiteral) condition).getValue();
        }
        return false;
    }

    @Override
    public List<Result> checkDeadCode() {
        List<Result> results = new ArrayList<>();
        AbstractNode thisNode = (this instanceof AbstractNode) ? (AbstractNode) this : null;

        // If this whole IfStatement was marked as dead by its parent, do nothing.
        if (thisNode != null && thisNode.wasDeadCodeReported) {
            return results;
        }

        // Check condition for dead code (though less common for expressions)
        if (getCondition() != null) {
            results.addAll(getCondition().checkDeadCode());
        }

        Scope thenBody = getBody();
        Scope elseBody = getElseBody();

        if (isAlwaysFalseCondition()) {
            // Then branch is dead
            if (thenBody != null) {
                AbstractNode thenAbstractNode = (thenBody instanceof AbstractNode) ? (AbstractNode) thenBody : null;
                if (thenAbstractNode != null && !thenAbstractNode.wasDeadCodeReported) {
                    String beforeString = thenAbstractNode.getSource() != null ? 
                                          SourceBuilder.toSourceLine(thenAbstractNode.getSource(), thenAbstractNode.getLine(), thenAbstractNode.getCharPosition(), SourceBuilder.toSourceCode(thenBody).length()) :
                                          SourceBuilder.toSourceCode(thenBody);
                    CompilationError err = new CompilationError(
                        ErrorType.DEAD_CODE,
                        beforeString,
                        "Then branch is unreachable due to always-false condition.",
                        ""
                    );
                    results.add(err);
                    markSubtreeAsDeadCodeReported(thenBody);
                } else if (thenAbstractNode == null && !thenBody.getChildren().isEmpty()) { // If thenBody is not an AbstractNode but has content, still report
                     String beforeString = SourceBuilder.toSourceCode(thenBody);
                     CompilationError err = new CompilationError(
                        ErrorType.DEAD_CODE,
                        beforeString,
                        "Then branch is unreachable due to always-false condition.",
                        ""
                    );
                    results.add(err);
                    markSubtreeAsDeadCodeReported(thenBody); // Mark children as well
                }
            }
            // Else branch is live (if it exists)
            if (hasElse && elseBody != null) {
                results.addAll(elseBody.checkDeadCode());
            }
        } else if (isAlwaysTrueCondition()) {
            // Then branch is live
            if (thenBody != null) {
                results.addAll(thenBody.checkDeadCode());
            }
            // Else branch is dead (if it exists)
            if (hasElse && elseBody != null) {
                AbstractNode elseAbstractNode = (elseBody instanceof AbstractNode) ? (AbstractNode) elseBody : null;
                if (elseAbstractNode != null && !elseAbstractNode.wasDeadCodeReported) {
                    String beforeString = elseAbstractNode.getSource() != null ? 
                                          SourceBuilder.toSourceLine(elseAbstractNode.getSource(), elseAbstractNode.getLine(), elseAbstractNode.getCharPosition(), SourceBuilder.toSourceCode(elseBody).length()) :
                                          SourceBuilder.toSourceCode(elseBody);
                    CompilationError err = new CompilationError(
                        ErrorType.DEAD_CODE,
                        beforeString,
                        "Else branch is unreachable due to always-true condition.",
                        ""
                    );
                    results.add(err);
                    markSubtreeAsDeadCodeReported(elseBody);
                } else if (elseAbstractNode == null && !elseBody.getChildren().isEmpty()) { // If elseBody is not an AbstractNode but has content, still report
                     String beforeString = SourceBuilder.toSourceCode(elseBody);
                     CompilationError err = new CompilationError(
                        ErrorType.DEAD_CODE,
                        beforeString,
                        "Else branch is unreachable due to always-true condition.",
                        ""
                    );
                    results.add(err);
                    markSubtreeAsDeadCodeReported(elseBody); // Mark children as well
                }
            }
        } else {
            // Condition is not a constant, check both branches
            if (thenBody != null) {
                results.addAll(thenBody.checkDeadCode());
            }
            if (hasElse && elseBody != null) {
                results.addAll(elseBody.checkDeadCode());
            }
        }
        return results;
    }

    // Helper to mark a node and its children as reported for dead code.
    // This prevents duplicate messages if a parent scope also identifies this subtree as dead.
    private void markSubtreeAsDeadCodeReported(Node node) {
        if (node instanceof AbstractNode abstractNode) {
            abstractNode.wasDeadCodeReported = true;
        }
        for (Node child : node.getChildren()) {
            markSubtreeAsDeadCodeReported(child);
        }
    }

    private boolean endsWithReturnOrBreak(Scope scope) {
        List<Node> stmts = scope.getChildren();
        for (int i = stmts.size() - 1; i >= 0; i--) {
            Node n = stmts.get(i);
            if (n instanceof ReturnStatement || n instanceof BreakStatement) return true;
            if (n instanceof IfStatement ifs) {
                boolean thenR = ifs.getBody() != null && endsWithReturnOrBreak(ifs.getBody());
                boolean elseR = ifs.hasElse && ifs.getElseBody() != null && endsWithReturnOrBreak(ifs.getElseBody());
                if (thenR && elseR) return true;
            }
            if (n instanceof Scope s) {
                if (endsWithReturnOrBreak(s)) return true;
            }
            // Если встретили не return/break/if — значит, не dead
            if (!(n instanceof IfStatement)) break;
        }
        return false;
    }

    @Override
    public void check(Scope scope) throws io.github.snaill.exception.FailedCheckException {
        getCondition().check(scope);
        Type condType = getCondition().getType(scope);
        if (!(condType instanceof PrimitiveType pt) || !pt.getName().equals("bool")) {
            String before = getSource() != null ?
                io.github.snaill.ast.SourceBuilder.toSourceLine(getSource(), getLine(), getCharPosition(), getCondition().toString().length()) :
                io.github.snaill.ast.SourceBuilder.toSourceCode(getCondition());
            throw new io.github.snaill.exception.FailedCheckException(
                new io.github.snaill.result.CompilationError(
                    io.github.snaill.result.ErrorType.TYPE_MISMATCH,
                    before,
                    "Condition in if statement must be of type bool, got " + condType,
                    ""
                ).toString()
            );
        }
        // then-ветка
        Scope thenScope = getBody();
        int deadStart = -1;
        var thenChildren = thenScope.getChildren();
        for (int i = 0; i < thenChildren.size() - 1; i++) {
            if (thenChildren.get(i) instanceof ReturnStatement || thenChildren.get(i) instanceof BreakStatement) {
                deadStart = i + 1;
                break;
            }
        }
        if (deadStart == -1) {
            for (Node child : thenChildren) {
                child.check(thenScope);
            }
        } else {
            for (int i = 0; i < deadStart; i++) {
                thenChildren.get(i).check(thenScope);
            }
        }
        // else-ветка
        if (getElseBody() != null) {
            Scope elseScope = getElseBody();
            deadStart = -1;
            var elseChildren = elseScope.getChildren();
            for (int i = 0; i < elseChildren.size() - 1; i++) {
                if (elseChildren.get(i) instanceof ReturnStatement || elseChildren.get(i) instanceof BreakStatement) {
                    deadStart = i + 1;
                    break;
                }
            }
            if (deadStart == -1) {
                for (Node child : elseChildren) {
                    child.check(elseScope);
                }
            } else {
                for (int i = 0; i < deadStart; i++) {
                    elseChildren.get(i).check(elseScope);
                }
            }
        }
    }

    // Проверка, всегда ли условие истинно (например, if (true))
    private boolean isAlwaysTrueCondition() {
        if (getCondition() instanceof BooleanLiteral bl) {
            return bl.getValue();
        }
        // Константная свёртка для бинарных выражений с двумя литералами
        if (getCondition() instanceof BinaryExpression be) {
            if (be.getLeft() instanceof NumberLiteral l && be.getRight() instanceof NumberLiteral r) {
                long lv = l.getValue();
                long rv = r.getValue();
                return switch (be.getOperator()) {
                    case "==" -> lv == rv;
                    case "!=" -> lv != rv;
                    case "<" -> lv < rv;
                    case "<=" -> lv <= rv;
                    case ">" -> lv > rv;
                    case ">=" -> lv >= rv;
                    default -> false;
                };
            }
        }
        return false;
    }

    public boolean isHasElse() {
        return hasElse;
    }
}
