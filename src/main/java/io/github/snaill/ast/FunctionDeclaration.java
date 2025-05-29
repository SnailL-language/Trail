package io.github.snaill.ast;

import io.github.snaill.bytecode.BytecodeConstants;
import io.github.snaill.bytecode.BytecodeContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Представляет объявление функции в AST.
 * Генерирует байткод для функции и её тела.
 */
public class FunctionDeclaration extends AbstractNode implements Statement /*, BytecodeEmittable */ {
    private final String name;
    private final List<Parameter> parameters;
    private final Type returnType;

    public FunctionDeclaration(String name, List<Parameter> parameters, Type returnType, Scope body) {
        super(List.of(body));
        this.name = name;
        this.parameters = parameters;
        this.returnType = returnType;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        try {
            return visitor.visit(this);
        } catch (IOException e) {
            throw new RuntimeException(e); // Or a more specific unchecked exception
        }
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

    @Override
    public void checkUnusedFunctions(Set<FunctionDeclaration> unused) {
        // no-op: FunctionDeclaration не должна удалять себя из unused
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FunctionDeclaration other) {
            return name.equals(other.name)
                && parameters.equals(other.parameters)
                && returnType.equals(other.returnType)
                && super.equals(other);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, returnType, parameters);
    }

    @Override
    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context) throws java.io.IOException, io.github.snaill.exception.FailedCheckException {
        emitBytecode(out, context, null);
    }

    public void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context, FunctionDeclaration currentFunction) throws java.io.IOException, io.github.snaill.exception.FailedCheckException {
        // Регистрируем функцию в контексте
        context.addFunction(this);
        // Генерируем байткод тела функции
        getBody().emitBytecode(out, context, this);
        // Добавляем инструкцию возврата, если её нет
        if (!hasReturnStatement(getBody())) {
            out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.PUSH_CONST);
            io.github.snaill.bytecode.BytecodeUtils.writeU16(out, 0); // Предполагается, что 0 - это индекс константы для null
            out.write(io.github.snaill.bytecode.BytecodeConstants.Opcode.RET);
        }
    }

    // Вспомогательный метод для проверки наличия оператора return
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
        // UNUSED warnings должны идти в System.out
        super.check();
    }

    // Добавляем метод для сбора локальных переменных
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
