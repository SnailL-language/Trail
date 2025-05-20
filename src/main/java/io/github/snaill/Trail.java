package io.github.snaill;

import io.github.snaill.ast.*;
import io.github.snaill.bytecode.BytecodeEmitter;
import io.github.snaill.exception.FailedCheckException;
import io.github.snaill.parser.SnailLexer;
import io.github.snaill.parser.SnailParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.Objects;

public class Trail {

    private static final String USAGE = "trail [options] <file_to_compile>";

    public static Node build(String filename) {
        Objects.requireNonNull(filename);
        final CharStream stream;
        try {
            stream = CharStreams.fromFileName(filename);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        SnailParser parser = new SnailParser(
                new CommonTokenStream(
                        new SnailLexer(stream)
                )
        );
        SnailParser.ProgramContext tree = parser.program();
        final ASTBuilder builder = new ASTReflectionBuilder();
        return builder.build(tree);
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println(USAGE);
            return;
        }
        final Node root;
        try {
            root = build(args[args.length - 1]);
        } catch (UncheckedIOException e) {
            System.err.println("Cannot read file " + e.getCause().getMessage());
            return;
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            return;
        }
        root.optimize();
        try {
            root.check();
        } catch (FailedCheckException e) {
            System.err.println("Fatal. Aborting...");
            return;
        }
//        BytecodeEmitter emitter = new BytecodeEmitter((Scope) root);
//        try {
//            System.out.println(emitter.emitToDebugString());
//        } catch (IOException e) {
//            System.err.println("Error write creating bytecode " + e.getMessage());
//        }
    }
}
