package io.github.snaill.ast;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Представляет цикл for в AST.
 */
public class ForLoop extends AbstractNode implements Statement {
    // Children order: 0: initialization, 1: condition, 2: step, 3: body
    public ForLoop(Statement initialization, Expression condition, Expression step, Scope body) {
        super(List.of(initialization, condition, step, body));
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public Statement getInitialization() {
        return (Statement) children.getFirst();
    }

    public Expression getCondition() {
        return (Expression) children.get(1);
    }

    public Expression getStep() {
        return (Expression) children.get(2);
    }

    public Scope getBody() {
        return (Scope) children.get(3);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ForLoop) {
            return super.equals(obj);
        }
        return false;
    }

    // This is the primary implementation for ForLoop, not an override of a 3-arg method from a supertype.
    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context, FunctionDeclaration currentFunction) throws java.io.IOException, io.github.snaill.exception.FailedCheckException {
        // Emit bytecode for initialization statement
        Statement initialization = getInitialization();
        if (initialization instanceof VariableDeclaration) { // Or any other statement that needs emitting
            initialization.emitBytecode(out, context);
        } else if (initialization instanceof ExpressionStatement) {
            initialization.emitBytecode(out, context);
             // ExpressionStatements (like assignments) might leave a value on stack if they are expressions.
             // However, typical for-loop initializers (e.g. i=0) as statements don't leave values.
             // If the initializer expression itself has a non-void type and is used as a statement,
             // its result should be popped if not used.
             // This logic depends on how ExpressionStatement.emitBytecode handles its expression's result.
             // For now, assuming ExpressionStatement handles its own stack effects correctly.
        }

        // Loop structure bytecode generation
        int conditionStart = out.size(); // Start of condition evaluation for looping back
        getCondition().emitBytecode(out, context);
        out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.JMP_IF_FALSE);
        int jmpIfFalseAddr = out.size();
        io.github.snaill.bytecode.BytecodeUtils.writeU16(out, 0); // Placeholder for jump to after loop

        // Body execution
        getBody().emitBytecode(out, context);

        // Step execution
        getStep().emitBytecode(out, context);
        // If step is an expression (not an assignment that's an ExpressionStatement), it might leave a value.
        // We need to pop it if its type is not void.
        if (getStep() instanceof Expression) { 
             Type stepType = ((Expression)getStep()).getType(getBody()); // Scope for type resolution
             boolean isVoidStep = false;
             if (stepType instanceof PrimitiveType primType) {
                 isVoidStep = "void".equals(primType.getName());
             }
             // Only pop if it's a direct expression and not void. 
             // Assignments like i+=1 are often ExpressionStatements, which should handle their stack.
             if (!isVoidStep && !(getStep() instanceof AssignmentExpression)) { 
                out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.POP);
             }
        }

        // Jump back to condition
        out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.JMP);
        // Calculate relative jump: target (conditionStart) - (current_address + size_of_jmp_operand)
        io.github.snaill.bytecode.BytecodeUtils.writeU16(out, conditionStart - (out.size() + 2)); 

        // Patch the JMP_IF_FALSE to jump to here (after the loop)
        int afterLoopAddr = out.size();
        byte[] bytecode = out.toByteArray(); // Get all bytes written so far
        io.github.snaill.bytecode.BytecodeUtils.patchU16(bytecode, jmpIfFalseAddr, afterLoopAddr - (jmpIfFalseAddr + 2));
        out.reset(); // Clear the stream
        out.write(bytecode); // Write the modified bytecode back
    }

    @Override
    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context) throws java.io.IOException, io.github.snaill.exception.FailedCheckException {
        // Delegate to the three-argument version, passing null for currentFunction.
        // The three-argument version should handle null if function context isn't strictly needed from this call path.
        emitBytecode(out, context, null);
    }

    @Override
    public java.util.List<io.github.snaill.result.Result> checkDeadCode() {
        return super.checkDeadCode();
    }

    @Override
    public void check(Scope enclosingScope) throws io.github.snaill.exception.FailedCheckException {
        // Create a new scope for the for-loop construct (initializer, condition, step, body)
        // This scope's parent is the enclosingScope.
        Scope loopInternalScope = new Scope(new java.util.ArrayList<>(), enclosingScope, enclosingScope.getEnclosingFunction());

        // Check initialization and add its declared variables to the loopInternalScope
        Statement initialization = getInitialization();
        initialization.check(loopInternalScope); // Check the initializer within the loop's new scope
        if (initialization instanceof VariableDeclaration) {
            // Ensure the variable declared in 'initialization' is registered in 'loopInternalScope'
            // Scope.check or VariableDeclaration.check should handle adding to its immediate scope if designed that way
            // Or, explicitly add: loopInternalScope.addDeclaration((VariableDeclaration)initialization);
            // For now, assuming VariableDeclaration.check() correctly registers itself in the passed scope.
        }

        // Check condition within the loopInternalScope
        Type conditionType = getCondition().getType(loopInternalScope);
        if (!(conditionType instanceof PrimitiveType pt) || !pt.getName().equals("bool")) {
            String before = getCondition().getSource() != null ?
                io.github.snaill.ast.SourceBuilder.toSourceLine(getCondition().getSource(), getCondition().getLine(), getCondition().getCharPosition(), getCondition().toString().length()) :
                io.github.snaill.ast.SourceBuilder.toSourceCode(getCondition());
            throw new io.github.snaill.exception.FailedCheckException(
                new io.github.snaill.result.CompilationError(
                    io.github.snaill.result.ErrorType.TYPE_MISMATCH,
                    before,
                    "For loop condition must be of type bool, got " + conditionType,
                    ""
                ).toString()
            );
        }
        // Check step and body within the loopInternalScope
        getStep().check(loopInternalScope);
        getBody().check(loopInternalScope); // The body's own statements will be checked against loopInternalScope as their parent
    }

    @Override
    public void checkUnusedVariables(Set<VariableDeclaration> unused) {
        getInitialization().checkUnusedVariables(unused);
        getCondition().checkUnusedVariables(unused);
        getStep().checkUnusedVariables(unused);
        getBody().checkUnusedVariables(unused);
    }
}
