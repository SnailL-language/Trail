package io.github.snaill.ast;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    // void checkTypes();

    /**
     * Единый метод для запуска всех доступных оптимизаций.
      */
    default void optimize() {

    }

    /**
     * Единый метод для запуска всех проверок корректности кода.
      */
    default void check() throws FailedCheckException {
        Set<FunctionDeclaration> unused = new HashSet<>();
        checkUnusedFunctions(unused);
        System.out.println(
            unused.stream()
            .filter(Predicate.not(fn -> fn.getName().equals("main")))
            .map(fn -> new Warning(WarningType.UNUSED, SourceBuilder.toSourceCode(fn)).toString())
            .collect(Collectors.joining(System.lineSeparator()))
        );
        List<Result> results = checkDeadCode();
        System.out.println(
            results.stream()
            .map(Object::toString)
            .collect(Collectors.joining(System.lineSeparator()))
        );
        if (results.stream().anyMatch(Result::isCritical)) {
            throw new FailedCheckException();
        }
        // checkTypes();
    }
}
