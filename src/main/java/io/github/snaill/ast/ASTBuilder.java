package io.github.snaill.ast;

import io.github.snaill.parser.SnailParser;

import java.text.ParseException;

public interface ASTBuilder {
    public Node build(SnailParser.ProgramContext tree);
}
