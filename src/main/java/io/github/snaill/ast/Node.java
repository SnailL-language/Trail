package io.github.snaill.ast;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import io.github.snaill.exception.FailedCheckException;
import io.github.snaill.result.Result;
import io.github.snaill.result.Warning;
import io.github.snaill.result.WarningType;

/**
 * Base interface for all Abstract Syntax Tree (AST) nodes.
 * Each node must be able to accept a visitor.
 */
public interface Node {
    /**
     * Accepts an AST visitor.
     *
     * @param visitor The visitor to accept.
     * @param <T> The return type of the visitor's methods.
     * @return The result of the visit operation.
     */
    <T> T accept(ASTVisitor<T> visitor);

    /**
     * Gets the child nodes of this AST node.
     * This is useful for generic tree traversal if needed, 
     * though the Visitor pattern is preferred for type-safe operations.
     *
     * @return A list of child nodes. Implementations should return an empty list if there are no children.
     */
    List<Node> getChildren();

    Node getChild(int index);

    void setChildren(Collection<Node> children);

    int getChildCount();

    void setChild(int index, Node child);

    List<Result> checkDeadCode();

    void checkUnusedFunctions(Set<FunctionDeclaration> unused);

    void checkUnusedVariables(Set<VariableDeclaration> unused);

    // void checkTypes();

    /**
     * Единый метод для запуска всех доступных оптимизаций.
      */
    default void optimize() {

    }

    /**
     * Единый метод для запуска всех проверок корректности кода.
      */
    @SuppressWarnings("unused")
    default void check() throws FailedCheckException {
        List<Result> results = checkDeadCode();
        // Сначала печатаем только ошибки
        results.stream().filter(r -> r instanceof io.github.snaill.result.CompilationError).forEach(r -> System.err.println(r));
        // Всегда ищем и печатаем неиспользуемые
        Set<FunctionDeclaration> unusedFns = new java.util.HashSet<>();
        checkUnusedFunctions(unusedFns);
        unusedFns.stream().filter(fn -> !fn.getName().equals("main")).forEach(fn -> System.err.println(new io.github.snaill.result.Warning(io.github.snaill.result.WarningType.UNUSED, io.github.snaill.ast.SourceBuilder.toSourceCode(fn))));
        Set<VariableDeclaration> unusedVars = new java.util.HashSet<>();
        checkUnusedVariables(unusedVars);
        unusedVars.forEach(var -> System.err.println(new io.github.snaill.result.Warning(io.github.snaill.result.WarningType.UNUSED, io.github.snaill.ast.SourceBuilder.toSourceCode(var))));
        boolean hasCritical = results.stream().anyMatch(Result::isCritical);
        if (hasCritical) {
            throw new FailedCheckException();
        }
    }

    /**
     * Проверка корректности кода с учётом текущего Scope (для типизации).
     */
    default void check(Scope scope) throws io.github.snaill.exception.FailedCheckException {
        for (Node child : getChildren()) {
            if (child != null) {
                child.check(scope);
            }
        }
    }

    private static <T extends Node> void checkUnused(Consumer<Set<T>> checker, Predicate<T> expected) {
        Set<T> unused = new HashSet<>();
        checker.accept(unused);
        unused.stream()
            .filter(Predicate.not(expected))
            .map(fn -> new Warning(WarningType.UNUSED, SourceBuilder.toSourceCode(fn)))
            .forEach(w -> {
                if (w.isCritical()) {
                    System.err.println(w);
                } else {
                    System.out.println(w);
                }
            });
    }

    /**
     * Генерирует байткод для данного узла AST.
     * @param out поток для записи байткода
     * @param context контекст генерации байткода
     */
    default void emitBytecode(java.io.ByteArrayOutputStream out, io.github.snaill.bytecode.BytecodeContext context) throws java.io.IOException, io.github.snaill.exception.FailedCheckException {
        throw new UnsupportedOperationException("emitBytecode не реализован для " + getClass().getSimpleName());
    }
}
