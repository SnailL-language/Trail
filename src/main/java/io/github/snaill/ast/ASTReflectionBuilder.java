package io.github.snaill.ast;

import io.github.snaill.parser.SnailParser;
import org.antlr.v4.runtime.ParserRuleContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class ASTReflectionBuilder implements ASTBuilder {

    public ASTReflectionBuilder() {
    }

    @Override
    public Node build(SnailParser.ProgramContext ctx) {
        Node root = parseProgram(ctx);
        return root;
    }

    private Node parseProgram(SnailParser.ProgramContext ctx) {
        List<Statement> children = Stream.concat(
                ctx.variableDeclaration().stream().map(this::parseVariableDeclaration),
                ctx.funcDeclaration().stream().map(this::parseFuncDeclaration)
        ).map(Statement.class::cast).collect(Collectors.toList());
        return new Scope(children);
    }

    private Node parseStatement(SnailParser.StatementContext ctx) {
        return parseContext(ctx, SnailParser.StatementContext.class);
    }

    private Node parseExpression(SnailParser.ExpressionContext ctx) {
        return parseContext(ctx, SnailParser.ExpressionContext.class);
    }

    private Node parseType(SnailParser.TypeContext ctx) {
        return parseContext(ctx, SnailParser.TypeContext.class);
    }

    private Node parseContext(ParserRuleContext ctx, Class<? extends ParserRuleContext> contextClass) {
        List<Method> methods = Arrays.stream(contextClass.getDeclaredMethods())
                .filter(method -> ParserRuleContext.class.isAssignableFrom(method.getReturnType()))
                .toList();
        for (Method method : methods) {
            try {
                Object result = method.invoke(ctx);
                if (result != null) {
                    String parseMethodName = "parse" + method.getName().substring(0, 1).toUpperCase() + method.getName().substring(1);
                    Method parseMethod = getClass().getDeclaredMethod(parseMethodName, method.getReturnType());
                    return (Node) parseMethod.invoke(this, result);
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException("Failed to parse context: " + ctx.getClass().getSimpleName(), e);
            }
        }
        throw new IllegalStateException("No matching context found in: " + ctx.getClass().getSimpleName());
    }

    private Node parseVariableDeclaration(SnailParser.VariableDeclarationContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        Type type = (Type) parseType(ctx.type());
        Expression expr = (Expression) parseExpression(ctx.expression());
        return new VariableDeclaration(name, type, expr);
    }

    private Node parseFuncDeclaration(SnailParser.FuncDeclarationContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        List<Parameter> params = ctx.paramList().param().stream()
                .map(this::parseParam).map(Parameter.class::cast)
                .toList();
        Type type = ctx.type() != null ? (Type) parseType(ctx.type()) : new PrimitiveType("void");
        Scope scope = parseScope(ctx.scope());
        return new FunctionDeclaration(name, params, type, scope);
    }

    private Node parseParam(SnailParser.ParamContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        Type type = (Type) parseType(ctx.type());
        return new Parameter(name, type);
    }

    private Node parseStringLiteral(SnailParser.StringLiteralContext ctx) {
        String value = ctx.STRING().getText();
        return new StringLiteral(value);
    }

    private Node parseNumberLiteral(SnailParser.NumberLiteralContext ctx) {
        long value = Long.parseLong(ctx.NUMBER().getText());
        return new NumberLiteral(value);
    }

    private Node parseIdentifier(SnailParser.IdentifierContext ctx) {
        String value = ctx.IDENTIFIER().getText();
        return new Identifier(value);
    }

    private Node parsePrimitiveType(SnailParser.PrimitiveTypeContext ctx) {
        return new PrimitiveType(ctx.getText());
    }

    private Node parseArrayType(SnailParser.ArrayTypeContext ctx) {
        Type type = (Type) parseType(ctx.type());
        NumberLiteral size = (NumberLiteral) parseNumberLiteral(ctx.numberLiteral());
        return new ArrayType(type, size);
    }

    private Node parseBinaryExpression(SnailParser.BinaryExpressionContext ctx) {
        String operator = ctx.binaryOperator.getText();
        Expression left;
        if (ctx.primaryExpression() != null) {
            left = (Expression) parsePrimaryExpression(ctx.primaryExpression());
        } else {
            left = (Expression) parseExpression(ctx.expression(0));
        }
        Expression right = (Expression) parseExpression(ctx.expression(ctx.primaryExpression() != null ? 0 : 1));
        return new BinaryExpression(left, operator, right);
    }

    private Node parseUnaryExpression(SnailParser.UnaryExpressionContext ctx) {
        String operator = ctx.unaryOperator.getText();
        Expression argument = (Expression) parseExpression(ctx.expression());
        return new UnaryExpression(operator, argument);
    }

    private Node parseReturnStatement(SnailParser.ReturnStatementContext ctx) {
        Expression returnable = ctx.expression() != null ? (Expression) parseExpression(ctx.expression()) : null;
        return new ReturnStatement(returnable);
    }

    private Node parseFunctionCall(SnailParser.FunctionCallContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        List<Expression> arguments = ctx.argumentList().expression().stream()
                .map(this::parseExpression)
                .map(Expression.class::cast)
                .collect(Collectors.toList());
        return new FunctionCall(name, arguments);
    }

    private Node parseArrayLiteral(SnailParser.ArrayLiteralContext ctx) {
        List<Expression> elements = ctx.expression().stream()
                .map(this::parseExpression)
                .map(Expression.class::cast)
                .collect(Collectors.toList());
        return new ArrayLiteral(elements);
    }

    private Node parseWhileLoop(SnailParser.WhileLoopContext ctx) {
        Expression condition = (Expression) parseExpression(ctx.expression());
        Scope body = parseScope(ctx.scope());
        return new WhileLoop(condition, body);
    }

    private Node parseForLoop(SnailParser.ForLoopContext ctx) {
        VariableDeclaration declaration = (VariableDeclaration) parseVariableDeclaration(ctx.variableDeclaration());
        Expression condition = (Expression) parseExpression(ctx.expression(0));
        Expression step = (Expression) parseExpression(ctx.expression(1));
        Scope body = parseScope(ctx.scope());
        return new ForLoop(declaration, condition, step, body);
    }

    private Node parseIfCondition(SnailParser.IfConditionContext ctx) {
        Expression condition = (Expression) parseExpression(ctx.expression());
        Scope body = parseScope(ctx.scope());
        return new IfStatement(condition, body);
    }

    private Node parseBreakStatement(SnailParser.BreakStatementContext ctx) {
        return new BreakStatement();
    }

    private Node parseAssigmentExpression(SnailParser.AssigmentExpressionContext ctx) {
        String variableName = ctx.IDENTIFIER().getText();
        String operator = ctx.assigmentOperator.getText();
        Expression expression = (Expression) parseExpression(ctx.expression());
        return new AssigmentExpression(variableName, operator, expression);
    }

    private Node parsePrimaryExpression(SnailParser.PrimaryExpressionContext ctx) {
        return parseContext(ctx, SnailParser.PrimaryExpressionContext.class);
    }

    private Node parseLiteral(SnailParser.LiteralContext ctx) {
        return parseContext(ctx, SnailParser.LiteralContext.class);
    }

    private Scope parseScope(SnailParser.ScopeContext ctx) {
        List<Statement> children = ctx.statement().stream()
                .map(this::parseStatement)
                .map(Statement.class::cast)
                .collect(Collectors.toList());
        return new Scope(children);
    }
}