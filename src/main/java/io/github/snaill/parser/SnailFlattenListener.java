package io.github.snaill.parser;

import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayDeque;
import java.util.Queue;

public class SnailFlattenListener extends SnailBaseListener {
    Queue<ParserRuleContext> nodes = new ArrayDeque<>();

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
        nodes.add(ctx);
        super.enterEveryRule(ctx);
    }

    public Queue<ParserRuleContext> getNodes() {
        return nodes;
    }
}