 package io.github.snaill;

import io.github.snaill.ast.*;
import io.github.snaill.exception.FailedCheckException;
import io.github.snaill.result.CompilationError;
import io.github.snaill.result.ErrorType;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Класс для проверки типов и других статических проверок AST.
 */
public class Check implements ASTVisitor<Void> {
    private static final Logger logger = LoggerFactory.getLogger(Check.class);
    private Scope currentScope;
    private final List<CompilationError> errors = new ArrayList<>();

    /**
     * Проверяет AST на корректность типов и другие статические ошибки.
     * Собирает ошибки в внутренний список.
     * @param node корневой узел AST
     * @return список найденных ошибок компиляции
     */
    public List<CompilationError> check(Node node) {
        logger.debug("ENTERING Check.check() with node: {} | initial currentScope: {}", (node != null ? node.getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(node)) + " " + node.getSourceInfo() : "null"), this.currentScope);
        this.errors.clear();
        if (this.currentScope == null) { // Only set initial scope if not already set by a wrapping call
            if (node instanceof AST) {
                this.currentScope = ((AST) node).root();
            } else if (node instanceof Scope) {
                this.currentScope = (Scope) node;
            } else if (node instanceof AbstractNode) { // Most nodes are AbstractNodes
                Scope enclosing = ((AbstractNode) node).getEnclosingScope();
                if (enclosing != null) {
                    this.currentScope = enclosing;
                }
            } else if (node != null) {
                // If it's a bare Node not an AbstractNode, and not AST/Scope, scope must be set by caller or found via children
                // This case should ideally not happen for an initial call if AST structure is consistent.
                // For now, we proceed, hoping a Scope visit will set currentScope soon.
            }
        }
        if (node != null) {
            node.accept(this);
        }
        return new ArrayList<>(this.errors);
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
        // Define the variable in the current scope first
        if (this.currentScope != null) {
            try {
                System.out.println("[CHECK_VAR_DECL] Attempting to add declaration for '" + node.getName() + "' to scope: " + System.identityHashCode(this.currentScope));
                this.currentScope.addDeclaration(node);
                System.out.println("[CHECK_VAR_DECL] Successfully added declaration for '" + node.getName() + "' to scope: " + System.identityHashCode(this.currentScope));
            } catch (FailedCheckException e) {
                System.err.println("[CHECK_VAR_DECL] FailedCheckException while adding declaration for '" + node.getName() + "': " + e.getMessage());
                if (e.getErrors() != null) {
                    this.errors.addAll(e.getErrors());
                } else {
                    this.errors.add(new CompilationError(ErrorType.INTERNAL_ERROR, (node.getSource() != null ? SourceBuilder.toSourceLine(node.getSource(), node.getLine(), node.getCharPosition(), 1) : "<source not available>"), e.getMessage() != null ? e.getMessage() : "Failed to add variable declaration", ""));
                }
                // Potentially return early if declaration fails, as further checks might be invalid
                 return null; 
            }
        } else {
            System.err.println("[CHECK_VAR_DECL] CRITICAL: currentScope is null when trying to declare variable '" + node.getName() + "'. This should not happen.");
            this.errors.add(new CompilationError(ErrorType.INTERNAL_ERROR, (node.getSource() != null ? SourceBuilder.toSourceLine(node.getSource(), node.getLine(), node.getCharPosition(), 1) : "<source not available>"), "Compiler error: current scope not set for variable declaration.", ""));
            return null; // Cannot proceed without a scope
        }

        Type declaredType = node.getType();
        Expression value = node.getValue();

