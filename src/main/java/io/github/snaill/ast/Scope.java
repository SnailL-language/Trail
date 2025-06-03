package io.github.snaill.ast;

import io.github.snaill.bytecode.BytecodeContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.Objects;
import java.util.Collection;

import io.github.snaill.result.CompilationError;
import io.github.snaill.result.ErrorType;
import io.github.snaill.result.Result;

/**
 * Представляет блок кода (scope) в AST.
 * Генерирует байткод для всех операторов в блоке.
 */
public class Scope extends AbstractNode implements Statement /*, BytecodeEmittable */ {
    private final Scope parent;
    private final FunctionDeclaration enclosingFunction;
    private boolean wasDeadCodeReported = false;

    public Scope(List<Statement> children) {
        this(children, null, null);
    }

    public Scope(List<Statement> children, Scope parent) {
        this(children, parent, null);
    }

    public Scope(List<Statement> children, Scope parent, FunctionDeclaration enclosingFunction) {
        super(children);
        this.parent = parent;
        this.enclosingFunction = enclosingFunction;
    }

    public Scope getParent() {
        return parent;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        try {
            return visitor.visit(this);
        } catch (IOException e) {
            throw new RuntimeException(e); // Or a more specific unchecked exception
        }
    }

    @Override
    public void setChildren(Collection<Node> children) {
        // Приводим к List<Statement> и затем к Collection<Node>
        List<Statement> stmts = children.stream().map(n -> (Statement) n).toList();
        super.setChildren((Collection<Node>) (Collection<?>) stmts);
    }

    @Override
    public List<Node> getChildren() {
        // Возвращаем как List<Node>, но реально это List<Statement>
        return new ArrayList<>(super.getChildren());
    }

    public List<Statement> getStatements() {
        return super.getChildren().stream().map(Statement.class::cast).toList();
    }

    @Override
    public void emitBytecode(ByteArrayOutputStream out, BytecodeContext context) throws IOException, io.github.snaill.exception.FailedCheckException {
        emitBytecode(out, context, null);
    }

    public void emitBytecode(ByteArrayOutputStream out, BytecodeContext context, FunctionDeclaration currentFunction) throws IOException, io.github.snaill.exception.FailedCheckException {
        for (Statement stmt : getStatements()) {
            try {
                stmt.getClass().getMethod("emitBytecode", ByteArrayOutputStream.class, BytecodeContext.class, FunctionDeclaration.class)
                    .invoke(stmt, out, context, currentFunction);
            } catch (NoSuchMethodException e) {
                stmt.emitBytecode(out, context);
            } catch (Exception e) {
                throw new IOException(e);
            }
        }
    }

    @Override
    public void checkUnusedVariables(Set<VariableDeclaration> unused) {
        super.checkUnusedVariables(unused);
    }

    @Override
    public void checkUnusedFunctions(Set<FunctionDeclaration> unused) {
        super.checkUnusedFunctions(unused);
    }

    @Override
    public void check() throws io.github.snaill.exception.FailedCheckException {
        boolean dead = false;
        for (int i = 0; i < getChildren().size(); i++) {
            Node child = getChildren().get(i);
            if (dead) {
                // Только для первого dead child
                String before = io.github.snaill.ast.SourceBuilder.toSourceCode(child);
                System.out.print("ERROR:" + before + ";");
                if (child instanceof Scope s) {
                    s.checkDeadCode(true);
                } else if (child instanceof IfStatement ifs) {
                    ifs.checkDeadCode(true);
                } else {
                    child.checkDeadCode();
                }
                break; // После dead child не продолжаем обход и не вызываем check() для его детей
            }
            if (child instanceof ReturnStatement || child instanceof BreakStatement) {
                dead = true;
            } else if (child instanceof IfStatement ifs) {
                boolean thenReturns = ifs.getBody() != null && endsWithReturnOrBreak(ifs.getBody());
                boolean elseReturns = ifs.isHasElse() && ifs.getElseBody() != null && endsWithReturnOrBreak(ifs.getElseBody());
                if (thenReturns && elseReturns) {
                    dead = true;
                } else {
                    child.check();
                }
            } else if (child instanceof Scope s) {
                child.check();
            } else {
                child.check();
            }
        }
        // UNUSED только для root scope
        if (parent == null) {
            Set<FunctionDeclaration> unusedFns = new java.util.HashSet<>();
            Set<VariableDeclaration> unusedVars = new java.util.HashSet<>();
            boolean seenFunction = false;
            for (Node child : getChildren()) {
                if (child instanceof FunctionDeclaration fn) {
                    seenFunction = true;
                    if (!fn.getName().equals("main")) unusedFns.add(fn);
                } else if (child instanceof VariableDeclaration var) {
                    if (!seenFunction) unusedVars.add(var);
                }
            }
            markUsedInTree(this, unusedVars, unusedFns);
            for (FunctionDeclaration fn : unusedFns) {
                System.out.println(new io.github.snaill.result.Warning(io.github.snaill.result.WarningType.UNUSED, io.github.snaill.ast.SourceBuilder.toSourceCode(fn)));
            }
            for (VariableDeclaration v : unusedVars) {
                System.out.println(new io.github.snaill.result.Warning(io.github.snaill.result.WarningType.UNUSED, io.github.snaill.ast.SourceBuilder.toSourceCode(v)));
            }
        }
    }

