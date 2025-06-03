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
import io.github.snaill.result.Result;
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
            logger.error("Error debugging bytecode: " + e.getMessage());
            if (debug) {
                e.printStackTrace();
            }
            return 1;
        }
    }
    
    /**
     * Выводит список ошибок компиляции в консоль
     * @param errors список ошибок компиляции
     */
    private void printCompilationError(List<CompilationError> errors) {
        if (errors == null || errors.isEmpty()) {
            logger.error("Неизвестная ошибка компиляции");
            return;
        }
        
        for (CompilationError error : errors) {
            logger.error(error.toString());
        }
    }

    /**
     * Класс-адаптер для обертывания Scope в AST
     */
    private static class ASTImpl implements AST {
        private final Scope root;
        
        public ASTImpl(Scope root) {
            this.root = root;
        }
        
        @Override
        public Scope getRoot() {
            return root;
        }
        
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
    public static AST build(String filename) {
        logger.debug("Starting build process for file: " + filename);
        Objects.requireNonNull(filename);
        final CharStream stream;
        try {
            stream = CharStreams.fromFileName(filename);
            logger.debug("Successfully read file: " + filename);
        } catch (IOException e) {
            logger.debug("Failed to read file: " + filename);
            throw new UncheckedIOException(e);
        }
        SnailParser parser = new SnailParser(
                new CommonTokenStream(
                        new SnailLexer(stream)
                )
        );
        SnailParser.ProgramContext tree = parser.program();
        logger.debug("Parsed program context for: " + filename);
        final ASTBuilder builder = new ASTReflectionBuilder();
        try {
            Node result = builder.build(tree);
            logger.debug("Successfully built AST for: " + filename);
            if (result instanceof Scope) {
                return new ASTImpl((Scope)result);
            } else if (result instanceof AST) {
                return (AST)result;
            } else {
                throw new RuntimeException("Unexpected AST node type: " + result.getClass().getName());
            }
        } catch (io.github.snaill.exception.FailedCheckException e) {
            logger.debug("Failed to build AST for: " + filename);
            throw new RuntimeException(e);
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
                logger.debug("Starting compiler with source file: " + sourceFile);
            }
            
            // Проверяем наличие исходного файла
            if (sourceFile == null) {
                logger.error("Не указан исходный файл для компиляции");
                return 1;
            }
            
            // Построение AST
            Node node = build(sourceFile);
            
            // Вывод исходного кода на основе AST, если указана опция --emit-source
            if (emitSource) {
                System.out.println(node);
            }
    
            // Проверка типов и другие статические проверки
            if (debug) {
                logger.debug("Starting check process for source: " + sourceFile);
            }
            try {
                Check check = new Check();
                node.accept(check);
    
                if (debug) {
                    logger.debug("Starting check process for source: " + sourceFile);
                }
                check.check(node instanceof AST ? ((AST) node).getRoot() : (Scope) node);
                logger.info("Check completed successfully.");
            } catch (FailedCheckException e) {
                printCompilationError(e.getErrors());
                return 1;
            }
            
            // Определяем выходной файл для байткода
            String outputFile;
            if (emitBytecodeFile != null) {
                outputFile = emitBytecodeFile;
            } else {
                String baseName = sourceFile.replaceFirst("\\.[^.]+$", "");
                outputFile = baseName + ".snb";
            }
    
            // Генерируем байткод
            try {
                BytecodeEmitter emitter = new BytecodeEmitter();
                if (node instanceof AST) {
                    emitter.emitBytecode((AST) node, outputFile);
                } else if (node instanceof Scope) {
                    // Создаем адаптер для Scope
                    emitter.emitBytecode(new ASTImpl((Scope) node), outputFile);
                } else {
                    throw new RuntimeException("Unexpected node type: " + node.getClass().getName());
                }
                return 0; // Успешное выполнение
            } catch (BytecodeEmitterException e) {
                logger.error("Ошибка генерации байткода: " + e.getMessage());
                if (debug) {
                    e.printStackTrace();
                }
                return 1;
            } catch (Exception e) {
                logger.error("Непредвиденная ошибка: " + e.getMessage());
                if (debug) {
                    e.printStackTrace();
                }
                return 1;
            }
        } catch (Exception e) {
            logger.error("Ошибка компиляции: " + e.getMessage());
            if (debug) {
                e.printStackTrace();
            }
            return 1;
        }
    }

    public static List<CompilationError> check(String sourceCode, String sourceName) {
        logger.error("DEBUG: Starting check process for source: " + sourceName);
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
