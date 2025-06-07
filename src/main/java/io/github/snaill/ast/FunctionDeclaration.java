package io.github.snaill.ast;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a function declaration in the AST.
 * Generates bytecode for the function and its body.
 */
public class FunctionDeclaration extends AbstractNode implements Statement /*, BytecodeEmittable */ {
    private final String name;
    private final List<Parameter> parameters;
    private final Type returnType;
    private final boolean isReturnTypeExplicit;

    public FunctionDeclaration(String name, List<Parameter> parameters, Type returnType, Scope body, boolean isReturnTypeExplicit) {
        super(List.of(body));
        this.name = name;
        this.parameters = parameters;
        this.returnType = returnType;
        this.isReturnTypeExplicit = isReturnTypeExplicit;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);

    }

    public String getName() {
        return name;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public Type getReturnType() {
        return returnType;
    }

    public Scope getBody() {
        return (Scope) children.getFirst();
    }

    public boolean isReturnTypeExplicit() {
        return isReturnTypeExplicit;
    }

    @Override
    public void checkUnusedFunctions(Set<FunctionDeclaration> unused) {
        // no-op: FunctionDeclaration should not remove itself from unused
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FunctionDeclaration other) {
            return name.equals(other.name)
                && parameters.equals(other.parameters)
                && returnType.equals(other.returnType)
                && isReturnTypeExplicit == other.isReturnTypeExplicit
                && super.equals(other);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, returnType, parameters, isReturnTypeExplicit);
    }

    @Override
    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context) throws java.io.IOException, io.github.snaill.exception.FailedCheckException {
        emitBytecode(out, context, null);
    }

    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context, FunctionDeclaration currentFunction) throws java.io.IOException, io.github.snaill.exception.FailedCheckException {
        // Register the function in the context
        context.addFunction(this);
        
        // Store arguments from the stack into local variables
        // Parameters have indices 0..N-1, where N is the number of parameters
        // When a function is called, arguments are already on the stack, in left-to-right order
        // Store them in reverse order (last argument is at the top of the stack)
        if (this.parameters != null && !this.parameters.isEmpty()) {
            int paramCount = this.parameters.size();
            // Arguments on the stack are in reverse order (last argument at the top of the stack)
            // So we store them in local variables starting from the last index
            for (int i = paramCount - 1; i >= 0; i--) {
                out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.STORE_LOCAL);
                io.github.snaill.bytecode.BytecodeUtils.writeU16(out, i);
            }
        }
        
        // Generate bytecode for the function body
        getBody().emitBytecode(out, context, this);
        
        // Add a return instruction if it's not present
        if (!hasReturnStatement(getBody())) {
            out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.PUSH_CONST);
            io.github.snaill.bytecode.BytecodeUtils.writeU16(out, 0); // Assumes 0 is the constant index for null
            out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.RET);
        }
    }

    // Helper method to check for the presence of a return statement
    private boolean hasReturnStatement(Scope scope) {
        for (Node node : scope.getChildren()) {
            if (node instanceof ReturnStatement) {
                return true;
            } else if (node instanceof Scope) {
                if (hasReturnStatement((Scope) node)) {
                    return true;
                }
            } else if (node instanceof IfStatement ifStmt) {
                if (ifStmt.getBody() != null && hasReturnStatement(ifStmt.getBody())) {
                    if (ifStmt.isHasElse() && ifStmt.getElseBody() != null && hasReturnStatement(ifStmt.getElseBody())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Scope getParentScope() {
        return getEnclosingScope();
    }

    @Override
    public void check() throws io.github.snaill.exception.FailedCheckException {
        if (getBody() != null) {
            List<io.github.snaill.result.Result> results = getBody().checkDeadCode();
            for (io.github.snaill.result.Result r : results) {
                if (r instanceof io.github.snaill.result.CompilationError) {
                    System.err.println(r);
                }
            }
        }
        // UNUSED warnings should go to System.out (now commented out)
        super.check();
    }

    // Add a method to collect local variables
    private void collectLocalVariables(Node node, java.util.Set<String> localVars, boolean isGlobalScope) {
        if (node instanceof VariableDeclaration varDecl) {
            if (!isGlobalScope) {
                localVars.add(varDecl.getName());
            }
        } else if (node instanceof Scope scope) {
            for (Node child : scope.getChildren()) {
                collectLocalVariables(child, localVars, isGlobalScope);
            }
        } else if (node instanceof IfStatement ifStmt) {
            if (ifStmt.getBody() != null) {
                collectLocalVariables(ifStmt.getBody(), localVars, isGlobalScope);
            }
            if (ifStmt.isHasElse() && ifStmt.getElseBody() != null) {
                collectLocalVariables(ifStmt.getElseBody(), localVars, isGlobalScope);
            }
        } else if (node instanceof ForLoop forLoop) {
            if (forLoop.getBody() != null) {
                collectLocalVariables(forLoop.getBody(), localVars, isGlobalScope);
            }
        } else if (node instanceof WhileLoop whileLoop) {
            if (whileLoop.getBody() != null) {
                collectLocalVariables(whileLoop.getBody(), localVars, isGlobalScope);
            }
        }
    }
}