        if (value != null) {
            value.accept(this); // Check the initializer expression first
            try {
                Scope tempScope = this.currentScope; // Prefer current visitor scope
                if (tempScope == null && node instanceof AbstractNode) { // Fallback if currentScope is somehow null
                    tempScope = ((AbstractNode)node).getEnclosingScope();
                }
                if (tempScope == null) {
                     this.errors.add(new CompilationError(ErrorType.INTERNAL_ERROR, (node.getSource() != null ? SourceBuilder.toSourceLine(node.getSource(), node.getLine(), node.getCharPosition(), 1) : "<source not available>"), "Cannot determine scope for variable '" + node.getName() + "' type checking.", ""));
                     return null;
                }
                Type actualType = value.getType(tempScope);
                
                boolean typesCompatible = false;
                if (declaredType.equals(actualType)) {
                    typesCompatible = true;
                } else if (declaredType instanceof PrimitiveType dt && dt.getName().equals("usize") && 
                           value instanceof NumberLiteral nl && nl.isNonNegative() && 
                           actualType instanceof PrimitiveType at && at.getName().equals("i32")) {
                    typesCompatible = true;
                } else if (declaredType instanceof ArrayType dtArray && dtArray.getSize().getValue() == 0 &&
                           actualType instanceof ArrayType atArray && atArray.getSize().getValue() == 0 &&
                           atArray.getElementType() instanceof PrimitiveType ptElem && "i32".equals(ptElem.getName())) {
                    // Allow assigning [] (which is [i32;0]) to any [SomeType;0]
                    typesCompatible = true;
                }

                if (!typesCompatible) {
                    String sourceLine = node.getSource() != null ? 
                                        SourceBuilder.toSourceLine(node.getSource(), node.getLine(), node.getCharPosition(), node.getName().length()) :
                                        "<source not available>";
                    errors.add(new CompilationError(
                        ErrorType.TYPE_MISMATCH,
                        sourceLine,
                        "Type mismatch: cannot assign " + actualType + " to variable '" + node.getName() + "' of type " + declaredType,
                        "Expected: " + declaredType + ", Actual: " + actualType
                    ));
                }
            } catch (FailedCheckException e) {
                if (e.getErrors() != null) {
                    this.errors.addAll(e.getErrors());
                } else {
                     this.errors.add(new CompilationError(ErrorType.INTERNAL_ERROR, (node.getSource() != null ? SourceBuilder.toSourceLine(node.getSource(), node.getLine(), node.getCharPosition(), 1) : "<source not available>"), e.getMessage() != null ? e.getMessage() : "Type resolution failed for variable '" + node.getName() + "'", ""));
                }
            } catch (Exception e) {
                this.errors.add(new CompilationError(ErrorType.INTERNAL_ERROR, (node.getSource() != null ? SourceBuilder.toSourceLine(node.getSource(), node.getLine(), node.getCharPosition(), 1) : "<source not available>"), "Error resolving type for variable '" + node.getName() + "' initializer: " + (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()), ""));
            }
        }
        return null;
    }

    @Override
    public Void visit(AssignmentExpression node) {
        System.out.println("[CHECK_ASSIGN_ENTRY] Entered visit(AssignmentExpression) for node: " + (node != null ? node.toString() + " " + node.getSourceInfo() : "null"));
        Expression left = node != null ? node.getLeft() : null;
        Expression right = node != null ? node.getRight() : null;
        System.out.println("[CHECK_ASSIGN_ENTRY] Left child: " + (left != null ? left.getClass().getName() + " -> " + left.toString() : "null"));
        System.out.println("[CHECK_ASSIGN_ENTRY] Right child: " + (right != null ? right.getClass().getName() + " -> " + right.toString() : "null"));
        System.out.println("[CHECK_ASSIGN_ENTRY] this.currentScope: " + (this.currentScope != null ? this.currentScope.getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this.currentScope)) : "null"));
        Expression leftExpr = node.getLeft();
        Expression rightExpr = node.getRight();

        if (leftExpr != null && rightExpr != null) {
            System.out.println("[CHECK_ASSIGN_IF] Entered if (leftExpr != null && rightExpr != null) block.");
            System.out.println("[CHECK_ASSIGN_IF] Before leftExpr.accept(this) for: " + leftExpr.getClass().getName() + ". Current scope: " + (this.currentScope != null ? System.identityHashCode(this.currentScope) : "null"));
            leftExpr.accept(this);
            System.out.println("[CHECK_ASSIGN_IF] After leftExpr.accept(this). Current scope: " + (this.currentScope != null ? System.identityHashCode(this.currentScope) : "null"));
            System.out.println("[CHECK_ASSIGN_IF] Before rightExpr.accept(this) for: " + rightExpr.getClass().getName() + ". Current scope: " + (this.currentScope != null ? System.identityHashCode(this.currentScope) : "null"));
            rightExpr.accept(this);
            System.out.println("[CHECK_ASSIGN_IF] After rightExpr.accept(this). Current scope: " + (this.currentScope != null ? System.identityHashCode(this.currentScope) : "null"));
            
            try {
                System.out.println("[CHECK_ASSIGN_TRY] Entered try block.");
                Scope tempScope = this.currentScope; // Prefer current visitor scope
                if (tempScope == null && node instanceof AbstractNode) { // Fallback if currentScope is somehow null
                    tempScope = ((AbstractNode)node).getEnclosingScope();
                }
                if (tempScope == null) {
                    this.errors.add(new CompilationError(ErrorType.INTERNAL_ERROR, (node.getSource() != null ? SourceBuilder.toSourceLine(node.getSource(), node.getLine(), node.getCharPosition(), 1) : "<source not available>"), "Cannot determine scope for assignment type checking.", ""));
                    return null;
                }
                System.out.println("[CHECK_ASSIGN_TRY] Before leftExpr.getType(tempScope) for: " + leftExpr.getClass().getName());
                Type leftType = leftExpr.getType(tempScope);
                System.out.println("[CHECK_ASSIGN_TRY] After leftExpr.getType(tempScope). Result: " + leftType);
                System.out.println("[CHECK_ASSIGN_TRY] Before rightExpr.getType(tempScope) for: " + rightExpr.getClass().getName());
                Type rightType = rightExpr.getType(tempScope);
                System.out.println("[CHECK_ASSIGN_TRY] After rightExpr.getType(tempScope). Result: " + rightType);

                System.out.println("[CHECK_ASSIGNMENT] Left Expression ('" + leftExpr + "', class: " + (leftExpr != null ? leftExpr.getClass().getSimpleName() : "null") + ") Type: " + leftType);
                System.out.println("[CHECK_ASSIGNMENT] Right Expression ('" + rightExpr + "', class: " + (rightExpr != null ? rightExpr.getClass().getSimpleName() : "null") + ") Type: " + rightType);
                System.out.println("[CHECK_ASSIGNMENT] Scope for getType: " + (tempScope != null ? tempScope.getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(tempScope)) : "null"));

                boolean typesCompatible = false;
                if (leftType.equals(rightType)) {
                    typesCompatible = true;
                } else if (leftType instanceof PrimitiveType dt && dt.getName().equals("usize") && 
                           rightExpr instanceof NumberLiteral nl && nl.isNonNegative() && 
                           rightType instanceof PrimitiveType at && at.getName().equals("i32")) {
                    typesCompatible = true;
                } else if (leftType instanceof ArrayType ltArray && ltArray.getSize().getValue() == 0 &&
                           rightType instanceof ArrayType rtArray && rtArray.getSize().getValue() == 0 &&
                           rtArray.getElementType() instanceof PrimitiveType ptElem && "i32".equals(ptElem.getName())) {
                    // Allow assigning [] (which is [i32;0]) to any [SomeType;0]
                    typesCompatible = true;
                }

                if (!typesCompatible) {
                    String sourceLine = node.getSource() != null ? 
                                        SourceBuilder.toSourceLine(node.getSource(), node.getLine(), node.getCharPosition(), 1) : // Assignment operator '=' has length 1
                                        "<source not available>";
                    errors.add(new CompilationError(
                        ErrorType.TYPE_MISMATCH,
                        sourceLine,
                        "Type mismatch: cannot assign " + rightType + " to " + leftType,
                        "Expected: " + leftType + ", Actual: " + rightType
                    ));
                }
            } catch (FailedCheckException e) {
                if (e.getErrors() != null) {
                    this.errors.addAll(e.getErrors());
                } else {
                     this.errors.add(new CompilationError(ErrorType.INTERNAL_ERROR, (node.getSource() != null ? SourceBuilder.toSourceLine(node.getSource(), node.getLine(), node.getCharPosition(), 1) : "<source not available>"), e.getMessage() != null ? e.getMessage() : "Type resolution failed during assignment", ""));
                }
            } catch (Exception e) {
                this.errors.add(new CompilationError(ErrorType.INTERNAL_ERROR, (node.getSource() != null ? SourceBuilder.toSourceLine(node.getSource(), node.getLine(), node.getCharPosition(), 1) : "<source not available>"), "Error during assignment type checking: " + (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()), ""));
            }
        }
        return null;
    }

    @Override
    public Void visit(Scope scope) {
        logger.debug("ENTERING Check.visit(Scope): {} | children: {}", (scope != null ? scope.getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(scope)) + " " + scope.getSourceInfo() : "null"), (scope != null ? scope.getChildren().stream().map(ch -> ch != null ? ch.getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(ch)) + " " + ch.getSourceInfo() : "null").toList() : "N/A"));
        Scope previousScope = this.currentScope;
        this.currentScope = scope;
        for (Node child : scope.getChildren()) {
            if (child != null) { // Explicitly check for null children
                child.accept(this);
            }
        }
        this.currentScope = previousScope;
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
