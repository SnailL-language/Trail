package io.github.snaill.ast;

import io.github.snaill.parser.Separator;
import io.github.snaill.parser.SnailFlattenListener;
import io.github.snaill.parser.SnailParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ASTFlattenBuilder implements ASTBuilder {
    private static final List<Class<? extends ParserRuleContext>> globalStatements =
            List.of(SnailParser.VariableDeclarationContext.class,
                    SnailParser.FuncDeclarationContext.class);
    private static final List<Class<? extends ParserRuleContext>> expressions =
            List.of(SnailParser.BinaryExpressionContext.class,
                    SnailParser.UnaryExpressionContext.class,
                    SnailParser.NumberLiteralContext.class, SnailParser.StringLiteralContext.class,
                    SnailParser.IdentifierContext.class, SnailParser.AssigmentExpressionContext.class);
    private static final List<Class<? extends ParserRuleContext>> statements =
            Stream.concat(
                    Stream.of(
                            SnailParser.VariableDeclarationContext.class,
                            SnailParser.ForLoopContext.class,
                            SnailParser.WhileLoopContext.class,
                            SnailParser.IfConditionContext.class,
                            SnailParser.BreakStatementContext.class,
                            SnailParser.ReturnStatementContext.class
                    ),
                    expressions.stream()
            ).collect(Collectors.toList());

    private Queue<ParserRuleContext> nodes = null;
    private Node root;

    public ASTFlattenBuilder() {
    }

    public Node build(SnailParser.ProgramContext tree) {
        SnailFlattenListener listener = new SnailFlattenListener();
        new ParseTreeWalker().walk(listener, tree);
        nodes = listener.getNodes();
        if (!nodes.isEmpty()) {
            root = buildNode(nodes.poll());
        }
        return root;
    }

    private Node buildNode(ParserRuleContext ctx) {
        if (ctx == null) {
            throw new IllegalStateException("Context cannot be null");
        }

        return switch (ctx) {
            case SnailParser.ProgramContext _ -> {
                List<Statement> children = collectChildren(globalStatements, Statement.class);
                yield new Scope(children);
            }

            case SnailParser.ScopeContext _ -> {
                List<Statement> children = collectChildren(statements, Statement.class);
                yield new Scope(children);
            }
            case SnailParser.FuncDeclarationContext _ -> {
                String name = ((SnailParser.FuncDeclarationContext) ctx).IDENTIFIER().getText();
                List<Parameter> params = collectChildren(SnailParser.ParamContext.class, Parameter.class);
                Type type;
                if (ctx.getChild(SnailParser.TypeContext.class, 0) != null) {
                    type = (Type) buildNode(nextContext());
                } else {
                    type = new PrimitiveType("void");
                }
                Scope scope = (Scope) buildNode(nextContext());
                yield new FunctionDeclaration(name, params, type, scope);
            }
            case SnailParser.ParamContext _ -> {
                String name = ((SnailParser.ParamContext) ctx).IDENTIFIER().getText();
                Type type = (Type) buildNode(nextContext());
                yield new Parameter(name, type);
            }

            case SnailParser.VariableDeclarationContext _ -> {
                String name = ((SnailParser.VariableDeclarationContext) ctx).IDENTIFIER().getText();
                Type type = (Type) buildNode(nextContext());
                Expression expr = (Expression) buildNode(nextContext());
                yield new VariableDeclaration(name, type, expr);
            }
            case SnailParser.StringLiteralContext _ -> {
                String value = ((SnailParser.StringLiteralContext) ctx).STRING().getText();
                yield new StringLiteral(value);
            }

            case SnailParser.NumberLiteralContext _ -> {
                long value = Long.parseLong(((SnailParser.NumberLiteralContext) ctx).NUMBER().getText());
                yield new NumberLiteral(value);
            }

            case SnailParser.IdentifierContext _ -> {
                String value = ctx.getChild(TerminalNode.class, 0).getText();
                yield new Identifier(value);
            }

            case SnailParser.PrimitiveTypeContext _ -> new PrimitiveType(ctx.getText());

            case SnailParser.ArrayTypeContext _ -> {
                Type type = (Type) buildNode(nextContext());
                NumberLiteral value = (NumberLiteral) buildNode(nextContext());
                yield new ArrayType(type, value);
            }

            case SnailParser.BinaryExpressionContext _ -> {
                String operator = ((SnailParser.BinaryExpressionContext) ctx).binaryOperator.getText();
                Expression left = (Expression) buildNode(nextContext());
                Expression right = (Expression) buildNode(nextContext());
                yield new BinaryExpression(left, operator, right);
            }

            case SnailParser.UnaryExpressionContext _ -> {
                String operator = ((SnailParser.UnaryExpressionContext) ctx).unaryOperator.getText();
                Expression argument = (Expression) buildNode(nextContext());
                yield new UnaryExpression(operator, argument);
            }

            case SnailParser.ReturnStatementContext _ -> {
                Expression returnable = (Expression) buildNode(nextContext());
                yield new ReturnStatement(returnable);
            }

            case SnailParser.FunctionCallContext _ -> {
                String name = ((SnailParser.FunctionCallContext) ctx).IDENTIFIER().getText();
                List<Expression> arguments = collectChildren(expressions, Expression.class);
                yield new FunctionCall(name, arguments);
            }

            case SnailParser.ArrayLiteralContext _ -> {
                List<Expression> elements = collectChildren(expressions, Expression.class);
                yield new ArrayLiteral(elements);
            }

            case SnailParser.WhileLoopContext _ -> {
                Expression condition = (Expression) buildNode(nextContext());
                Scope body = (Scope) buildNode(nextContext());
                yield new WhileLoop(condition, body);
            }

            case SnailParser.ForLoopContext _ -> {
                VariableDeclaration declaration = (VariableDeclaration) buildNode(nextContext());
                Expression condition = (Expression) buildNode(nextContext());
                Expression step = (Expression) buildNode(nextContext());
                Scope body = (Scope) buildNode(nextContext());
                yield new ForLoop(declaration, condition, step, body);
            }

            case SnailParser.IfConditionContext _ -> {
                Expression condition = (Expression) buildNode(nextContext());
                Scope body = (Scope) buildNode(nextContext());
                yield new IfStatement(condition, body);
            }

            case SnailParser.BreakStatementContext _ -> new BreakStatement();

            case SnailParser.AssigmentExpressionContext _ -> {
                String variableName = ((SnailParser.AssigmentExpressionContext) ctx).IDENTIFIER().getText();
                String operator = ((SnailParser.AssigmentExpressionContext) ctx).assigmentOperator.getText();
                Expression expression = (Expression) buildNode(nextContext());
                yield new AssigmentExpression(variableName, operator, expression);
            }

            default -> throw new IllegalStateException("Unknown context: " + ctx.getClass().getSimpleName());
        };
    }

    private <R extends Node> List<R> collectChildren(List<Class<? extends ParserRuleContext>> contextTypes,
                                                     Class<R> nodeType) {
        List<R> children = new ArrayList<>();
        while (nodes.peek() != null && contextTypes.stream().anyMatch(t -> t.isInstance(nodes.peek()))) {
            R child = nodeType.cast(buildNode(nodes.poll()));
            children.add(child);
        }
        if (nodes.peek() != null && nodes.peek() instanceof Separator) {
            nodes.poll();
        }
        return children;
    }

    private <T extends ParserRuleContext, R extends Node> List<R> collectChildren(Class<T> contextType,
                                                                                  Class<R> nodeType) {
        return collectChildren(List.of(contextType), nodeType);
    }

    private ParserRuleContext nextContext() {
        ParserRuleContext ctx = nodes.poll();
        if (ctx == null) {
            throw new IllegalStateException("Expected a context, but queue is empty");
        }
        return ctx;
    }
}