package io.github.snaill.ast;

import io.github.snaill.parser.SnailParser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import io.github.snaill.result.ErrorType;


public class ASTReflectionBuilder implements ASTBuilder {

    @Override
    public Node build(SnailParser.ProgramContext ctx) throws io.github.snaill.exception.FailedCheckException {
        
        // Сначала создаем корневую область видимости с пустым списком statements
        List<Statement> statements = new ArrayList<>();
        Scope rootScope = new Scope(statements, null);
        
        // Собираем все глобальные переменные (до первой функции)
        int i = 0;
        while (i < ctx.getChildCount()) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof SnailParser.VariableDeclarationContext varCtx) {
                VariableDeclaration globalVar = parseVariableDeclaration(varCtx, rootScope);
                if (globalVar != null) {
                    rootScope.addDeclaration(globalVar); // Add to rootScope's symbol table
                    statements.add(globalVar); // Add to AST children list for rootScope
                }
                i++;
            } else {
                break;
            }
        }
        
        // Затем все функции (минимум одна по грамматике)
        for (; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof SnailParser.FuncDeclarationContext funcCtx) {
                // Передаем rootScope как родительскую область видимости
                Statement func = (Statement) parseFuncDeclaration(funcCtx, rootScope);
                if (func != null) statements.add(func);
            }
        }
        
        // Обновляем children в rootScope, хотя это уже должно быть сделано автоматически
        // Явно приводим List<Statement> к Collection<Node>
        rootScope.setChildren(new ArrayList<>(statements));
        
        return rootScope;
    }

    private Node parseStatement(SnailParser.StatementContext ctx, Scope parent) throws io.github.snaill.exception.FailedCheckException {
        if (ctx.variableDeclaration() != null) {
            return parseVariableDeclaration(ctx.variableDeclaration(), parent);
        }
        if (ctx.forLoop() != null) {
            return parseForLoop(ctx.forLoop(), parent);
        }
        if (ctx.funcDeclaration() != null) {
            // Allow nested function declarations
            return parseFuncDeclaration(ctx.funcDeclaration(), parent);
        }
        if (ctx.whileLoop() != null) {
            return parseWhileLoop(ctx.whileLoop(), parent);
        }
        if (ctx.ifCondition() != null) {
            return parseIfCondition(ctx.ifCondition(), parent);
        }
        if (ctx.breakStatement() != null) {
            return parseBreakStatement(ctx.breakStatement());
        }
        if (ctx.returnStatement() != null) {
            return parseReturnStatement(ctx.returnStatement(), parent);
        }
        if (ctx.expression() != null) {
            Expression expr = (Expression) parseExpression(ctx.expression(), parent);
            if (expr == null) {
                String before = ctx.getStart() != null ?
                    io.github.snaill.ast.SourceBuilder.toSourceLine(ctx.getStart().getInputStream().toString(), ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), ctx.getText().length()) :
                    io.github.snaill.ast.SourceBuilder.toSourceCode(ctx);
                throw new io.github.snaill.exception.FailedCheckException(
                    new io.github.snaill.result.CompilationError(
                        io.github.snaill.result.ErrorType.UNKNOWN_TYPE,
                        before,
                        "Empty expression",
                        ""
                    ).toString()
                );
            }
            ExpressionStatement stmt = new ExpressionStatement(expr);
            if (ctx.getStart() != null) {
                stmt.setSourceInfo(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), ctx.getStart().getInputStream().toString());
            }
            return stmt;
        }
        // Если statement нераспознан — выбрасываем ошибку
        String before = ctx.getStart() != null ?
            io.github.snaill.ast.SourceBuilder.toSourceLine(ctx.getStart().getInputStream().toString(), ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), ctx.getText().length()) :
            io.github.snaill.ast.SourceBuilder.toSourceCode(ctx);
        throw new io.github.snaill.exception.FailedCheckException(
            new io.github.snaill.result.CompilationError(
                io.github.snaill.result.ErrorType.UNKNOWN_TYPE,
                before,
                "Unknown or empty statement",
                ""
            ).toString()
        );
    }

    private VariableDeclaration parseVariableDeclaration(SnailParser.VariableDeclarationContext ctx, Scope parentScopeContext) throws io.github.snaill.exception.FailedCheckException {
        String name = ctx.IDENTIFIER().getText();
        Type type = (Type) parseType(ctx.type(), parentScopeContext);
        // Pass parentScopeContext for resolving expressions within the variable's own scope context
        Expression expr = (Expression) parseExpression(ctx.expression(), parentScopeContext);
        VariableDeclaration varDecl = new VariableDeclaration(name, type, expr);
        // Setting the enclosing scope and adding to localDeclarations will be handled by the calling Scope's addDeclaration method.
        // Source info is set here as it's directly related to the parsing context of the declaration itself.
        if (ctx.IDENTIFIER() != null && ctx.IDENTIFIER().getSymbol() != null && ctx.start != null && ctx.start.getInputStream() != null) {
            varDecl.setSourceInfo(ctx.IDENTIFIER().getSymbol().getLine(), ctx.IDENTIFIER().getSymbol().getCharPositionInLine(), ctx.start.getInputStream().toString());
        } else if (ctx.getStart() != null && ctx.start.getInputStream() != null) { // Fallback if IDENTIFIER symbol is null for some reason
            varDecl.setSourceInfo(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), ctx.start.getInputStream().toString());
        }
        return varDecl;
    }

    private Node parseFuncDeclaration(SnailParser.FuncDeclarationContext ctx, Scope parent) throws io.github.snaill.exception.FailedCheckException {
        String name = ctx.IDENTIFIER().getText();
        List<Parameter> params = ctx.paramList() != null ?
                parseParamList(ctx.paramList(), parent) : List.of();
        Type returnType;
        if (ctx.type() != null) {
            returnType = (Type) parseType(ctx.type(), parent); // 'parent' is the scope containing the function declaration
        } else {
            returnType = new PrimitiveType("void");
            if (returnType instanceof AbstractNode rn) {
                 rn.setEnclosingScope(parent); // Scope where 'void' is effectively used/declared
                 // Set source info for the Type node itself, if available and makes sense
                 // For a default void, it might point to the function signature or be omitted.
                 if (ctx.IDENTIFIER() != null && ctx.IDENTIFIER().getSymbol() != null) { // Use function name as anchor
                    rn.setSourceInfo(ctx.IDENTIFIER().getSymbol().getLine(), ctx.IDENTIFIER().getSymbol().getCharPositionInLine(), parent.getSource());
                 } else if (ctx.getStart() != null) {
                    rn.setSourceInfo(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), parent.getSource());
                 }
            }
        }
        // Тело функции — отдельный scope с доступом к глобальным переменным через родительскую область видимости
        // Параметры НЕ добавляем как VariableDeclaration, они обрабатываются в методе resolveVariable
        Scope funcScope = new Scope(new ArrayList<>(), parent, null);
        FunctionDeclaration funcDecl = new FunctionDeclaration(name, params, returnType, funcScope);
        Scope body = parseScope(ctx.scope(), funcScope, funcDecl);
        funcScope.setChildren(body.getChildren());
        return funcDecl;
    }

    private List<Parameter> parseParamList(SnailParser.ParamListContext ctx, Scope containingScope) {
        return ctx.param().stream()
                .map(param -> (Parameter) parseParam(param, containingScope))
                .collect(Collectors.toList());
    }

    private Node parseParam(SnailParser.ParamContext ctx, Scope containingScope) {
        String name = ctx.IDENTIFIER().getText();
        Type type;
        try {
            type = (Type) parseType(ctx.type(), containingScope);
        } catch (io.github.snaill.exception.FailedCheckException e) {
            throw new RuntimeException(e);
        }
        Parameter paramNode = new Parameter(name, type);
        // The Parameter node itself (representing the variable 'name') lives in the function's body scope.
        // However, its type ('type') is resolved in the 'containingScope' (scope of function declaration).
        // The enclosing scope for the Parameter node (as a declaration) will be set when it's added to the FunctionDeclaration's scope.
        // For now, ensure the Parameter node has source info.
        if (ctx.IDENTIFIER() != null && ctx.IDENTIFIER().getSymbol() != null) {
            paramNode.setSourceInfo(ctx.IDENTIFIER().getSymbol().getLine(), ctx.IDENTIFIER().getSymbol().getCharPositionInLine(), containingScope.getSource());
        } else if (ctx.getStart() != null) {
            paramNode.setSourceInfo(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), containingScope.getSource());
        }
        // The 'type' node within Parameter already has its enclosingScope set by parseType.
        return paramNode;
    }

    private Scope parseScope(SnailParser.ScopeContext ctx, Scope parent) throws io.github.snaill.exception.FailedCheckException {
        return parseScope(ctx, parent, null);
    }

    private Scope parseScope(SnailParser.ScopeContext ctx, Scope parent, FunctionDeclaration enclosingFunction) throws io.github.snaill.exception.FailedCheckException {
        if (ctx == null) {
            String errorSource = "Unknown location (ScopeContext was null)";
            String problemLocation = "a code block";
            if (enclosingFunction != null) {
                problemLocation = "the body of function '" + enclosingFunction.getName() + "'";
                if (enclosingFunction.getLine() != -1 && enclosingFunction.getSource() != null) {
                     errorSource = "function '" + enclosingFunction.getName() + "' declared at line " + enclosingFunction.getLine() + " in " + enclosingFunction.getSource();
                } else {
                    errorSource = "function '" + enclosingFunction.getName() + "'";
                }
            } else if (parent != null && parent.getSource() != null && parent.getLine() != -1) {
                 errorSource = "a scope starting near line " + parent.getLine() + " in " + parent.getSource();
            }

            throw new io.github.snaill.exception.FailedCheckException(
                new io.github.snaill.result.CompilationError(
                    io.github.snaill.result.ErrorType.SYNTAX_ERROR,
                    errorSource,
                    "Syntax error: Expected " + problemLocation + " (e.g., using '{...}') but it was missing or malformed.",
                    "Ensure the block is correctly defined with '{' and '}' or check for syntax errors preventing its recognition."
                ).toString()
            );
        }
        Scope currentScope = new Scope(new ArrayList<>(), parent, enclosingFunction);
        List<Node> children = new ArrayList<>();
        for (var stmtCtx : ctx.statement()) {
            Statement stmt;
            if (stmtCtx.variableDeclaration() != null) {
                VariableDeclaration varDecl = parseVariableDeclaration(stmtCtx.variableDeclaration(), currentScope);
                if (varDecl != null) {
                    currentScope.addDeclaration(varDecl); // Add to current scope's symbol table
                    stmt = varDecl;
                } else {
                    stmt = null; // Should not happen if parseVariableDeclaration throws on error
                }
            } else if (stmtCtx.funcDeclaration() != null) {
                // Temporarily commented out to isolate other compilation errors
                /*
                String errorSourceFuncScope = stmtCtx.funcDeclaration().getStart() != null ?
                    io.github.snaill.ast.SourceBuilder.toSourceLine(stmtCtx.funcDeclaration().getStart().getInputStream().toString(), stmtCtx.funcDeclaration().getStart().getLine(), stmtCtx.funcDeclaration().getStart().getCharPositionInLine(), stmtCtx.funcDeclaration().getText().length()) :
                    io.github.snaill.ast.SourceBuilder.toSourceCode(stmtCtx.funcDeclaration());
                throw new io.github.snaill.exception.FailedCheckException(
                    new io.github.snaill.result.CompilationError(
                        ErrorType.SEMANTIC_ERROR,
                        errorSourceFuncScope,
                        "Function declarations are not allowed inside blocks.",
                        "Define functions only at the top level or directly within the global scope."
                    ).toString()
                );
                */
            } else {
                stmt = (Statement) parseStatement(stmtCtx, currentScope);
            }
            if (stmt != null) {
                children.add(stmt);
                currentScope.setChildren(new ArrayList<>(children));
            }
        }
        return currentScope;
    }
        
    private Node parseForLoop(SnailParser.ForLoopContext ctx, Scope parent) throws io.github.snaill.exception.FailedCheckException {
        // Create a new scope for the for-loop itself. This scope will contain the loop variable.
        Scope forScope = new Scope(new ArrayList<>(), parent, parent.getEnclosingFunction());

        // Parse the initializer. The VariableDeclaration node is created with its initializer resolved in the PARENT scope.
        VariableDeclaration loopVarDecl = (VariableDeclaration) parseVariableDeclaration(ctx.variableDeclaration(), parent);
        if (loopVarDecl != null) {
             // Add the loop variable declaration to the forScope, making it visible within the loop.
             forScope.addDeclaration(loopVarDecl);
        }
        // This is the initialization statement for the ForLoop node.

        // Parse condition using the forScope (loop variable is visible here).
        Expression condition = (Expression) parseExpression(ctx.expression(0), forScope);

        // Parse step using the forScope (loop variable is visible here).
        Expression step = (Expression) parseExpression(ctx.expression(1), forScope);

        // Parse the body scope. Its parent is the forScope.
        Scope body = parseScope(ctx.scope(), forScope);
        
        ForLoop forLoopNode = new ForLoop(loopVarDecl, condition, step, body);
        if (ctx.getStart() != null && ctx.getStart().getInputStream() != null) {
            forLoopNode.setSourceInfo(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), ctx.getStart().getInputStream().toString());
        }
        return forLoopNode;
    }

    private Node parseWhileLoop(SnailParser.WhileLoopContext ctx, Scope parent) throws io.github.snaill.exception.FailedCheckException {
        // Condition is resolved in the parent scope
        Expression condition = (Expression) parseExpression(ctx.expression(), parent);
        if (condition == null) {
            String before = ctx.getStart() != null ?
                io.github.snaill.ast.SourceBuilder.toSourceLine(ctx.getStart().getInputStream().toString(), ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), ctx.getText().length()) :
                io.github.snaill.ast.SourceBuilder.toSourceCode(ctx);
            throw new io.github.snaill.exception.FailedCheckException(
                new io.github.snaill.result.CompilationError(
                    io.github.snaill.result.ErrorType.UNKNOWN_TYPE,
                    before,
                    "Empty or invalid while condition",
                    ""
                ).toString()
            );
        }

        // Create a new scope for the while loop's body
        Scope whileBodyScopeContext = new Scope(new ArrayList<>(), parent, parent.getEnclosingFunction());
        Scope body = parseScope(ctx.scope(), whileBodyScopeContext); // Body parsed in this new scope
        
        WhileLoop whileLoop = new WhileLoop(condition, body);
        if (ctx.getStart() != null && ctx.getStart().getInputStream() != null) {
            whileLoop.setSourceInfo(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), ctx.getStart().getInputStream().toString());
        }
        return whileLoop;
    }

    private Node parseIfCondition(SnailParser.IfConditionContext ctx, Scope parent) throws io.github.snaill.exception.FailedCheckException {
        Expression condition = (Expression) parseExpression(ctx.expression(), parent);
        if (condition == null) {
            String before = ctx.getStart() != null ?
                io.github.snaill.ast.SourceBuilder.toSourceLine(ctx.getStart().getInputStream().toString(), ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), ctx.getText().length()) :
                io.github.snaill.ast.SourceBuilder.toSourceCode(ctx);
            throw new io.github.snaill.exception.FailedCheckException(
                new io.github.snaill.result.CompilationError(
                    io.github.snaill.result.ErrorType.UNKNOWN_TYPE,
                    before,
                    "Empty or invalid if condition",
                    ""
                ).toString()
            );
        }

        // Create a new scope for the 'then' branch
        Scope thenScopeContext = new Scope(new ArrayList<>(), parent, parent.getEnclosingFunction());
        Scope thenScope = ctx.scope(0) != null ? parseScope(ctx.scope(0), thenScopeContext) : new Scope(new ArrayList<>(), thenScopeContext, parent.getEnclosingFunction());
        if (thenScope == null && ctx.scope(0) != null) { 
             String before = ctx.getStart() != null ?
                io.github.snaill.ast.SourceBuilder.toSourceLine(ctx.getStart().getInputStream().toString(), ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), ctx.getText().length()) :
                io.github.snaill.ast.SourceBuilder.toSourceCode(ctx);
            throw new io.github.snaill.exception.FailedCheckException(
                new io.github.snaill.result.CompilationError(
                    io.github.snaill.result.ErrorType.UNKNOWN_TYPE,
                    before,
                    "Empty or invalid then block in if",
                    ""
                ).toString()
            );
        }

        Scope elseScope = null;
        if (ctx.scope().size() > 1 && ctx.scope(1) != null) {
            Scope elseScopeContext = new Scope(new ArrayList<>(), parent, parent.getEnclosingFunction());
            elseScope = parseScope(ctx.scope(1), elseScopeContext);
            if (elseScope == null) { 
                String before = ctx.getStart() != null ?
                    io.github.snaill.ast.SourceBuilder.toSourceLine(ctx.getStart().getInputStream().toString(), ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), ctx.getText().length()) :
                    io.github.snaill.ast.SourceBuilder.toSourceCode(ctx);
                throw new io.github.snaill.exception.FailedCheckException(
                    new io.github.snaill.result.CompilationError(
                        io.github.snaill.result.ErrorType.UNKNOWN_TYPE,
                        before,
                        "Empty or invalid else block in if",
                        ""
                    ).toString()
                );
            }
        }
        
        IfStatement ifStmt = new IfStatement(condition, thenScope, elseScope);
        if (ctx.getStart() != null && ctx.getStart().getInputStream() != null) {
            ifStmt.setSourceInfo(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), ctx.getStart().getInputStream().toString());
        }
        return ifStmt;
    }

    private Node parseBreakStatement(SnailParser.BreakStatementContext ctx) {
        return new BreakStatement();
    }

    private Node parseReturnStatement(SnailParser.ReturnStatementContext ctx, Scope parent) throws io.github.snaill.exception.FailedCheckException {
        Expression expr = ctx.expression() != null ? (Expression) parseExpression(ctx.expression(), parent) : null;
        ReturnStatement ret = new ReturnStatement(expr);
        if (ctx.getStart() != null) {
            ret.setSourceInfo(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), ctx.getStart().getInputStream().toString());
        }
        return ret;
    }

    private Node parseExpression(SnailParser.ExpressionContext ctx, Scope parent) throws io.github.snaill.exception.FailedCheckException {
        if (ctx == null) {
            return null;
        }
        if (ctx.assigmentExpression() != null) {
            return parseAssigmentExpression(ctx.assigmentExpression(), parent);
        }
        if (ctx.binaryExpression() != null) {
            return parseBinaryExpression(ctx.binaryExpression(), parent);
        }
        if (ctx.unaryExpression() != null) {
            return parseUnaryExpression(ctx.unaryExpression(), parent);
        }
        if (ctx.primaryExpression() != null) {
            return parsePrimaryExpression(ctx.primaryExpression(), parent);
        }
        // Попытка разобрать как бинарное выражение, если есть два подвыражения и оператор
        if (ctx.getChildCount() == 3 && ctx.getChild(1) instanceof TerminalNode) {
            Expression left = (Expression) parseExpression((SnailParser.ExpressionContext) ctx.getChild(0), parent);
            String op = ctx.getChild(1).getText();
            Expression right = (Expression) parseExpression((SnailParser.ExpressionContext) ctx.getChild(2), parent);
            return new BinaryExpression(left, op, right);
        }
        if (ctx.getChildCount() == 3 && ctx.getChild(0).getText().equals("(") && ctx.getChild(2).getText().equals(")")) {
            return parseExpression(ctx.expression(), parent);
        }
        return null;
    }

    private Node parseAssigmentExpression(SnailParser.AssigmentExpressionContext ctx, Scope parent) throws io.github.snaill.exception.FailedCheckException {
        Expression left;
        // ctx.identifier() is the SnailParser.IdentifierContext.
        // parseIdentifier can handle both variableIdentifier and arrayElement nested within IdentifierContext,
        // and it correctly sets the enclosingScope on the created Identifier/ArrayElement node.
        left = (Expression) parseIdentifier(ctx.identifier(), parent);

        String op = ctx.assigmentOperator.getText();
        Expression right = (Expression) parseExpression(ctx.expression(), parent);
        AssignmentExpression assign;

        if (op.equals("=")) {
            assign = new AssignmentExpression(left, right);
        } else {
            String binOp = switch (op) {
                case "+=" -> "+";
                case "-=" -> "-";
                case "*=" -> "*";
                case "/=" -> "/";
                default -> throw new RuntimeException("Unknown assignment operator: " + op);
            };
            // The 'left' expression is reused. It already has its enclosingScope set by parseIdentifier.
            BinaryExpression compoundRhs = new BinaryExpression(left, binOp, right);
            if (parent != null) {
                compoundRhs.setEnclosingScope(parent); // Set scope for the new BinaryExpression node
            }
            // Attempt to set source info for the implicit binary operation
            // Check if left node has valid source info (line != -1)
            if (left.getLine() != -1 && left.getSource() != null) { 
                compoundRhs.setSourceInfo(left.getLine(), left.getCharPosition(), left.getSource());
            } else if (ctx.assigmentOperator != null && ctx.start != null && ctx.start.getInputStream() != null) { 
                 compoundRhs.setSourceInfo(ctx.assigmentOperator.getLine(), ctx.assigmentOperator.getCharPositionInLine(), ctx.start.getInputStream().toString());
            }
            assign = new AssignmentExpression(left, compoundRhs);
        }

        // Set source info for the AssignmentExpression node itself
        if (ctx.assigmentOperator != null && ctx.start != null && ctx.start.getInputStream() != null) {
            assign.setSourceInfo(ctx.assigmentOperator.getLine(), ctx.assigmentOperator.getCharPositionInLine(), ctx.start.getInputStream().toString());
        }
        
        // Set enclosing scope for the AssignmentExpression node
        if (parent != null) {
            assign.setEnclosingScope(parent);
        }
        return assign;
    }

    private Node parseBinaryExpression(SnailParser.BinaryExpressionContext ctx, Scope parent) throws io.github.snaill.exception.FailedCheckException {
        if (ctx == null) {
            return null;
        }
        // ANTLR может вложить primaryExpression, но чаще всего есть expression(0) и expression(1)
        Expression left = null;
        Expression right = null;
        String op = null;
        if (ctx.expression().size() == 2) {
            left = (Expression) parseExpression(ctx.expression(0), parent);
            right = (Expression) parseExpression(ctx.expression(1), parent);
            op = ctx.binaryOperator != null ? ctx.binaryOperator.getText() : ctx.getChild(1).getText();
        } else if (ctx.primaryExpression() != null && ctx.expression().size() == 1) {
            left = (Expression) parsePrimaryExpression(ctx.primaryExpression(), parent);
            right = (Expression) parseExpression(ctx.expression(0), parent);
            op = ctx.binaryOperator != null ? ctx.binaryOperator.getText() : ctx.getChild(1).getText();
        }
        if (left != null && right != null && op != null) {
            BinaryExpression bin = new BinaryExpression(left, op, right);
            if (ctx.binaryOperator != null && ctx.start != null && ctx.start.getInputStream() != null) {
                bin.setSourceInfo(ctx.binaryOperator.getLine(), ctx.binaryOperator.getCharPositionInLine(), ctx.start.getInputStream().toString());
            }
            if (parent != null) {
                bin.setEnclosingScope(parent);
            }
            return bin;
        }
        return null;
    }

    private Node parseUnaryExpression(SnailParser.UnaryExpressionContext ctx, Scope parent) throws io.github.snaill.exception.FailedCheckException {
        if (ctx == null || ctx.expression() == null) {
            return null;
        }
        String operator = ctx.unaryOperator.getText();
        Expression argument = (Expression) parseExpression(ctx.expression(), parent);
        UnaryExpression unaryExpr = new UnaryExpression(operator, argument);
        if (parent != null) {
            unaryExpr.setEnclosingScope(parent);
        }
        // Set source info for the UnaryExpression, typically from the operator
        if (ctx.unaryOperator != null && ctx.start != null && ctx.start.getInputStream() != null) {
            unaryExpr.setSourceInfo(ctx.unaryOperator.getLine(), ctx.unaryOperator.getCharPositionInLine(), ctx.start.getInputStream().toString());
        }
        return unaryExpr;
    }

    private Node parsePrimaryExpression(SnailParser.PrimaryExpressionContext ctx, Scope parent) throws io.github.snaill.exception.FailedCheckException {
        if (ctx == null) {
            String before = "^";
            throw new io.github.snaill.exception.FailedCheckException(
                new io.github.snaill.result.CompilationError(
                    io.github.snaill.result.ErrorType.UNKNOWN_TYPE,
                    before,
                    "Empty expression",
                    ""
                ).toString()
            );
        }
        if (ctx.literal() != null) {
            return parseLiteral(ctx.literal());
        }
        if (ctx.identifier() != null) {
            return parseIdentifier(ctx.identifier(), parent);
        }
        if (ctx.arrayElement() != null) {
            return parseArrayElement(ctx.arrayElement(), parent);
        }
        if (ctx.functionCall() != null) {
            return parseFunctionCall(ctx.functionCall(), parent);
        }
        if (ctx.arrayLiteral() != null) {
            return parseArrayLiteral(ctx.arrayLiteral(), parent);
        }
        String before = ctx.getStart() != null ?
            io.github.snaill.ast.SourceBuilder.toSourceLine(ctx.getStart().getInputStream().toString(), ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), ctx.getText().length()) :
            io.github.snaill.ast.SourceBuilder.toSourceCode(ctx);
        throw new io.github.snaill.exception.FailedCheckException(
            new io.github.snaill.result.CompilationError(
                io.github.snaill.result.ErrorType.UNKNOWN_TYPE,
                before,
                "Unknown or empty primary expression",
                ""
            ).toString()
        );
    }

    private Node parseLiteral(SnailParser.LiteralContext ctx) throws io.github.snaill.exception.FailedCheckException {
        if (ctx.numberLiteral() != null) {
            return parseNumberLiteral(ctx.numberLiteral());
        } else if (ctx.stringLiteral() != null) {
            return parseStringLiteral(ctx.stringLiteral());
        } else if (ctx.booleanLiteral() != null) {
            return parseBooleanLiteral(ctx.booleanLiteral());
        }
        throw new io.github.snaill.exception.FailedCheckException(
            new io.github.snaill.result.CompilationError(
                io.github.snaill.result.ErrorType.UNKNOWN_TYPE,
                (ctx != null && ctx.getStart() != null) ?
                    io.github.snaill.ast.SourceBuilder.toSourceLine(ctx.getStart().getInputStream().toString(), ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), ctx.getText().length()) :
                    io.github.snaill.ast.SourceBuilder.toSourceCode(ctx),
                "Unknown literal",
                ""
            ).toString()
        );
    }

    private Node parseNumberLiteral(SnailParser.NumberLiteralContext ctx) {
        return new NumberLiteral(Long.parseLong(ctx.NUMBER().getText()));
    }

    private Node parseStringLiteral(SnailParser.StringLiteralContext ctx) {
        String text = ctx.STRING().getText();
        return new StringLiteral(text.substring(1, text.length() - 1));
    }

    private Node parseBooleanLiteral(SnailParser.BooleanLiteralContext ctx) {
        return new BooleanLiteral(ctx.getText().equals("true"));
    }

    private Node parseIdentifier(SnailParser.IdentifierContext ctx, Scope parent) throws io.github.snaill.exception.FailedCheckException {
        if (ctx.variableIdentifier() != null) {
            String name = ctx.variableIdentifier().IDENTIFIER().getText();
            if (parent != null && parent.resolveVariable(name) == null) {
                String before = ctx.getStart() != null ?
                    io.github.snaill.ast.SourceBuilder.toSourceLine(ctx.getStart().getInputStream().toString(), ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), name.length()) :
                    io.github.snaill.ast.SourceBuilder.toSourceCode(ctx);
                throw new io.github.snaill.exception.FailedCheckException(
                    new io.github.snaill.result.CompilationError(
                        io.github.snaill.result.ErrorType.UNKNOWN_VARIABLE,
                        before,
                        "Unknown variable: " + name,
                        ""
                    ).toString()
                );
            }
            Identifier id = new Identifier(name);
            if (parent != null) { // Set the enclosing scope
                id.setEnclosingScope(parent);
            }
            if (ctx.variableIdentifier().IDENTIFIER() != null) {
                id.setSourceInfo(ctx.variableIdentifier().IDENTIFIER().getSymbol().getLine(), ctx.variableIdentifier().IDENTIFIER().getSymbol().getCharPositionInLine(), ctx.start.getInputStream().toString());
            }
            return id;
        } else if (ctx.arrayElement() != null) {
            return parseArrayElement(ctx.arrayElement(), parent);
        }
        throw new io.github.snaill.exception.FailedCheckException(
            new io.github.snaill.result.CompilationError(
                io.github.snaill.result.ErrorType.UNKNOWN_TYPE,
                (ctx != null && ctx.getStart() != null) ?
                    io.github.snaill.ast.SourceBuilder.toSourceLine(ctx.getStart().getInputStream().toString(), ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), ctx.getText().length()) :
                    io.github.snaill.ast.SourceBuilder.toSourceCode(ctx),
                "Invalid identifier",
                ""
            ).toString()
        );
    }

    private Node parseArrayElement(SnailParser.ArrayElementContext ctx, Scope parent) throws io.github.snaill.exception.FailedCheckException {
        Identifier identifier = new Identifier(ctx.IDENTIFIER().getText());
        // Set source info and scope for the identifier part of the array element
        if (ctx.IDENTIFIER() != null && ctx.IDENTIFIER().getSymbol() != null) {
            identifier.setSourceInfo(ctx.IDENTIFIER().getSymbol().getLine(), ctx.IDENTIFIER().getSymbol().getCharPositionInLine(), parent.getSource());
        }
        identifier.setEnclosingScope(parent);

        List<Expression> indices = new ArrayList<>();
        // Обрабатываем все индексы для многомерных массивов
        if (ctx.expression() != null) {
            List<SnailParser.ExpressionContext> expressions = ctx.expression();
            for (SnailParser.ExpressionContext expr : expressions) {
                indices.add((Expression) parseExpression(expr, parent));
            }
        }
        ArrayElement arrElem = new ArrayElement(identifier, indices);
        // Set source info and scope for the entire array element expression
        arrElem.setSourceInfo(ctx.start.getLine(), ctx.start.getCharPositionInLine(), parent.getSource());
        arrElem.setEnclosingScope(parent);
        return arrElem;
    }

    private Node parseFunctionCall(SnailParser.FunctionCallContext ctx, Scope parent) throws io.github.snaill.exception.FailedCheckException {
        if (ctx == null || ctx.IDENTIFIER() == null) {
            String before = ctx != null && ctx.getStart() != null ?
                io.github.snaill.ast.SourceBuilder.toSourceLine(ctx.getStart().getInputStream().toString(), ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), ctx.getText().length()) :
                "^";
            throw new io.github.snaill.exception.FailedCheckException(
                new io.github.snaill.result.CompilationError(
                    io.github.snaill.result.ErrorType.UNKNOWN_TYPE,
                    before,
                    "Empty function call expression",
                    ""
                ).toString()
            );
        }
        String name = ctx.IDENTIFIER().getText();
        List<Expression> args = ctx.argumentList() != null ?
                parseArgumentList(ctx.argumentList(), parent) : List.of();
        FunctionCall call = new FunctionCall(name, args);
        if (ctx.IDENTIFIER() != null && ctx.IDENTIFIER().getSymbol() != null && ctx.start != null && ctx.start.getInputStream() != null) {
            call.setSourceInfo(ctx.IDENTIFIER().getSymbol().getLine(), ctx.IDENTIFIER().getSymbol().getCharPositionInLine(), ctx.start.getInputStream().toString());
        }
        if (parent != null) {
            call.setEnclosingScope(parent);
        }
        return call;
    }

    private List<Expression> parseArgumentList(SnailParser.ArgumentListContext ctx, Scope parent) {
        return ctx.expression().stream()
                .map(expr -> {
                    try {
                        return (Expression) parseExpression(expr, parent);
                    } catch (io.github.snaill.exception.FailedCheckException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    private Node parseArrayLiteral(SnailParser.ArrayLiteralContext ctx, Scope parent) throws io.github.snaill.exception.FailedCheckException {
        List<Expression> elements = new ArrayList<>();
        if (ctx.expression() != null) {
            for (var exprCtx : ctx.expression()) {
                if (exprCtx == null) continue;
                Expression expr = (Expression) parseExpression(exprCtx, parent);
                if (expr == null) continue;
                elements.add(expr);
            }
        }
        return new ArrayLiteral(elements);
    }

    private Node parseType(SnailParser.TypeContext ctx, Scope currentScope) throws io.github.snaill.exception.FailedCheckException {
        if (ctx.primitiveType() != null) {
            return parsePrimitiveType(ctx.primitiveType(), currentScope);
        } else if (ctx.arrayType() != null) {
            return parseArrayType(ctx.arrayType(), currentScope);
        }
        throw new io.github.snaill.exception.FailedCheckException(
            new io.github.snaill.result.CompilationError(
                io.github.snaill.result.ErrorType.UNKNOWN_TYPE,
                (ctx != null && ctx.getStart() != null) ?
                    io.github.snaill.ast.SourceBuilder.toSourceLine(ctx.getStart().getInputStream().toString(), ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), ctx.getText().length()) :
                    io.github.snaill.ast.SourceBuilder.toSourceCode(ctx),
                "Unknown type",
                ""
            ).toString()
        );
    }

    private Node parseArrayType(SnailParser.ArrayTypeContext ctx, Scope currentScope) throws io.github.snaill.exception.FailedCheckException {
        Type elementType = (Type) parseType(ctx.type(), currentScope);
        NumberLiteral size = new NumberLiteral(Long.parseLong(ctx.numberLiteral().getText()));
        // Set source info for NumberLiteral if it's an AbstractNode and needs it
        if (size instanceof AbstractNode sn) {
            sn.setSourceInfo(ctx.numberLiteral().start.getLine(), ctx.numberLiteral().start.getCharPositionInLine(), currentScope.getSource());
            sn.setEnclosingScope(currentScope);
        }
        ArrayType at = new ArrayType(elementType, size);
        at.setSourceInfo(ctx.start.getLine(), ctx.start.getCharPositionInLine(), currentScope.getSource());
        at.setEnclosingScope(currentScope);
        return at;
    }

     private Node parsePrimitiveType(SnailParser.PrimitiveTypeContext ctx, Scope currentScope) {
        // With the current grammar (primitiveType : 'i32' | 'usize' | 'void' | 'string' | 'bool'),
        // IDENTIFIER is not an alternative, so ctx.IDENTIFIER() would not be available.
        // This method will only create PrimitiveType nodes for the predefined keywords.
        // For custom type support, the grammar rule 'primitiveType' or 'type' needs to include IDENTIFIER.
        PrimitiveType pt = new PrimitiveType(ctx.getText());
        pt.setSourceInfo(ctx.start.getLine(), ctx.start.getCharPositionInLine(), currentScope.getSource());
        pt.setEnclosingScope(currentScope);
        return pt;
    }
}