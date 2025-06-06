package io.github.snaill;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.snaill.ast.*;
import io.github.snaill.bytecode.BytecodeEmitter;
import io.github.snaill.bytecode.BytecodeEmitter.BytecodeEmitterException;
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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import io.github.snaill.result.CompilationError;
import io.github.snaill.result.ErrorType;
import io.github.snaill.result.Result; // Retained for List<Result>
import java.util.Objects;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Trail - компилятор для языка Snail.
 * Компилирует исходный код на языке Snail в байткод для SnailVM.
 */
@Command(
    name = "trail",
    version = "1.0",
    description = "Trail - компилятор для языка Snail",
    mixinStandardHelpOptions = true,
    requiredOptionMarker = '*'  // Маркер для обязательных опций в справке
)
public class Trail implements Callable<Integer> {

    private static final Logger logger = LoggerFactory.getLogger(Trail.class);

    /**
     * Исходный файл для компиляции
     */
    @Option(names = {"-f", "--file"}, description = "Файл для компиляции", paramLabel = "<file>")
    private String sourceFile;

    /**
     * Вывести форматированный исходный код на основе AST
     */
    @Option(names = "--emit-source", description = "Вывести форматированный исходный код на основе AST")
    private boolean emitSource = false;

    /**
     * Отладочный вывод байткода
     */
    @Option(names = "--debug-bytecode", description = "Вывести отладочную информацию о байткоде", paramLabel = "<bytecode-file>")
    private String debugBytecodeFile;
    
    /**
     * Выходной файл для байткода
     */
    @Option(names = {"--emit-bytecode", "-o"}, description = "Записать байткод в указанный файл", paramLabel = "<output-file>")
    private String emitBytecodeFile;
    
    /**
     * Включить отладочный вывод
     */
    @Option(names = {"-d", "--debug"}, description = "Включить отладочные сообщения")
    private boolean debug = false;

    /**
     * Отладочный просмотр байткода
     * 
     * @param bytecodeFile Путь к файлу байткода для просмотра
     * @return Код возврата (0 = успешно, 1 = ошибка)
     */
    private int debugBytecode(String bytecodeFile) {
        try {
            logger.info("Starting bytecode debug for file: {}", bytecodeFile);
            byte[] bytecode = Files.readAllBytes(Paths.get(bytecodeFile));
            String disassembly = DebugBytecodeViewer.disassemble(bytecode);
            System.out.println(disassembly); // Используем System.out.println для вывода результатов дизассемблера
            return 0;
        } catch (Exception e) {
            logger.error("Error debugging bytecode: {}", e.getMessage());
            if (debug) {
                e.printStackTrace();
            }
            return 1;
        }
    }

    /**
     * }
     * <p>
     * /**
     * Класс-адаптер для обертывания Scope в AST
     */
        private record ASTImpl(Scope root) implements AST {

        @Override
            public <T> T accept(ASTVisitor<T> visitor) {
                return root.accept(visitor);
            }

        @Override
            public Node getChild(int index) {
                return root.getChild(index);
            }

        @Override
            public int getChildCount() {
                return root.getChildCount();
            }

        @Override
            public void setChild(int index, Node child) {
                root.setChild(index, child);
            }

        @Override
            public List<Node> getChildren() {
                return root.getChildren();
            }

        @Override
            public void setChildren(Collection<Node> children) {
                root.setChildren(children);
            }

            @Override
            public int getLine() {
                return root.getLine();
            }

            @Override
            public int getCharPosition() {
                return root.getCharPosition();
            }

            @Override
            public String getSource() {
                return root.getSource();
            }

            @Override
            public String getSourceInfo() {
                return root.getSourceInfo();
            }

            @Override
            public void setSourceInfo(int line, int charPosition, String source) {
                root.setSourceInfo(line, charPosition, source);
            }

        @Override
            public List<Result> checkDeadCode() {
                return root.checkDeadCode();
            }

        @Override
            public void checkUnusedVariables(Set<VariableDeclaration> usedVariables) {
                root.checkUnusedVariables(usedVariables);
            }

        @Override
            public void checkUnusedFunctions(Set<FunctionDeclaration> usedFunctions) {
                root.checkUnusedFunctions(usedFunctions);
            }
        }
    