    // Рекурсивно обходит всё дерево и вызывает checkUnused* для всех узлов
    private void markUsedInTree(Node node, Set<VariableDeclaration> unusedVars, Set<FunctionDeclaration> unusedFns) {
        node.checkUnusedVariables(unusedVars);
        if (node instanceof FunctionDeclaration fn && fn.getBody() != null) {
            markUsedInTree(fn.getBody(), unusedVars, unusedFns);
        } else {
            node.checkUnusedFunctions(unusedFns);
            for (Node child : node.getChildren()) {
                if (child != null) markUsedInTree(child, unusedVars, unusedFns);
            }
        }
    }

    @Override
    public Node getParentNode() {
        return parent;
    }

    @Override
    public List<Result> checkDeadCode() {
        return checkDeadCode(false);
    }

    public List<Result> checkDeadCode(boolean insideDead) {
        if (this instanceof AbstractNode an && an.wasDeadCodeReported) return new ArrayList<>();
        if (insideDead) return new ArrayList<>(); // Не печатать DEAD_CODE для вложенных dead-блоков
        List<Result> results = new ArrayList<>();
        boolean dead = insideDead;
        for (int i = 0; i < children.size(); i++) {
            Node n = children.get(i);
            if (dead) {
                if (n instanceof AbstractNode an && an.wasDeadCodeReported) return results;
                if (!insideDead) {
                    String before = io.github.snaill.ast.SourceBuilder.toSourceCode(n);
                    CompilationError err = new io.github.snaill.result.CompilationError(
                        io.github.snaill.result.ErrorType.DEAD_CODE,
                        before,
                        "DEAD_CODE",
                        ""
                    );
                    System.out.println(err);
                    results.add(err);
                    if (n instanceof AbstractNode an2) an2.wasDeadCodeReported = true;
                    if (this instanceof AbstractNode anScope) {
                        anScope.wasDeadCodeReported = true;
                        markAllParentsDeadCodeReported(this);
                    }
                    markAllDeadCodeReported(n);
                }
                break; // break всегда после первого dead child
            }
            if (!dead) {
                if (n instanceof ReturnStatement || n instanceof BreakStatement) {
                    dead = true;
                } else if (n instanceof IfStatement ifs) {
                    boolean thenReturns = ifs.getBody() != null && endsWithReturnOrBreak(ifs.getBody());
                    boolean elseReturns = ifs.isHasElse() && ifs.getElseBody() != null && endsWithReturnOrBreak(ifs.getElseBody());
                    if (thenReturns && elseReturns) {
                        dead = true;
                    } else {
                        results.addAll(ifs.checkDeadCode(false));
                    }
                } else if (n instanceof Scope s) {
                    results.addAll(s.checkDeadCode(false));
                } else {
                    results.addAll(n.checkDeadCode());
                }
            }
        }
        return results;
    }

    private String toStatementWithSemicolon(Node n) {
        if (n == null) return "";
        String code = io.github.snaill.ast.SourceBuilder.toSourceCode(n);
        code = code.trim();
        if (!code.endsWith(";")) code += ";";
        return code;
    }

