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

        Node current = switch (ctx) {
            case SnailParser.ProgramContext _ -> {
                List<GlobalDeclaration> children = collectChildren(SnailParser.FuncDeclarationContext.class, GlobalDeclaration.class);
                yield new Program(children);
            }
            case SnailParser.VarDeclStatementContext _ -> {
                VariableDeclaration varDecl = (VariableDeclaration) buildNode(nextContext());
                yield new VariableDeclarationStatement(varDecl, null);
            }
            case SnailParser.FuncDeclarationContext _ -> {
                String name = ((SnailParser.FuncDeclarationContext) ctx).IDENTIFIER().getText();
                ParameterList params = (ParameterList) buildNode(nextContext());
                Type type = (Type) buildNode(nextContext());
                Scope scope = (Scope) buildNode(nextContext());
                yield new FunctionDeclaration(name, params, type, scope, null);
            }
            case SnailParser.ParamListContext _ -> {
                List<Parameter> children = collectChildren(SnailParser.ParamContext.class, Parameter.class);
                yield new ParameterList(children, null);
            }
            case SnailParser.ParamContext _ -> {
                Type type = (Type) buildNode(nextContext());
                yield new Parameter(type, null);
            }
            case SnailParser.ScopeContext _ -> {
                List<Statement> children = collectChildren(SnailParser.StatementContext.class, Statement.class);
                yield new Scope(children, null);
            }
            case SnailParser.VariableDeclarationContext _ -> {
                String name = ((SnailParser.VariableDeclarationContext) ctx).IDENTIFIER().getText();
                Type type = (Type) buildNode(nextContext());
                Expression expr = (Expression) buildNode(nextContext());
                yield new VariableDeclaration(name, type, expr, null);
            }
            case SnailParser.PrimaryExprContext _ -> {
                List<PrimaryExpression> children = collectChildren(SnailParser.PrimaryExprContext.class, PrimaryExpression.class);
                yield new PrimaryExpression(children, null);
            }
            case SnailParser.LiteralPrimaryExprContext _ -> {
                Literal literal = (Literal) buildNode(nextContext());
                yield new LiteralPrimaryExpression(literal, null);
            }
            case SnailParser.LiteralContext _ -> {
                TerminalNode number = ((SnailParser.LiteralContext) ctx).NUMBER();
                yield new Literal(number, null);
            }
            case SnailParser.TypeContext _ -> {
                String typeText = ctx.getText();
                yield new Type(typeText, null);
            }
            case SnailParser.ExprStmtContext _,
                 SnailParser.ForLoopStmtContext _,
                 SnailParser.WhileLoopStmtContext _,
                 SnailParser.IfConditionStmtContext _,
                 SnailParser.BreakStmtContext _,
                 SnailParser.ReturnStmtContext _,
                 SnailParser.ExpressionStatementContext _,
                 SnailParser.ArgumentListContext _,
                 SnailParser.ForLoopContext _,
                 SnailParser.WhileLoopContext _,
                 SnailParser.IfConditionContext _,
                 SnailParser.BreakStatementContext _,
                 SnailParser.ReturnStatementContext _,
                 SnailParser.LogicalOrExprContext _,
                 SnailParser.MultiplicativeExprContext _,
                 SnailParser.EqualityExprContext _,
                 SnailParser.AdditiveExprContext _,
                 SnailParser.AssignmentExprContext _,
                 SnailParser.NotExprContext _,
                 SnailParser.RelationalExprContext _,
                 SnailParser.LogicalAndExprContext _,
                 SnailParser.NegateExprContext _,
                 SnailParser.AssignmentOperatorContext _,
                 SnailParser.IdentifierPrimaryExprContext _,
                 SnailParser.FunctionCallPrimaryExprContext _,
                 SnailParser.ArrayLiteralPrimaryExprContext _,
                 SnailParser.ParenthesizedPrimaryExprContext _,
                 SnailParser.FunctionCallContext _,
                 SnailParser.ArrayLiteralContext _ -> throw new UnsupportedOperationException("Not implemented: " + ctx.getClass().getSimpleName());
            default -> throw new IllegalStateException("Unknown context: " + ctx.getClass().getSimpleName());
        };
        for (Node child : current.getChildren()) {
            child.setParent(current);
        }
        return current;
    }

    private <T extends ParserRuleContext, R extends Node> List<R> collectChildren(Class<T> contextType, Class<R> nodeType) {
        List<R> children = new ArrayList<>();
        while (nodes.peek() != null && contextType.isInstance(nodes.peek())) {
            R child = nodeType.cast(buildNode(nodes.poll()));
            children.add(child);
        }
        return children;
    }

    private ParserRuleContext nextContext() {
        ParserRuleContext ctx = nodes.poll();
        if (ctx == null) {
            throw new IllegalStateException("Expected a context, but queue is empty");
        }
        return ctx;
    }
}