package io.github.snaill.ast;

import io.github.snaill.parser.SnailParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class ASTBuilder {
    private static final List<Class<? extends ParserRuleContext>> globalStatements =
            List.of(SnailParser.VariableDeclarationContext.class,
                    SnailParser.FuncDeclarationContext.class);
    private static final List<Class<? extends ParserRuleContext>> statements =
            List.of(SnailParser.VariableDeclarationContext.class,
                    SnailParser.ForLoopContext.class,
                    SnailParser.WhileLoopContext.class, SnailParser.IfConditionContext.class,
                    SnailParser.BreakStatementContext.class, SnailParser.ReturnStatementContext.class,
                    SnailParser.ExpressionContext.class);
    private final Queue<ParserRuleContext> nodes;
    private Node root;

    public ASTBuilder(Queue<ParserRuleContext> nodes) {
        this.nodes = nodes;
    }

    public static String toSourceCode(Node node, boolean isRoot) {
        if (node == null) {
            return "";
        }

        return switch (node) {
            case Scope scope -> {
                StringBuilder sb = new StringBuilder();
                for (Statement stmt : scope.getStatements()) {
                    sb.append(toSourceCode(stmt, false));
                }
                if (!isRoot) {
                    sb.insert(0, "{").append("}");
                }
                yield sb.toString();
            }
            case FunctionDeclaration func -> {
                StringBuilder sb = new StringBuilder();
                sb.append("fn").append(func.getName()).append("(");
                sb.append(toSourceCode(func.getParameterList(), false));
                sb.append(")");
                Type returnType = func.getReturnType();
                if (!returnType.getTypeName().equals("void")) {
                    sb.append("->").append(returnType.getTypeName());
                }
                sb.append(toSourceCode(func.getScope(), false));
                yield sb.toString();
            }
            case ParameterList params -> {
                StringBuilder sb = new StringBuilder();
                List<Parameter> paramList = params.getParameters();
                for (int i = 0; i < paramList.size(); i++) {
                    sb.append(toSourceCode(paramList.get(i), false));
                    if (i < paramList.size() - 1) {
                        sb.append(",");
                    }
                }
                yield sb.toString();
            }
            case Parameter param -> "param:" + param.getType().getTypeName();
            case VariableDeclaration var -> "let" + var.getName() +
                    ":" + var.getType().getTypeName() +
                    "=" + toSourceCode(var.getValue(), false) +
                    ";";
            case Literal literal -> literal.getValue().toString();
            case Identifier identifier -> identifier.getName();
            case Type type -> type.getTypeName();
            case BinaryExpression binExpr -> toSourceCode(binExpr.getLeft(), false) +
                    binExpr.getOperator() +
                    toSourceCode(binExpr.getRight(), false);
            case UnaryExpression unExpr -> unExpr.getOperator() +
                    toSourceCode(unExpr.getArgument(), false);
            case ReturnStatement ret -> "return" + toSourceCode(ret.getReturnable(), false) + ";";
            default -> "//Неизвестный узел:" + node.getClass().getSimpleName();
        };
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
                ParameterList params = (ParameterList) buildNode(nextContext());
                Type type;
                if (ctx.getChild(SnailParser.TypeContext.class, 0) != null) {
                    type = (Type) buildNode(nextContext());
                } else {
                    type = new Type("void");
                }
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
                Expression argument = (Expression) buildNode(nextContext());
                yield new UnaryExpression(operator, argument);
            }

            case SnailParser.ReturnStatementContext _ -> {
                Expression returnable = (Expression) buildNode(nextContext());
                yield new ReturnStatement(returnable);
            }

            case SnailParser.ArgumentListContext _,
                 SnailParser.ForLoopContext _,
                 SnailParser.WhileLoopContext _,
                 SnailParser.IfConditionContext _,
                 SnailParser.BreakStatementContext _,
                 SnailParser.AssignmentOperatorContext _,
                 SnailParser.FunctionCallContext _,
                 SnailParser.ArrayLiteralContext _ ->
                    throw new UnsupportedOperationException("Not implemented: " + ctx.getClass().getSimpleName());
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
}