    /**
     * Строит AST из исходного файла
     * 
     * @param filename Путь к исходному файлу
     * @return Построенное AST
     */
    public static AST build(String filename) throws FailedCheckException, UncheckedIOException {
        logger.debug("Starting build process for file: {}", filename);
        Objects.requireNonNull(filename);
        final CharStream stream;
        try {
            stream = CharStreams.fromFileName(filename);
            logger.debug("Successfully read file: {}", filename);
        } catch (IOException e) {
            logger.debug("Failed to read file: {}", filename);
            throw new UncheckedIOException(e);
        }
        SnailParser parser = new SnailParser(
                new CommonTokenStream(
                        new SnailLexer(stream)
                )
        );
        SnailParser.ProgramContext tree = parser.program();
        logger.debug("Parsed program context for: {}", filename);
        final ASTBuilder builder = new ASTReflectionBuilder();
        Node result = builder.build(tree); // Can throw FailedCheckException
        logger.debug("Successfully built AST for: {}", filename);
        if (result instanceof Scope) {
            return new ASTImpl((Scope)result);
        } else if (result instanceof AST) {
            return (AST)result;
        } else {
            throw new RuntimeException("Unexpected AST node type: " + result.getClass().getName());
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Trail()).execute(args);
        System.exit(exitCode);
    }
    
    /**
     * Основной метод выполнения команды, реализующий интерфейс Callable.
     * 
     * @return Код возврата программы (0 = успешно, 1 = ошибка)
     */
    @Override
    public Integer call() {
        try {
            // Если указан файл для отладочного просмотра байткода
            if (debugBytecodeFile != null) {
                // Для отладки байткода не требуется исходный файл
                return debugBytecode(debugBytecodeFile);
            }
            
            // Начинаем отладочный вывод, если включен режим отладки
            if (debug) {
                logger.debug("Starting compiler with source file: {}", sourceFile);
            }
            
            // Проверяем наличие исходного файла
            if (sourceFile == null) {
                logger.error("Не указан исходный файл для компиляции");
                return 1;
            }

            boolean hasErrors = false; // Flag to track if any errors occurred
            AST astNode = null; // Initialize astNode to null

            // 1. Построение AST и Проверка типов (Semantic Checks)
            if (debug) {
                logger.debug("Starting AST build and semantic check process for source: {}", sourceFile);
            }
            Check semanticChecker = new Check();
            try {
                astNode = build(sourceFile); // build now throws FailedCheckException or UncheckedIOException
                
                // Only proceed to emitSource and semanticCheck if astNode was successfully built
                // (build() would have thrown if astNode is effectively unusable)
                if (emitSource) {
                    System.out.println(SourceBuilder.toSourceCode(astNode));
                }
                semanticChecker.check(astNode.root()); // This can also throw FailedCheckException
                logger.info("AST build and semantic checks completed successfully.");
                
            } catch (FailedCheckException e) {
                if (e.getErrors() != null) {
                    for (CompilationError error : e.getErrors()) {
                        System.err.println(error.toString()); // Print to System.err for test compatibility
                    }
                } else {
                    // Fallback if getErrors() is null, though our changes aim to prevent this
                    System.err.println(e.getMessage()); 
                }
                hasErrors = true; // Errors occurred, astNode might be null or partially built
            } catch (UncheckedIOException e) { // Catch specific IO exception from build()
                logger.error("Ошибка чтения файла: {} - {}", sourceFile, e.getMessage());
                 System.err.println("File Read Error: " + e.getCause().getMessage()); // Print cause message for clarity
                 if (debug) {
                     e.printStackTrace();
                 }
                 return 1; // Exit early for IO errors
            }

            // 2. Проверка мертвого кода (Dead Code Analysis)
            if (astNode != null && !hasErrors) { // Check astNode and no prior errors
                if (debug) {
                    logger.debug("Starting dead code analysis for source: {}", sourceFile);
                }
                List<Result> deadCodeResults = astNode.root().checkDeadCode();
            if (deadCodeResults != null && !deadCodeResults.isEmpty()) {
                boolean foundDeadCodeErrorThisPass = false;
                for (Result deadCodeResult : deadCodeResults) {
                    if (deadCodeResult instanceof CompilationError) {
                        System.err.println(deadCodeResult); // Print errors to System.err
                        foundDeadCodeErrorThisPass = true;
                    }
                    // TODO: Handle Warnings from deadCodeResults if applicable (print to System.out)
                }
                if (foundDeadCodeErrorThisPass) {
                    hasErrors = true;
                }
            }
            } // Closes 'if (astNode != null && !hasErrors)' for Dead Code Analysis

            // 3. Проверка неиспользуемых переменных и функций (Unused Symbol Checks)
            if (astNode != null && !hasErrors) { // Check astNode and no prior errors
                if (debug) {
                    logger.debug("Starting unused symbol checks for source: {}", sourceFile);
                }
                List<io.github.snaill.result.Warning> unusedSymbolWarnings = astNode.root().getUnusedSymbolWarnings();
            if (unusedSymbolWarnings != null && !unusedSymbolWarnings.isEmpty()) {
                for (io.github.snaill.result.Warning warning : unusedSymbolWarnings) {
                    System.out.println(warning.toString()); // Print warnings to System.out
                }
                // Note: Unused symbol warnings typically do not cause the compilation to fail (exit code 1)
                // unless specific compiler flags are set. Current tests expect exit code 0 if only warnings are present.
            }
            } // Closing brace for 'if (astNode != null && !hasErrors)' for unused symbol checks

            // Если были ошибки мертвого кода или семантические ошибки, выходим с кодом 1
            if (hasErrors) {
                return 1;
            }
            
            // Если ошибок не было, продолжаем генерацию байткода
            // This check is already implicitly handled by 'if (hasErrors) { return 1; }' before this block
            String outputFile;
            if (emitBytecodeFile != null) {
                outputFile = emitBytecodeFile;
            } else {
                String baseName = sourceFile.replaceFirst("\\.[^.]+$", "");
                outputFile = baseName + ".snb";
            }
    
            // Генерируем байткод
            try {
                // Ensure astNode is not null before attempting to use it for bytecode emission
                if (astNode == null) {
                    // This case should ideally be caught by 'hasErrors' flag earlier,
                    // but as a safeguard:
                    logger.error("AST node is null, cannot proceed to bytecode emission. This indicates an earlier error.");
                    return 1;
                }
                BytecodeEmitter emitter = new BytecodeEmitter();
                // ASTImpl logic is handled within build() method, astNode should be AST (which ASTImpl implements)
                emitter.emitBytecode(astNode, outputFile);
                logger.info("Bytecode emission completed successfully to {}", outputFile);
                return 0; // Успешное выполнение
            } catch (BytecodeEmitterException e) {
                logger.error("Ошибка генерации байткода: {}", e.getMessage());
                System.err.println("Bytecode Emitter Error: " + e.getMessage()); // For test visibility
                if (debug) {
                    e.printStackTrace();
                }
                return 1;
            } catch (Exception e) { // This is now for truly unexpected errors during bytecode emission
                logger.error("Непредвиденная ошибка во время генерации байткода: {}", e.getMessage());
                System.err.println("Unexpected Bytecode Generation Error: " + e.getMessage()); // For test visibility
                if (debug) {
                    e.printStackTrace();
                }
                return 1;
            }
        }
        catch (Exception e) { // This outermost catch handles truly unexpected errors not caught by more specific handlers above.
            logger.error("Общая ошибка компиляции: {}", e.getMessage());
            System.err.println("Compiler Error: " + e.getMessage()); // For test visibility
            if (debug) {
                e.printStackTrace();
            }
            return 1;
        }
    }

