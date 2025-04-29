package io.github.snaill;

import io.github.snaill.ast.*;
import io.github.snaill.parser.SnailFlattenListener;
import io.github.snaill.parser.SnailLexer;
import io.github.snaill.parser.SnailParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;

public class Trail {

    private static final String USAGE = "trail [options] builderType(flatten, reflection) <file_to_compile>";

    public static void main(String[] args) {
        if (args.length <= 1) {
            System.err.println(USAGE);
            return;
        }
        final CharStream stream;
        try {
            stream = CharStreams.fromFileName(args[args.length - 1]);
        } catch (IOException e) {
            System.err.println("Cannot read file");
            return;
        }
        SnailParser parser = new SnailParser(
                new CommonTokenStream(
                        new SnailLexer(stream)
                )
        );
        SnailParser.ProgramContext tree = parser.program();
        ASTBuilder builder = null;
        if (args[args.length - 2].equals("flatten")) {
            builder = new ASTFlattenBuilder();
        } else if (args[args.length - 2].equals("reflection")) {
            builder = new ASTReflectionBuilder();
        } else {
            System.err.println(USAGE);
            return;
        }
        Node root = builder.build(tree);
        System.out.println(SourceBuilder.toSourceCode(root));
    }
}
