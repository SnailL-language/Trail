package io.github.snaill.ast;

import io.github.snaill.parser.SnailParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class ASTBuilder {
    private final Queue<ParserRuleContext> nodes;
    private Node root;
    private static final List<Class<? extends ParserRuleContext>> statements =
            List.of(SnailParser.VariableDeclarationContext.class,
            SnailParser.ForLoopContext.class, SnailParser.FuncDeclarationContext.class,
            SnailParser.WhileLoopContext.class, SnailParser.IfConditionContext.class,
            SnailParser.BreakStatementContext.class, SnailParser.ReturnStatementContext.class,
                    SnailParser.ExpressionContext.class);

    public ASTBuilder(Queue<ParserRuleContext> nodes) {
        this.nodes = nodes;
    }

    public Node build() {
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
            case SnailParser.ProgramContext _, SnailParser.ScopeContext _ -> {
                List<Statement> children = collectChildren(statements, Statement.class);
                yield new Scope(children);
            }
            case SnailParser.FuncDeclarationContext _ -> {
                String name = ((SnailParser.FuncDeclarationContext) ctx).IDENTIFIER().getText();
                ParameterList params = (ParameterList) buildNode(nextContext());
                Type type = (Type) buildNode(nextContext());
                Scope scope = (Scope) buildNode(nextContext());
                yield new FunctionDeclaration(name, params, type, scope);
            }
            case SnailParser.ParamListContext _ -> {
                List<Parameter> children = collectChildren(SnailParser.ParamContext.class, Parameter.class);
                yield new ParameterList(children);
            }
            case SnailParser.ParamContext _ -> {
                Type type = (Type) buildNode(nextContext());
                yield new Parameter(type);
            }

            case SnailParser.VariableDeclarationContext _ -> {
                String name = ((SnailParser.VariableDeclarationContext) ctx).IDENTIFIER().getText();
                Type type = (Type) buildNode(nextContext());
                Expression expr = (Expression) buildNode(nextContext());
                yield new VariableDeclaration(name, type, expr);
            }
            case SnailParser.LiteralContext _ -> {
                String value = ctx.getChild(TerminalNode.class, 0).getText();
                yield new Literal(value);
            }

            case SnailParser.IdentifierContext _ -> {
                String value = ctx.getChild(TerminalNode.class, 0).getText();
                yield new Identifier(value);
            }

            case SnailParser.TypeContext _ -> new Type(ctx.getChild(TerminalNode.class, 0).getText());

            case SnailParser.BinaryExpressionContext _ -> {
                String operator = ((SnailParser.BinaryExpressionContext) ctx).binaryOperator.getText();
                Expression left = (Expression) buildNode(nextContext());
                Expression right = (Expression) buildNode(nextContext());
                yield new BinaryExpression(left, operator, right);
            }

            case SnailParser.UnaryExpressionContext _ -> {
                String operator = ((SnailParser.UnaryExpressionContext) ctx).unaryOperator.getText();
                Expression arg = (Expression) buildNode(nextContext());
                yield new UnaryExpression(operator, arg);
            }

            case SnailParser.ArgumentListContext _,
                 SnailParser.ForLoopContext _,
                 SnailParser.WhileLoopContext _,
                 SnailParser.IfConditionContext _,
                 SnailParser.BreakStatementContext _,
                 SnailParser.ReturnStatementContext _,
                 SnailParser.AssignmentOperatorContext _,
                 SnailParser.FunctionCallContext _,
                 SnailParser.ArrayLiteralContext _ -> throw new UnsupportedOperationException("Not implemented: " + ctx.getClass().getSimpleName());
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

    public static void printAST(Node node, String indent) {
        if (node == null) {
            System.out.println(indent + "└── [Пустой узел]");
            return;
        }

        switch (node) {
            case Scope scope -> {
                System.out.println(indent + "Область видимости");
                for (Statement stmt : scope.getStatements()) {
                    printAST(stmt, indent + "    ");
                }
            }
            case FunctionDeclaration func -> {
                System.out.printf("%sФункция: %s (тип: %s)%n", indent, func.getName(), func.getReturnType().getTypeName());
                printAST(func.getParameterList(), indent + "    ");
                printAST(func.getScope(), indent + "    ");
            }
            case ParameterList params -> {
                System.out.println(indent + "Параметры:");
                for (Parameter param : params.getParameters()) {
                    printAST(param, indent + "    ");
                }
            }
            case Parameter param -> {
                System.out.printf("%sПараметр (тип: %s)%n", indent, param.getType().getTypeName());
            }
            case VariableDeclaration var -> {
                System.out.printf("%sПеременная: %s (тип: %s)%n", indent, var.getName(), var.getType().getTypeName());
                if (var.getValue() != null) {
                    printAST(var.getValue(), indent + "    ");
                }
            }
            case Literal literal -> {
                System.out.printf("%sЛитерал: %s%n", indent, literal.getValue());
            }
            case Identifier identifier -> {
                System.out.printf("%sИдентификатор: %s%n", indent, identifier.getName());
            }
            case Type type -> {
                System.out.printf("%sТип: %s%n", indent, type.getTypeName());
            }
            case BinaryExpression binExpr -> {
                System.out.printf("%sБинарное выражение: %s%n", indent, binExpr.getOperator());
                printAST(binExpr.getLeft(), indent + "    ");
                printAST(binExpr.getRight(), indent + "    ");
            }
            case UnaryExpression unExpr -> {
                System.out.printf("%sУнарное выражение: %s%n", indent, unExpr.getOperator());
                printAST(unExpr.getArgument(), indent + "    ");
            }
            default -> System.out.println(indent + "Неизвестный узел: " + node.getClass().getSimpleName());
        }
    }
}