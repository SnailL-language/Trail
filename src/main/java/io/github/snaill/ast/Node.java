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

public interface Node {
    Node getChild(int index);

    Collection<Node> getChildren();

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
        // checkTypes();
        List<Result> results = checkDeadCode();
        String out = results.stream()
            .map(Object::toString)
            .collect(Collectors.joining(System.lineSeparator())
        );
        if (!out.equals("")) {
            System.out.println(out);
        }
        if (results.stream().anyMatch(Result::isCritical)) {
            throw new FailedCheckException();
        }
        checkUnused(this::checkUnusedFunctions, fn -> fn.getName().equals("main"));
        checkUnused(this::checkUnusedVariables, v -> false);
    }

    private static <T extends Node> void checkUnused(Consumer<Set<T>> checker, Predicate<T> expected) {
        Set<T> unused = new HashSet<>();
        checker.accept(unused);
        String out = unused.stream()
            .filter(Predicate.not(expected))
            .map(fn -> new Warning(WarningType.UNUSED, SourceBuilder.toSourceCode(fn)).toString())
            .collect(Collectors.joining(System.lineSeparator())
        );
        if (!out.equals("")) {
            System.out.println(out);
        }
    }
}
