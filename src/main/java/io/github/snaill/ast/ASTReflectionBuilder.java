package io.github.snaill.ast;

import io.github.snaill.parser.SnailParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.github.snaill.ast.SourceBuilder;

public class ASTReflectionBuilder implements ASTBuilder {

    @Override
    public Node build(SnailParser.ProgramContext ctx) throws io.github.snaill.exception.FailedCheckException {
        // Сначала собираем все глобальные переменные (до первой функции)
        List<Statement> statements = new ArrayList<>();
        int i = 0;
        while (i < ctx.getChildCount()) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof SnailParser.VariableDeclarationContext varCtx) {
                Statement var = (Statement) parseVariableDeclaration(varCtx, null);
                if (var != null) statements.add(var);
                i++;
            } else {
                break;
            }
        }
        // Затем все функции (минимум одна по грамматике)
        for (; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof SnailParser.FuncDeclarationContext funcCtx) {
                Statement func = (Statement) parseFuncDeclaration(funcCtx, null);
                if (func != null) statements.add(func);
            }
        }
        Scope rootScope = new Scope(statements, null);
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
            throw new RuntimeException("Function declarations are not allowed inside blocks");
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

    private Node parseContext(ParseTree ctx, Class<?> contextClass) {
        if (ctx == null) {
            return null;
        }
        String methodName = "parse" + contextClass.getSimpleName().replace("Context", "");
        try {
            Method method = this.getClass().getDeclaredMethod(methodName, contextClass);
            return (Node) method.invoke(this, ctx);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("No parse method for " + contextClass.getSimpleName(), e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Failed to invoke parse method for " + contextClass.getSimpleName(), e);
        }
    }

    private Node parseVariableDeclaration(SnailParser.VariableDeclarationContext ctx, Scope parent) throws io.github.snaill.exception.FailedCheckException {
        String name = ctx.IDENTIFIER().getText();
        Type type = (Type) parseType(ctx.type());
        Expression expr = (Expression) parseExpression(ctx.expression(), parent);
        VariableDeclaration varDecl = new VariableDeclaration(name, type, expr);
        if (parent != null) {
            List<Node> children = new ArrayList<>(parent.getChildren());
            children.add(varDecl);
            parent.setChildren(children);
        }
        if (ctx.IDENTIFIER() != null) {
            varDecl.setSourceInfo(ctx.IDENTIFIER().getSymbol().getLine(), ctx.IDENTIFIER().getSymbol().getCharPositionInLine(), ctx.start.getInputStream().toString());
        }
        return varDecl;
    }

    private Node parseFuncDeclaration(SnailParser.FuncDeclarationContext ctx, Scope parent) throws io.github.snaill.exception.FailedCheckException {
        String name = ctx.IDENTIFIER().getText();
        List<Parameter> params = ctx.paramList() != null ?
                parseParamList(ctx.paramList()) : List.of();
        Type returnType = ctx.type() != null ?
                (Type) parseType(ctx.type()) : new PrimitiveType("void");
        // Тело функции — отдельный scope, параметры НЕ добавляем как VariableDeclaration
        Scope funcScope = new Scope(new ArrayList<>(), null, null);
        FunctionDeclaration funcDecl = new FunctionDeclaration(name, params, returnType, funcScope);
        Scope body = parseScope(ctx.scope(), funcScope, funcDecl);
        funcScope.setChildren(body.getChildren());
        return funcDecl;
    }

    private List<Parameter> parseParamList(SnailParser.ParamListContext ctx) {
        return ctx.param().stream()
                .map(param -> (Parameter) parseParam(param))
                .collect(Collectors.toList());
    }

    private Node parseParam(SnailParser.ParamContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        Type type;
        try {
            type = (Type) parseType(ctx.type());
        } catch (io.github.snaill.exception.FailedCheckException e) {
            throw new RuntimeException(e);
        }
        return new Parameter(name, type);
    }

    private Scope parseScope(SnailParser.ScopeContext ctx, Scope parent) throws io.github.snaill.exception.FailedCheckException {
        return parseScope(ctx, parent, null);
    }

    private Scope parseScope(SnailParser.ScopeContext ctx, Scope parent, FunctionDeclaration enclosingFunction) throws io.github.snaill.exception.FailedCheckException {
        Scope currentScope = new Scope(new ArrayList<>(), parent, enclosingFunction);
        List<Node> children = new ArrayList<>();
        for (var stmtCtx : ctx.statement()) {
            Statement stmt;
            if (stmtCtx.variableDeclaration() != null) {
                stmt = (Statement) parseVariableDeclaration(stmtCtx.variableDeclaration(), currentScope);
            } else if (stmtCtx.funcDeclaration() != null) {
                throw new RuntimeException("Function declarations are not allowed inside blocks");
            } else {
                stmt = (Statement) parseStatement(stmtCtx, currentScope);
            }
            if (stmt != null) {
                children.add(stmt);
                currentScope.setChildren(new ArrayList<>(children));
            }
        }
        currentScope.setChildren(children);
        return currentScope;
    }

    private Node parseForLoop(SnailParser.ForLoopContext ctx, Scope parent) throws io.github.snaill.exception.FailedCheckException {
        // Переменная цикла видна только внутри тела for
        Scope forScope = new Scope(new ArrayList<>(), parent);
        List<Node> children = new ArrayList<>();
        VariableDeclaration declaration = (VariableDeclaration) parseVariableDeclaration(ctx.variableDeclaration(), forScope);
        children.add(declaration);
        if (ctx.scope() != null) {
            for (var stmtCtx : ctx.scope().statement()) {
                Statement stmt;
                if (stmtCtx.variableDeclaration() != null) {
                    stmt = (Statement) parseVariableDeclaration(stmtCtx.variableDeclaration(), forScope);
                } else if (stmtCtx.funcDeclaration() != null) {
                    throw new RuntimeException("Function declarations are not allowed inside blocks");
                } else {
                    stmt = (Statement) parseStatement(stmtCtx, forScope);
                }
                if (stmt != null) {
                    children.add(stmt);
                }
            }
        }
        forScope.setChildren(children);
        Expression condition = (Expression) parseExpression(ctx.expression(0), forScope);
        Expression step = (Expression) parseExpression(ctx.expression(1), forScope);
        return new ForLoop(forScope, condition, step);
    }

    private Node parseWhileLoop(SnailParser.WhileLoopContext ctx, Scope parent) throws io.github.snaill.exception.FailedCheckException {
        Expression condition = (Expression) parseExpression(ctx.expression(), parent);
        Scope bodyScope = ctx.scope() != null ? parseScope(ctx.scope(), parent) : new Scope(new ArrayList<>(), parent);
        return new WhileLoop(condition, bodyScope);
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
        Scope thenScope = ctx.scope(0) != null ? parseScope(ctx.scope(0), parent) : null;
        if (thenScope == null) {
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
        if (ctx.scope().size() > 1) {
            elseScope = ctx.scope(1) != null ? parseScope(ctx.scope(1), parent) : null;
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
        return new IfStatement(condition, thenScope, elseScope);
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
        if (ctx.identifier().variableIdentifier() != null) {
            String name = ctx.identifier().variableIdentifier().IDENTIFIER().getText();
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
            left = new Identifier(name);
        } else if (ctx.identifier().arrayElement() != null) {
            left = (Expression) parseArrayElement(ctx.identifier().arrayElement(), parent);
        } else {
            throw new RuntimeException("Invalid left-hand side of assignment");
        }
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
            assign = new AssignmentExpression(left, new BinaryExpression(left, binOp, right));
        }
        if (ctx.assigmentOperator != null) {
            assign.setSourceInfo(ctx.assigmentOperator.getLine(), ctx.assigmentOperator.getCharPositionInLine(), ctx.start.getInputStream().toString());
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
            if (ctx.binaryOperator != null) {
                bin.setSourceInfo(ctx.binaryOperator.getLine(), ctx.binaryOperator.getCharPositionInLine(), ctx.start.getInputStream().toString());
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
        return new UnaryExpression(operator, argument);
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

    private Node parseNumberLiteral(SnailParser.NumberLiteralContext ctx) throws io.github.snaill.exception.FailedCheckException {
        return new NumberLiteral(Long.parseLong(ctx.NUMBER().getText()));
    }

    private Node parseStringLiteral(SnailParser.StringLiteralContext ctx) throws io.github.snaill.exception.FailedCheckException {
        String text = ctx.STRING().getText();
        return new StringLiteral(text.substring(1, text.length() - 1));
    }

    private Node parseBooleanLiteral(SnailParser.BooleanLiteralContext ctx) throws io.github.snaill.exception.FailedCheckException {
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
        List<Expression> indices = new ArrayList<>();
        if (ctx.expression() != null) {
            indices.add((Expression) parseExpression(ctx.expression(), parent));
        } else if (ctx.numberLiteral() != null) {
            indices.add((Expression) parseNumberLiteral(ctx.numberLiteral()));
        }
        if (ctx.arrayElement() != null) {
            ArrayElement inner = (ArrayElement) parseArrayElement(ctx.arrayElement(), parent);
            identifier = (Identifier) inner.getIdentifier();
            indices.addAll(inner.getDims());
        }
        ArrayElement arrElem = new ArrayElement(identifier, indices);
        if (ctx.IDENTIFIER() != null) {
            arrElem.setSourceInfo(ctx.IDENTIFIER().getSymbol().getLine(), ctx.IDENTIFIER().getSymbol().getCharPositionInLine(), ctx.start.getInputStream().toString());
        }
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
        if (ctx.IDENTIFIER() != null) {
            call.setSourceInfo(ctx.IDENTIFIER().getSymbol().getLine(), ctx.IDENTIFIER().getSymbol().getCharPositionInLine(), ctx.start.getInputStream().toString());
        }
        return call;
    }

    private List<Expression> parseArgumentList(SnailParser.ArgumentListContext ctx, Scope parent) throws io.github.snaill.exception.FailedCheckException {
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

    private Node parseType(SnailParser.TypeContext ctx) throws io.github.snaill.exception.FailedCheckException {
        if (ctx.primitiveType() != null) {
            return parsePrimitiveType(ctx.primitiveType());
        } else if (ctx.arrayType() != null) {
            return parseArrayType(ctx.arrayType());
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

    private Node parseArrayType(SnailParser.ArrayTypeContext ctx) throws io.github.snaill.exception.FailedCheckException {
        Type elementType = (Type) parseType(ctx.type());
        NumberLiteral size = new NumberLiteral(Long.parseLong(ctx.numberLiteral().getText()));
        return new ArrayType(elementType, size);
    }

    private Node parsePrimitiveType(SnailParser.PrimitiveTypeContext ctx) throws io.github.snaill.exception.FailedCheckException {
        return new PrimitiveType(ctx.getText());
    }
}