package io.github.snaill.parser;

import io.github.snaill.ast.Parameter;
import org.antlr.v4.runtime.ParserRuleContext;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Set;

public class SnailFlattenListener extends SnailBaseListener {
    Queue<ParserRuleContext> nodes = new ArrayDeque<>();
    Set<Class<? extends  ParserRuleContext>> excluded = Set.of(SnailParser.StatementContext.class,
            SnailParser.PrimaryExpressionContext.class, SnailParser.ExpressionContext.class);

    public Queue<ParserRuleContext> getNodes() {
        return nodes;
    }


    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
        if (!excluded.contains(ctx.getClass())) {
            nodes.add(ctx);
        }
        super.enterEveryRule(ctx);
    }
}