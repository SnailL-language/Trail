package io.github.snaill.ast;

import io.github.snaill.parser.Separator;
import io.github.snaill.parser.SnailParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ASTBuilder {
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
                StringBuilder Sb = new StringBuilder();
                for (Statement stmt : scope.getStatements()) {
                    Sb.append(toSourceCode(stmt, false));
                    if (stmt instanceof Expression) {
                        Sb.append(';');
                    }
                }
                if (!isRoot) {
                    Sb.insert(0, "{").append("}");
                }
                yield Sb.toString();
            }
            case FunctionDeclaration func -> {
                StringBuilder Sb = new StringBuilder();
                Sb.append("fn ").append(func.getName()).append("(");
                String params = func.getParameterList().stream()
                        .map(param -> toSourceCode(param, false))
                        .collect(Collectors.joining(", "));
                Sb.append(params).append(")");
                String returnType = toSourceCode(func.getReturnType(), false);
                if (!returnType.equals("void")) {
                    Sb.append(" -> ").append(returnType);
                }
                Sb.append(toSourceCode(func.getScope(), false));
                yield Sb.toString();
            }
            case Parameter param -> param.getName() + ": " + toSourceCode(param.getType(), false);
            case VariableDeclaration var -> "let " + var.getName() +
                    ": " + toSourceCode(var.getType(), false) +
                    " = " + toSourceCode(var.getValue(), false) +
                    ";";
            case StringLiteral stringLiteral -> stringLiteral.getValue();
            case NumberLiteral numberLiteral -> String.valueOf(numberLiteral.getValue());
            case Identifier identifier -> identifier.getName();
            case PrimitiveType primitiveType -> primitiveType.getName();
            case ArrayType arrayType -> "[" + toSourceCode(arrayType.getElementType(), false) +
                    "; " + toSourceCode(arrayType.getSize(), false) + "]";
            case BinaryExpression binExpr -> toSourceCode(binExpr.getLeft(), false) +
                    " " + binExpr.getOperator() + " " +
                    toSourceCode(binExpr.getRight(), false);
            case UnaryExpression unExpr -> unExpr.getOperator() +
                    toSourceCode(unExpr.getArgument(), false);
            case ReturnStatement ret -> {
                String returnable = toSourceCode(ret.getReturnable(), false);
                yield "return" + (returnable.isEmpty() ? "" : " " + returnable) + ";";
            }
            case FunctionCall funcCall -> {
                StringBuilder Sb = new StringBuilder();
                Sb.append(funcCall.getName()).append("(");
                String args = funcCall.getArguments().stream()
                        .map(arg -> toSourceCode(arg, false))
                        .collect(Collectors.joining(", "));
                Sb.append(args).append(")");
                yield Sb.toString();
            }
            case ArrayLiteral arrayLiteral -> {
                StringBuilder Sb = new StringBuilder();
                Sb.append("[");
                String elements = arrayLiteral.getElements().stream()
                        .map(elem -> toSourceCode(elem, false))
                        .collect(Collectors.joining(", "));
                Sb.append(elements).append("]");
                yield Sb.toString();
            }
            case WhileLoop whileLoop -> "while (" + toSourceCode(whileLoop.getCondition(), false) + ") " +
                    toSourceCode(whileLoop.getBody(), false);
            case ForLoop forLoop -> "for (" + toSourceCode(forLoop.getDeclaration(), false) +
                    toSourceCode(forLoop.getCondition(), false) + "; " + toSourceCode(forLoop.getStep(), false) +
                    ")" +
                    toSourceCode(forLoop.getBody(), false);
            case IfStatement ifStmt -> "if (" + toSourceCode(ifStmt.getCondition(), false) + ") " +
                    toSourceCode(ifStmt.getBody(), false);
            case BreakStatement _ -> "break;";
            case AssigmentExpression assignExpr -> assignExpr.getVariableName() +
                    " " + assignExpr.getOperator() + " " +
                    toSourceCode(assignExpr.getExpression(), false);
            default -> "// Неизвестный узел: " + node.getClass().getSimpleName();
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