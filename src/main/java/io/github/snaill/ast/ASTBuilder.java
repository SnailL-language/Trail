package io.github.snaill.ast;

import io.github.snaill.parser.SnailParser;

public interface ASTBuilder {
    public Node build(SnailParser.ProgramContext tree);
}