    private boolean endsWithReturnOrBreak(Scope scope) {
        List<Node> stmts = scope.getChildren();
        for (int i = stmts.size() - 1; i >= 0; i--) {
            Node n = stmts.get(i);
            if (n instanceof ReturnStatement || n instanceof BreakStatement) return true;
            if (n instanceof IfStatement ifs) {
                boolean thenR = ifs.getBody() != null && endsWithReturnOrBreak(ifs.getBody());
                boolean elseR = ifs.getElseBody() != null && endsWithReturnOrBreak(ifs.getElseBody());
                if (thenR && elseR) return true;
            }
            if (n instanceof Scope s) {
                if (endsWithReturnOrBreak(s)) return true;
            }
            if (!(n instanceof IfStatement)) break;
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Scope) {
            return super.equals(obj);
        }
        return false;
    }

    public VariableDeclaration resolveVariable(String name) {
        System.out.println("DEBUG: Resolving variable: " + name);
        
        // Проверяем параметры функции, если мы находимся в области видимости функции
        if (enclosingFunction != null) {
            for (Parameter param : enclosingFunction.getParameters()) {
                if (param.getName().equals(name)) {
                    System.out.println("DEBUG: Found parameter " + name + " in function " + enclosingFunction.getName());
                    // Создаем временное объявление переменной на основе параметра
                    return new VariableDeclaration(name, param.getType(), null);
                }
            }
        }
        
        // Проверяем локальные переменные в текущей области видимости
        for (Node child : children) {
            if (child instanceof VariableDeclaration varDecl && varDecl.getName().equals(name)) {
                System.out.println("DEBUG: Found variable " + name + " in current scope");
                return varDecl;
            }
        }
        
        // Если не нашли, ищем в родительской области видимости
        if (parent != null) {
            System.out.println("DEBUG: Variable " + name + " not found in current scope, checking parent scope");
            return parent.resolveVariable(name);
        }
        
        System.out.println("DEBUG: Variable " + name + " not found in any scope");
        return null;
    }

    public VariableDeclaration resolveVariable(String name, Node context) {
        System.out.println("DEBUG: Resolving variable with context: " + name);
        
        // Проверяем параметры функции, если мы находимся в области видимости функции
        if (enclosingFunction != null) {
            for (Parameter param : enclosingFunction.getParameters()) {
                if (param.getName().equals(name)) {
                    System.out.println("DEBUG: Found parameter " + name + " in function " + enclosingFunction.getName());
                    // Создаем временное объявление переменной на основе параметра
                    return new VariableDeclaration(name, param.getType(), null);
                }
            }
        }
        
        // Сначала ищем VariableDeclaration
        for (Node child : children) {
            if (child instanceof VariableDeclaration vd && vd.getName().equals(name)) {
                // Если context — выражение инициализации этой переменной, не разрешаем саму себя
                if (context != null && vd.getValue() == context) {
                    continue;
                }
                System.out.println("DEBUG: Found variable " + name + " in current scope");
                return vd;
            }
        }
        
        // Если не нашли, ищем в родительской области видимости
        if (parent != null) {
            System.out.println("DEBUG: Variable " + name + " not found in current scope, checking parent scope");
            return parent.resolveVariable(name, context);
        }
        
        System.out.println("DEBUG: Variable " + name + " not found in any scope");
        return null;
    }

    private FunctionDeclaration findEnclosingFunction() {
        if (this.enclosingFunction != null) return this.enclosingFunction;
        if (parent != null) return parent.findEnclosingFunction();
        return null;
    }

    public FunctionDeclaration resolveFunction(String name) {
        for (Node child : children) {
            if (child instanceof FunctionDeclaration f && f.getName().equals(name)) {
                return f;
            }
        }
        return parent != null ? parent.resolveFunction(name) : null;
    }

    // Помечает всех AbstractNode в поддереве как wasDeadCodeReported
    private void markAllDeadCodeReported(Node node) {
        if (node instanceof AbstractNode an) {
            an.wasDeadCodeReported = true;
        }
        for (Node child : node.getChildren()) {
            if (child != null) markAllDeadCodeReported(child);
        }
    }

    // Помечает всех родителей до Scope как wasDeadCodeReported
    private void markAllParentsDeadCodeReported(Node node) {
        Node parent = null;
        if (node instanceof AbstractNode an) parent = an.getParentNode();
        while (parent != null && parent instanceof AbstractNode anp) {
            anp.wasDeadCodeReported = true;
            if (parent instanceof Scope) break;
            parent = anp.getParentNode();
        }
    }
}
