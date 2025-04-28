package io.github.snaill;

import java.io.IOException;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import io.github.snaill.parser.SnailBaseListener;
import io.github.snaill.parser.SnailLexer;
import io.github.snaill.parser.SnailParser;

public class Trail {

    private static final String USAGE = "trail [options] <file_to_compile>";

    private static class MainFuncListener extends SnailBaseListener {

        // @Override
        // public void enterProgram(ProgramContext ctx) {
        //     System.out.println(ctx.statement());
        // }

        @Override
        public void enterFuncDeclaration(SnailParser.FuncDeclarationContext ctx) {
            if (ctx.IDENTIFIER().getText().equals("main")) {
                System.out.println(ctx.getText());
            }
        }
    }
    public static void main(String[] args) {
        if (args.length == 0) {
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
        new ParseTreeWalker().walk(new MainFuncListener(), tree);
        
    }   
}
