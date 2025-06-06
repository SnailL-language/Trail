package io.github.snaill.ast;

import io.github.snaill.parser.SnailParser;

public interface ASTBuilder {
    Node build(SnailParser.ProgramContext tree) throws io.github.snaill.exception.FailedCheckException;
}
