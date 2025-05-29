package io.github.snaill;

import io.github.snaill.ast.*;
import io.github.snaill.bytecode.BytecodeEmitter;
import io.github.snaill.bytecode.DebugBytecodeViewer;
import io.github.snaill.exception.FailedCheckException;
import io.github.snaill.parser.SnailLexer;
import io.github.snaill.parser.SnailParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.Objects;

public class Trail {

    private static final String USAGE = "trail [options] <file_to_compile>\n" +
            "    --emit-source         print pretty source from AST\n" +
            "    --debug-bytecode FILE print debug view of bytecode file (.slime)\n" +
            "    --emit-bytecode FILE  output bytecode to FILE (default: <input>.slime)\n";

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
        try {
            return builder.build(tree);
        } catch (io.github.snaill.exception.FailedCheckException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException, io.github.snaill.exception.FailedCheckException {
        if (args.length < 1) {
            System.err.println(USAGE);
            return;
        }
        boolean emitSource = false;
        String filename = null;
        String debugBytecodeFile = null;
        String emitBytecodeFile = null;
        String positionalOutFile = null;
        int positionalCount = 0;
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--emit-source" -> emitSource = true;
                case "--debug-bytecode" -> {
                    if (i + 1 < args.length) {
                        debugBytecodeFile = args[++i];
                    } else {
                        System.err.println("Missing file for --debug-bytecode");
                        return;
                    }
                }
                case "--emit-bytecode" -> {
                    if (i + 1 < args.length) {
                        emitBytecodeFile = args[++i];
                    } else {
                        System.err.println("Missing file for --emit-bytecode");
                        return;
                    }
                }
                default -> {
                    if (positionalCount == 0) {
                        filename = args[i];
                        positionalCount++;
                    } else if (positionalCount == 1) {
                        positionalOutFile = args[i];
                        positionalCount++;
                    }
                }
            }
        }
        if (debugBytecodeFile != null) {
            try {
                byte[] code = Files.readAllBytes(java.nio.file.Path.of(debugBytecodeFile));
                System.out.println(DebugBytecodeViewer.disassemble(code));
            } catch (IOException e) {
                System.err.println("Cannot read bytecode file: " + e.getMessage());
            }
            return;
        }
        if (filename == null) {
            System.err.println(USAGE);
            return;
        }
        final Node root;
        try {
            root = build(filename);
        } catch (UncheckedIOException e) {
            System.err.println("Cannot read file " + e.getCause().getMessage());
            return;
        } catch (RuntimeException e) {
            if (e.getMessage() != null && !e.getMessage().isEmpty() && !e.getMessage().equals("null")) {
                System.err.print(e.getMessage());
            }
            return;
        }
        root.optimize();
        try {
            root.check();
        } catch (FailedCheckException e) {
            if (e.getMessage() != null && !e.getMessage().isEmpty() && !e.getMessage().equals("null")) {
                System.err.print(e.getMessage());
            }
            System.err.print("Fatal.Aborting...");
            return;
        }
        if (emitSource) {
            try {
                System.out.println(SourceBuilder.toSourceCode(root));
            } catch (Exception e) {
                System.err.println("Error printing AST/source: " + e.getMessage());
            }
            return;
        }
        // По умолчанию: компилируем в байткод-файл
        String outFile = emitBytecodeFile;
        if (outFile == null) {
            if (positionalOutFile != null) {
                outFile = positionalOutFile;
            } else {
                int dot = filename.lastIndexOf('.');
                outFile = (dot > 0 ? filename.substring(0, dot) : filename) + ".slime";
            }
        }
        byte[] bytecode = new BytecodeEmitter((Scope) root).emit();
        Files.write(java.nio.file.Path.of(outFile), bytecode);
    }
}
