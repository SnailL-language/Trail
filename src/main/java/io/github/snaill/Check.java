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
 * Class for checking types and other static checks of the AST.
 */
public class Check implements ASTVisitor<Void> {
    private static final Logger logger = LoggerFactory.getLogger(Check.class);
    private Scope currentScope;
    private final List<CompilationError> errors = new ArrayList<>();

    /**
     * Checks the AST for type correctness and other static errors.
     * Collects errors in the internal list.
     * @param node the root node of the AST
     * @return the list of found compilation errors
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
     * Returns the list of found errors.
     * @return the list of errors
     */
    public List<CompilationError> getErrors() {
        return errors;
    }

    @Override
    public Void visit(FunctionDeclaration node) {
        Scope previousScope = this.currentScope;
        logger.debug("ENTERING FunctionDeclaration.visit for function '{}'. Old scope: {}. New scope (function body): {}", node.getName(), (previousScope != null ? previousScope.getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(previousScope)) : "null"), (node.getBody() != null ? node.getBody().getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(node.getBody())) : "null"));

        this.currentScope = node.getBody(); // The function's body is its scope

        // Parameters are part of the function's scope (body) and should have been added during AST building.
        // We can optionally visit them if Parameter.visit needs to do something specific.
        if (node.getParameters() != null) {
            for (Parameter param : node.getParameters()) {
                param.accept(this); // Assuming Parameter.visit() is mostly a no-op or for specific checks
            }
        }

        // Check the function body with the function's scope active
        if (node.getBody() != null) {
            node.getBody().accept(this);
        }

        logger.debug("EXITING FunctionDeclaration.visit for function '{}'. Restoring old scope: {}", node.getName(), (previousScope != null ? previousScope.getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(previousScope)) : "null"));
        this.currentScope = previousScope; // Restore the outer scope
        return null;
    }

    @Override
    public Void visit(VariableDeclaration node) {
        // Define the variable in the current scope first
        if (this.currentScope != null) {
            try {
                this.currentScope.addDeclaration(node);
            } catch (FailedCheckException e) {
                if (e.getErrors() != null) {
                    this.errors.addAll(e.getErrors());
                } else {
                    this.errors.add(new CompilationError(ErrorType.INTERNAL_ERROR, (node.getSource() != null ? SourceBuilder.toSourceLine(node.getSource(), node.getLine(), node.getCharPosition(), 1) : "<source not available>"), e.getMessage() != null ? e.getMessage() : "Failed to add variable declaration", ""));
                }
                // Potentially return early if declaration fails, as further checks might be invalid
                return null; 
            }
        } else {
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
        Expression left = node != null ? node.getLeft() : null;
        Expression right = node != null ? node.getRight() : null;

        if (left != null && right != null) {
            left.accept(this);
            right.accept(this);

            try {
                Scope tempScope = this.currentScope; // Prefer current visitor scope
                if (tempScope == null && node instanceof AbstractNode) { // Fallback if currentScope is somehow null
                    tempScope = ((AbstractNode)node).getEnclosingScope();
                }
                if (tempScope == null) {
                    this.errors.add(new CompilationError(ErrorType.INTERNAL_ERROR, (node.getSource() != null ? SourceBuilder.toSourceLine(node.getSource(), node.getLine(), node.getCharPosition(), 1) : "<source not available>"), "Cannot determine scope for assignment type checking.", ""));
                    return null;
                }
                Type leftType = left.getType(tempScope);
                Type rightType = right.getType(tempScope);

                boolean typesCompatible = false;
                if (leftType.equals(rightType)) {
                    typesCompatible = true;
                } else if (leftType instanceof PrimitiveType dt && dt.getName().equals("usize") && 
                           right instanceof NumberLiteral nl && nl.isNonNegative() && 
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
        // Check the left and right sides of the binary expression
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
        // Check the argument of the unary expression
        if (node.getArgument() != null) {
            node.getArgument().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(IfStatement node) {
        // Check the condition and bodies of the if and else branches
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
        // Check the condition and body of the while loop
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
        logger.debug("ENTERING ForLoop.visit for loop at {}:{}. Current scope: {}", node.getLine(), node.getCharPosition(), (this.currentScope != null ? this.currentScope.getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this.currentScope)) : "null"));
        try {
            // ForLoop.check() handles its own internal scope creation and checking of its components.
            // It needs the current (enclosing) scope to establish its parent.
            node.check(this.currentScope);
        } catch (FailedCheckException e) {
            if (e.getErrors() != null && !e.getErrors().isEmpty()) {
                this.errors.addAll(e.getErrors());
                for (CompilationError err : e.getErrors()) {
                    logger.warn("Error from ForLoop.check() for loop at {}:{}: {}", node.getLine(), node.getCharPosition(), err.getMessage());
                }
            } else {
                // Fallback if FailedCheckException has a general message but no structured errors
                String errorMsg = "Error during for-loop checking at " + node.getLine() + ":" + node.getCharPosition() + ": " + e.getMessage();
                this.errors.add(new CompilationError(ErrorType.INTERNAL_ERROR, SourceBuilder.toSourceLine(node.getSource(), node.getLine(), node.getCharPosition(), 1), errorMsg, ""));
                logger.error(errorMsg, e);
            }
        }
        // Note: We don't need to manage currentScope here because ForLoop.check() uses the passed scope
        // as a parent and manages its own internal scope. The Check visitor's currentScope remains unchanged
        // by this call, which is correct as the ForLoop node itself doesn't define a new scope at the Check visitor's level.
        logger.debug("EXITING ForLoop.visit for loop at {}:{}", node.getLine(), node.getCharPosition());
        return null;
    }

    @Override
    public Void visit(ReturnStatement node) {
        // Check the type of the returned value
        if (node.getReturnable() != null) {
            node.getReturnable().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(BreakStatement node) {
        // No additional checks for break statement
        return null;
    }

    @Override
    public Void visit(VariableReference node) {
        // Basic check for variable reference
        return null;
    }

    @Override
    public Void visit(FunctionCall node) {
        // Check all arguments of the function call
        for (Expression arg : node.getArguments()) {
            if (arg != null) {
                arg.accept(this);
            }
        }
        return null;
    }


    @Override
    public Void visit(ArrayLiteral node) {
        // Check all elements of the array
        for (Expression element : node.getElements()) {
            if (element != null) {
                element.accept(this);
            }
        }
        return null;
    }

    @Override
    public Void visit(Identifier node) {
        // Simplified logging for scope details as getDeclarations() is not directly available for general logging here.
        // Specific declarations can be inspected via localDeclarations if currentScope is an instance of Scope.
        logger.debug("Visiting Identifier: '{}' in scope: {}", node.getName(), (this.currentScope != null ? this.currentScope.getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this.currentScope)) : "null"));
        if (this.currentScope == null) {
            String errorMsg = String.format("Compiler error: current scope not set when checking identifier '%s'.", node.getName());
            this.errors.add(new CompilationError(ErrorType.INTERNAL_ERROR, SourceBuilder.toSourceLine(node.getSource(), node.getLine(), node.getCharPosition(), node.getName().length()), errorMsg, ""));
            logger.error(errorMsg);
            return null;
        }

        // Assuming Identifier node here refers to a variable. Function calls are handled by FunctionCall visitor.
        VariableDeclaration decl = this.currentScope.resolveVariable(node.getName(), node);

        if (decl == null) {
            String errorMsg = String.format("Identifier '%s' (variable) not found in the current scope.", node.getName());
            // Using UNKNOWN_VARIABLE instead of UNDECLARED_VARIABLE
            this.errors.add(new CompilationError(ErrorType.UNKNOWN_VARIABLE, SourceBuilder.toSourceLine(node.getSource(), node.getLine(), node.getCharPosition(), node.getName().length()), errorMsg, ""));
            // Simplified logging for scope details
            logger.warn("{} Scope: {}@{}", errorMsg, this.currentScope.getClass().getSimpleName(), Integer.toHexString(System.identityHashCode(this.currentScope)));
        } else {
            logger.debug("Identifier '{}' resolved to VariableDeclaration: {}", node.getName(), decl.getName());
            // Further type checks or usage checks can be added here if needed
        }
        return null;
    }

    @Override
    public Void visit(NumberLiteral node) {
        // Проверка для числового литерала не требуется
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