    public static List<CompilationError> check(String sourceCode, String sourceName) {
        logger.error("DEBUG: Starting check process for source: {}", sourceName);
        List<CompilationError> errors = new ArrayList<>();
        try {
            // Создаем поток из исходного кода
            CharStream charStream = CharStreams.fromString(sourceCode);
            
            // Создаем лексер
            SnailLexer lexer = new SnailLexer(charStream);
            lexer.removeErrorListeners();
            
            // Создаем парсер
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            SnailParser parser = new SnailParser(tokens);
            parser.removeErrorListeners();
            
            // Анонимный класс для сбора ошибок
            class ErrorCollector extends org.antlr.v4.runtime.BaseErrorListener {
                private final List<CompilationError> syntaxErrors = new ArrayList<>();
                
                @Override
                public void syntaxError(org.antlr.v4.runtime.Recognizer<?, ?> recognizer, 
                                       Object offendingSymbol, 
                                       int line, 
                                       int charPositionInLine, 
                                       String msg, 
                                       org.antlr.v4.runtime.RecognitionException e) {
                    // Создаем ошибку компиляции из синтаксической ошибки ANTLR
                    CompilationError error = new CompilationError(
                            ErrorType.UNKNOWN_TYPE, // Для синтаксических ошибок используем UNKNOWN_TYPE
                            line + "," + charPositionInLine,
                            "Syntax error: " + msg,
                            offendingSymbol != null ? offendingSymbol.toString() : ""
                    );
                    syntaxErrors.add(error);
                }
                
                public List<CompilationError> getErrors() {
                    return syntaxErrors;
                }
            }
            
            // Создаем и добавляем слушатель ошибок
            ErrorCollector errorCollector = new ErrorCollector();
            lexer.addErrorListener(errorCollector);
            parser.addErrorListener(errorCollector);
            
            // Парсим программу
            parser.program();
            
            // Собираем ошибки
            errors.addAll(errorCollector.getErrors());
            
        } catch (Exception e) {
            // Добавляем исключение как ошибку компиляции
            CompilationError error = new CompilationError(
                ErrorType.UNKNOWN_TYPE, // Для общих ошибок парсинга используем UNKNOWN_TYPE
                "<unknown>",
                "Exception during parsing: " + e.getMessage(),
                ""
            );
            errors.add(error);
            e.printStackTrace();
        }
        return errors;
    }
}
