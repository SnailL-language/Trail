package io.github.snaill.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.github.snaill.result.CompilationError;
import io.github.snaill.result.ErrorType;
import io.github.snaill.result.Result;
import java.io.IOException;

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
        try {
            return visitor.visit(this);
        } catch (IOException e) {
            throw new RuntimeException(e); // Or a more specific unchecked exception
        }
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

    @Override
    public List<Result> checkDeadCode() {
        return checkDeadCode(false);
    }

    public List<Result> checkDeadCode(boolean insideDead) {
        if (this instanceof AbstractNode an && an.wasDeadCodeReported) return new ArrayList<>();
        if (insideDead) return new ArrayList<>();
        List<Result> results = new ArrayList<>();
        boolean dead = false;
        // Проверяем then-блок
        if (getBody() != null && !dead) {
            results.addAll(getBody().checkDeadCode(insideDead));
        }
        // Проверяем else-блок
        if (hasElse && getElseBody() != null && !dead) {
            results.addAll(getElseBody().checkDeadCode(insideDead));
        }
        // Если then и else оба есть и оба завершаются return/break, то всё после if — dead code (Scope отвечает)
        // Если условие всегда true, то весь else-блок — dead code
        if (hasElse && getElseBody() != null && isAlwaysTrueCondition() && !insideDead) {
            Scope elseScope = getElseBody();
            if (elseScope instanceof AbstractNode an && an.wasDeadCodeReported) {
                // Уже печатали DEAD_CODE для этого блока
            } else {
                String before = io.github.snaill.ast.SourceBuilder.toSourceCode(elseScope);
                CompilationError err = new io.github.snaill.result.CompilationError(
                    io.github.snaill.result.ErrorType.DEAD_CODE,
                    before,
                    "DEAD_CODE",
                    ""
                );
                System.out.println(err);
                results.add(err);
                if (elseScope instanceof AbstractNode an2) an2.wasDeadCodeReported = true;
            }
        }
        return results;
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
