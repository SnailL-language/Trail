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

public class ASTReflectionBuilder implements ASTBuilder {

    @Override
    public Node build(SnailParser.ProgramContext ctx) {
        List<Statement> statements = new ArrayList<>();
        for (SnailParser.VariableDeclarationContext varDecl : ctx.variableDeclaration()) {
            statements.add((Statement) parseVariableDeclaration(varDecl));
        }
        for (SnailParser.FuncDeclarationContext funcDecl : ctx.funcDeclaration()) {
            statements.add((Statement) parseFuncDeclaration(funcDecl));
        }
        return new Scope(statements);
    }

    private Node parseStatement(SnailParser.StatementContext ctx) {
        return parseContext(ctx.getChild(0), ctx.getChild(0).getClass());
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

    private Node parseVariableDeclaration(SnailParser.VariableDeclarationContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        Type type = (Type) parseType(ctx.type());
        Expression expr = (Expression) parseExpression(ctx.expression());
        return new VariableDeclaration(name, type, expr);
    }

    private Node parseFuncDeclaration(SnailParser.FuncDeclarationContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        List<Parameter> params = ctx.paramList() != null ?
                parseParamList(ctx.paramList()) : List.of();
        Type returnType = ctx.type() != null ?
                (Type) parseType(ctx.type()) : new PrimitiveType("void");
        Scope scope = parseScope(ctx.scope());
        return new FunctionDeclaration(name, params, returnType, scope);
    }

    private List<Parameter> parseParamList(SnailParser.ParamListContext ctx) {
        return ctx.param().stream()
                .map(param -> (Parameter) parseParam(param))
                .collect(Collectors.toList());
    }

    private Node parseParam(SnailParser.ParamContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        Type type = (Type) parseType(ctx.type());
        return new Parameter(name, type);
    }

    private Scope parseScope(SnailParser.ScopeContext ctx) {
        List<Statement> statements = ctx.statement().stream()
                .map(this::parseStatement)
                .map(Statement.class::cast)
                .collect(Collectors.toList());
        return new Scope(statements);
    }

    private Node parseForLoop(SnailParser.ForLoopContext ctx) {
        VariableDeclaration declaration = (VariableDeclaration) parseVariableDeclaration(ctx.variableDeclaration());
        Expression condition = (Expression) parseExpression(ctx.expression(0));
        Expression step = (Expression) parseExpression(ctx.expression(1));
        Scope body = parseScope(ctx.scope());
        return new ForLoop(declaration, condition, step, body);
    }

    private Node parseWhileLoop(SnailParser.WhileLoopContext ctx) {
        Expression condition = (Expression) parseExpression(ctx.expression());
        Scope body = parseScope(ctx.scope());
        return new WhileLoop(condition, body);
    }

    private Node parseIfCondition(SnailParser.IfConditionContext ctx) {
        Expression condition = (Expression) parseExpression(ctx.expression());
        Scope body = parseScope(ctx.scope(0));
        Scope elseBody = ctx.scope().size() > 1 ? parseScope(ctx.scope(1)) : null;
        return new IfStatement(condition, body, elseBody);
    }

    private Node parseBreakStatement(SnailParser.BreakStatementContext ctx) {
        return new BreakStatement();
    }

    private Node parseReturnStatement(SnailParser.ReturnStatementContext ctx) {
        Expression expr = ctx.expression() != null ? (Expression) parseExpression(ctx.expression()) : null;
        return new ReturnStatement(expr);
    }

    private Node parseExpression(SnailParser.ExpressionContext ctx) {
        if (ctx.assigmentExpression() != null) {
            return parseAssigmentExpression(ctx.assigmentExpression());
        } else if (ctx.binaryExpression() != null) {
            return parseBinaryExpression(ctx.binaryExpression());
        } else if (ctx.unaryExpression() != null) {
            return parseUnaryExpression(ctx.unaryExpression());
        } else if (ctx.primaryExpression() != null) {
            return parsePrimaryExpression(ctx.primaryExpression());
        } else if (ctx.getChild(0) instanceof TerminalNode && ctx.getChild(0).getText().equals("(")) {
            return parseExpression(ctx.expression());
        }
        throw new RuntimeException("Unknown expression");
    }

    private Node parseAssigmentExpression(SnailParser.AssigmentExpressionContext ctx) {
        Expression left;
        if (ctx.identifier().variableIdentifier() != null) {
            left = (Expression) parseIdentifier(ctx.identifier());
        } else if (ctx.identifier().arrayElement() != null) {
            left = (Expression) parseArrayElement(ctx.identifier().arrayElement());
        } else {
            throw new RuntimeException("Invalid left-hand side of assignment");
        }
        String operator = ctx.assigmentOperator.getText();
        Expression right = (Expression) parseExpression(ctx.expression());
        return new AssigmentExpression(left, operator, right);
    }

    private Node parseBinaryExpression(SnailParser.BinaryExpressionContext ctx) {
        Expression left = ctx.primaryExpression() != null ?
                (Expression) parsePrimaryExpression(ctx.primaryExpression()) :
                (Expression) parseExpression(ctx.expression(0));
        String operator = ctx.binaryOperator.getText();
        Expression right = (Expression) parseExpression(ctx.expression(ctx.primaryExpression() != null ? 0 : 1));
        return new BinaryExpression(left, operator, right);
    }

    private Node parseUnaryExpression(SnailParser.UnaryExpressionContext ctx) {
        String operator = ctx.unaryOperator.getText();
        Expression argument = (Expression) parseExpression(ctx.expression());
        return new UnaryExpression(operator, argument);
    }

    private Node parsePrimaryExpression(SnailParser.PrimaryExpressionContext ctx) {
        if (ctx.literal() != null) {
            return parseLiteral(ctx.literal());
        } else if (ctx.identifier() != null) {
            return parseIdentifier(ctx.identifier());
        } else if (ctx.arrayElement() != null) {
            return parseArrayElement(ctx.arrayElement());
        } else if (ctx.functionCall() != null) {
            return parseFunctionCall(ctx.functionCall());
        } else if (ctx.arrayLiteral() != null) {
            return parseArrayLiteral(ctx.arrayLiteral());
        }
        throw new RuntimeException("Unknown primary expression");
    }

    private Node parseLiteral(SnailParser.LiteralContext ctx) {
        if (ctx.numberLiteral() != null) {
            return parseNumberLiteral(ctx.numberLiteral());
        } else if (ctx.stringLiteral() != null) {
            return parseStringLiteral(ctx.stringLiteral());
        } else if (ctx.booleanLiteral() != null) {
            return parseBooleanLiteral(ctx.booleanLiteral());
        }
        throw new RuntimeException("Unknown literal");
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

    private Node parseIdentifier(SnailParser.IdentifierContext ctx) {
        if (ctx.variableIdentifier() != null) {
            return new Identifier(ctx.variableIdentifier().IDENTIFIER().getText());
        } else if (ctx.arrayElement() != null) {
            return parseArrayElement(ctx.arrayElement());
        }
        throw new RuntimeException("Invalid identifier");
    }

    private Node parseArrayElement(SnailParser.ArrayElementContext ctx) {
        Identifier identifier = new Identifier(ctx.IDENTIFIER().getText());
        List<Expression> indices = new ArrayList<>();
        if (ctx.expression() != null) {
            indices.add((Expression) parseExpression(ctx.expression()));
        } else if (ctx.numberLiteral() != null) {
            indices.add((Expression) parseNumberLiteral(ctx.numberLiteral()));
        }
        if (ctx.arrayElement() != null) {
            ArrayElement inner = (ArrayElement) parseArrayElement(ctx.arrayElement());
            identifier = (Identifier) inner.getIdentifier();
            indices.addAll(inner.getDims());
        }
        return new ArrayElement(identifier, indices);
    }

    private Node parseFunctionCall(SnailParser.FunctionCallContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        List<Expression> args = ctx.argumentList() != null ?
                parseArgumentList(ctx.argumentList()) : List.of();
        return new FunctionCall(name, args);
    }

    private List<Expression> parseArgumentList(SnailParser.ArgumentListContext ctx) {
        return ctx.expression().stream()
                .map(expr -> (Expression) parseExpression(expr))
                .collect(Collectors.toList());
    }

    private Node parseArrayLiteral(SnailParser.ArrayLiteralContext ctx) {
        List<Expression> elements = ctx.expression().stream()
                .map(expr -> (Expression) parseExpression(expr))
                .collect(Collectors.toList());
        return new ArrayLiteral(elements);
    }

    private Node parseType(SnailParser.TypeContext ctx) {
        if (ctx.primitiveType() != null) {
            return parsePrimitiveType(ctx.primitiveType());
        } else if (ctx.arrayType() != null) {
            return parseArrayType(ctx.arrayType());
        }
        throw new RuntimeException("Unknown type");
    }

    private Node parseArrayType(SnailParser.ArrayTypeContext ctx) {
        Type elementType = (Type) parseType(ctx.type());
        NumberLiteral size = new NumberLiteral(Long.parseLong(ctx.numberLiteral().getText()));
        return new ArrayType(elementType, size);
    }

    private Node parsePrimitiveType(SnailParser.PrimitiveTypeContext ctx) {
        return new PrimitiveType(ctx.getText());
    }
}