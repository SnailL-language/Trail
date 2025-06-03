package io.github.snaill;

import io.github.snaill.ast.*;
import io.github.snaill.exception.FailedCheckException;
import io.github.snaill.result.CompilationError;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс для проверки типов и других статических проверок AST.
 */
public class Check implements ASTVisitor<Void> {
    private List<CompilationError> errors = new ArrayList<>();

    /**
     * Проверяет AST на корректность типов и другие статические ошибки
     * @param node корневой узел AST
     * @throws FailedCheckException если найдены ошибки
     */
    public void check(Node node) throws FailedCheckException {
        // Посещаем все узлы AST и собираем ошибки
        node.accept(this);
        
        // Проверяем наличие ошибок и выбрасываем исключение, если они есть
        if (!errors.isEmpty()) {
            throw new FailedCheckException(errors);
        }
    }

    /**
     * Возвращает список найденных ошибок
     * @return список ошибок
     */
    public List<CompilationError> getErrors() {
        return errors;
    }

    @Override
    public Void visit(FunctionDeclaration node) {
        // Проверяем тело функции
        if (node.getBody() != null) {
            node.getBody().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(VariableDeclaration node) {
        // Проверяем инициализирующее выражение, если оно есть
        if (node.getValue() != null) {
            node.getValue().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(Scope node) {
        // Проверяем все дочерние узлы в скоупе
        for (Node child : node.getChildren()) {
            if (child != null) {
                child.accept(this);
            }
        }
        return null;
    }

    @Override
    public Void visit(BinaryExpression node) {
        // Проверяем левую и правую части выражения
        if (node.getLeft() != null) {
            node.getLeft().accept(this);
        }
        if (node.getRight() != null) {
            node.getRight().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(UnaryExpression node) {
        // Проверяем аргумент унарного выражения
        if (node.getArgument() != null) {
            node.getArgument().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(IfStatement node) {
        // Проверяем условие и тела if и else
        if (node.getCondition() != null) {
            node.getCondition().accept(this);
        }
        if (node.getBody() != null) {
            node.getBody().accept(this);
        }
        if (node.getElseBody() != null) {
            node.getElseBody().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(WhileLoop node) {
        // Проверяем условие и тело цикла
        if (node.getCondition() != null) {
            node.getCondition().accept(this);
        }
        if (node.getBody() != null) {
            node.getBody().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(ForLoop node) {
        // Проверяем условие, шаг и тело цикла
        if (node.getCondition() != null) {
            node.getCondition().accept(this);
        }
        if (node.getStep() != null) {
            node.getStep().accept(this);
        }
        if (node.getBody() != null) {
            node.getBody().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(ReturnStatement node) {
        // Проверяем возвращаемое выражение
        if (node.getReturnable() != null) {
            node.getReturnable().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(BreakStatement node) {
        // Для оператора break нет дополнительных проверок
        return null;
    }

    @Override
    public Void visit(VariableReference node) {
        // Базовая проверка ссылки на переменную
        return null;
    }

    @Override
    public Void visit(FunctionCall node) {
        // Проверяем все аргументы функции
        for (Expression arg : node.getArguments()) {
            if (arg != null) {
                arg.accept(this);
            }
        }
        return null;
    }

    @Override
    public Void visit(ArrayAccess node) {
        // Проверяем массив и индекс
        if (node.getArray() != null) {
            node.getArray().accept(this);
        }
        if (node.getIndex() != null) {
            node.getIndex().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(ArrayAssignment node) {
        // Проверяем массив, индекс и значение
        if (node.getArray() != null) {
            node.getArray().accept(this);
        }
        if (node.getIndex() != null) {
            node.getIndex().accept(this);
        }
        if (node.getValue() != null) {
            node.getValue().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(ArrayLiteral node) {
        // Проверяем все элементы массива
        for (Expression element : node.getElements()) {
            if (element != null) {
                element.accept(this);
            }
        }
        return null;
    }

    @Override
    public Void visit(Identifier node) {
        // Базовая проверка идентификатора
        return null;
    }

    @Override
    public Void visit(NumberLiteral node) {
        // Числовые литералы не требуют проверки
        return null;
    }

    @Override
    public Void visit(StringLiteral node) {
        // Строковые литералы не требуют проверки
        return null;
    }

    @Override
    public Void visit(BooleanLiteral node) {
        // Булевы литералы не требуют проверки
        return null;
    }

    @Override
    public Void visit(PrimitiveType node) {
        // Типы не требуют проверки
        return null;
    }

    @Override
    public Void visit(ArrayType node) {
        // Типы массивов не требуют проверки
        return null;
    }

    @Override
    public Void visit(Parameter node) {
        // Параметры не требуют проверки
        return null;
    }

    @Override
    public Void visit(AssignmentExpression node) {
        // Проверяем левую и правую части присваивания
        if (node.getLeft() != null) {
            node.getLeft().accept(this);
        }
        if (node.getRight() != null) {
            node.getRight().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(ArrayElement node) {
        // Проверяем идентификатор и размерности
        if (node.getIdentifier() != null) {
            node.getIdentifier().accept(this);
        }
        for (Expression dim : node.getDims()) {
            if (dim != null) {
                dim.accept(this);
            }
        }
        return null;
    }
}
