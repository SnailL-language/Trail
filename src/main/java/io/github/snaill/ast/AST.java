package io.github.snaill.ast;

/**
 * Интерфейс, представляющий абстрактное синтаксическое дерево.
 * Основной корневой узел дерева.
 */
public interface AST extends Node {
    /**
     * Возвращает корневой узел AST (обычно Scope).
     * @return корневой узел AST
     */
    Scope root();
}
