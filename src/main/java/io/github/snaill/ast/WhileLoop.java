package io.github.snaill.ast;

import java.util.List;

/**
 * Представляет цикл while в AST.
 */
public class WhileLoop extends AbstractNode implements Statement {
    public WhileLoop(Expression condition, Scope body) {
        super(List.of(condition, body));
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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WhileLoop) {
            return super.equals(obj);
        }
        return false;
    }

    @Override
    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context) throws java.io.IOException, io.github.snaill.exception.FailedCheckException {
        emitBytecode(out, context, null);
    }

    /**
     * Генерирует байткод для цикла while со следующей структурой:
     * <p>
     * start:
     *   <байткод условия>
     *   JMP_IF_FALSE end
     *   <байткод тела>
     *   JMP start (указывает наверх для продолжения цикла)
     * end:
     */
    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context, FunctionDeclaration currentFunction) throws java.io.IOException, io.github.snaill.exception.FailedCheckException {
        // Запоминаем начальную позицию цикла
        int startPos = out.size();
        
        // Генерируем байткод для условия
        getCondition().emitBytecode(out, context, currentFunction);
        
        // Генерируем условный переход в конец цикла, если условие ложно
        out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.JMP_IF_FALSE);
        int jmpIfFalseOffset = out.size();
        io.github.snaill.bytecode.BytecodeUtils.writeU16(out, 0); // Заглушка для смещения
        
        // Генерируем байткод для тела цикла
        getBody().emitBytecode(out, context, currentFunction);
        
        // Генерируем безусловный переход назад к началу цикла
        out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.JMP);
        
        // Вычисляем смещение для перехода назад (отрицательное смещение)
        // Обратите внимание: смещение вычисляется относительно текущей позиции после опкода JMP
        // И должно учитывать 2 байта самого смещения
        int currentPos = out.size();
        int backOffset = startPos - currentPos - 2; // -2 для учета размера самого смещения (2 байта)
        io.github.snaill.bytecode.BytecodeUtils.writeU16(out, backOffset);
        
        // Запоминаем конечную позицию цикла
        int endPos = out.size();
        
        // Обновляем заглушку для смещения в JMP_IF_FALSE
        byte[] bytes = out.toByteArray();
        // Вычисляем смещение от позиции после JMP_IF_FALSE до конца цикла
        int forwardOffset = endPos - jmpIfFalseOffset - 2; // -2 для учета размера самого смещения (2 байта)
        io.github.snaill.bytecode.BytecodeUtils.writeU16(new java.io.ByteArrayOutputStream() { 
            @Override public void write(int b) { 
                bytes[jmpIfFalseOffset + (count++)] = (byte)b; 
            } 
            private int count = 0; 
        }, forwardOffset);
        
        // Записываем обновленный байткод
        out.reset();
        out.write(bytes);
    }

    @Override
    public java.util.List<io.github.snaill.result.Result> checkDeadCode() {
        return super.checkDeadCode();
    }

    @Override
    public void check(Scope scope) throws io.github.snaill.exception.FailedCheckException {
        Type conditionType = getCondition().getType(scope);
        if (!(conditionType instanceof PrimitiveType pt) || !pt.getName().equals("bool")) {
            String before = getCondition().getSource() != null ?
                io.github.snaill.ast.SourceBuilder.toSourceLine(getCondition().getSource(), getCondition().getLine(), getCondition().getCharPosition(), getCondition().toString().length()) :
                io.github.snaill.ast.SourceBuilder.toSourceCode(getCondition());
            throw new io.github.snaill.exception.FailedCheckException(
                new io.github.snaill.result.CompilationError(
                    io.github.snaill.result.ErrorType.TYPE_MISMATCH,
                    before,
                    "While loop condition must be of type bool, got " + conditionType,
                    ""
                ).toString()
            );
        }
        getBody().check(scope);
    }
}
