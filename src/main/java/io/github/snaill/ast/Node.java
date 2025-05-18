package io.github.snaill.ast;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import io.github.snaill.exception.FailedCheckException;
import io.github.snaill.result.Result;

public interface Node {
    Node getChild(int index);

    Collection<Node> getChildren();

    void setChildren(Collection<Node> children);

    int getChildCount();

    void setChild(int index, Node child);

    List<Result> checkDeadCode();

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
        List<Result> results = checkDeadCode();
        System.out.println(results.stream().map(Object::toString).collect(Collectors.joining(System.lineSeparator())));
        if (results.stream().anyMatch(Result::isCritical)) {
            throw new FailedCheckException();
        }
        // checkTypes();
    }
}
