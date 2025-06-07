package io.github.snaill;

import io.github.snaill.ast.*;
import io.github.snaill.bytecode.BytecodeEmitter;
import io.github.snaill.bytecode.BytecodeEmitter.BytecodeEmitterException;
import io.github.snaill.bytecode.DebugBytecodeViewer;
import io.github.snaill.exception.FailedCheckException;
import io.github.snaill.parser.SnailLexer;
import io.github.snaill.parser.SnailParser;
import io.github.snaill.result.CompilationError;
import io.github.snaill.result.ErrorType;
import io.github.snaill.result.Result;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Callable;

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
        public void checkUnusedFunctions(Set<FunctionDeclaration> unusedFunctions) {
            root.checkUnusedFunctions(unusedFunctions);
        }
    }


    /**
     * Собирает AST из исходного файла.
     *
     * @param filename Путь к исходному файлу
     * @return Собранное AST
     */
    public static AST build(String filename) throws FailedCheckException, UncheckedIOException {
        logger.debug("Starting build process for file: {}", filename);
        Objects.requireNonNull(filename);
        final CharStream stream;
        try {
            stream = CharStreams.fromFileName(filename);
            logger.debug("Successfully read file: {}", filename);
        } catch (IOException e) {
            logger.error("Failed to read file {}: {}", filename, e.getMessage());
            CompilationError error = new CompilationError(
                    ErrorType.INTERNAL_ERROR,
                    filename,
                    "Cannot read source file: " + e.getMessage(),
                    "Ensure the file exists and is readable."
            );
            throw new FailedCheckException(error.toString());
        }
        SnailParser parser = new SnailParser(
                new CommonTokenStream(
                        new SnailLexer(stream)
                )
        );
        SnailParser.ProgramContext tree = parser.program();
        logger.debug("Parsed program context for: {}", filename);
        final ASTBuilder builder = new ASTReflectionBuilder();
        Node initialAstNode = builder.build(tree); // Can throw FailedCheckException for critical parsing/AST building errors
        logger.debug("Successfully built initial AST structure for: {}", filename);

        AST astNode;
        if (initialAstNode instanceof Scope) {
            astNode = new ASTImpl((Scope) initialAstNode);
        } else if (initialAstNode instanceof AST) {
            astNode = (AST) initialAstNode;
        } else {
            logger.error("Unexpected AST node type after initial build: {}", initialAstNode.getClass().getName());
            throw new RuntimeException("Unexpected AST node type: " + initialAstNode.getClass().getName());
        }

        // Perform semantic checks
        Check semanticChecker = new Check(); // Assuming Check is in the same package or imported
        logger.debug("Trail.build for {}: BEFORE semanticChecker.check() for AST node: {}", filename, (astNode != null ? astNode.getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(astNode)) : "null"));
        List<CompilationError> semanticErrors = semanticChecker.check(astNode);
        logger.debug("Trail.build for {}: AFTER semanticChecker.check(). Errors found: {} {}", filename, (semanticErrors != null ? semanticErrors.size() : "null list"), (semanticErrors != null && !semanticErrors.isEmpty() ? "-> " + semanticErrors : ""));

        if (!semanticErrors.isEmpty()) {
            StringBuilder errorMessages = new StringBuilder("Semantic errors found during build process for file '" + filename + "':\n");
            for (CompilationError error : semanticErrors) {
                errorMessages.append("- ").append(error.toString()).append("\n");
            }
            logger.error(errorMessages.toString()); // Log the errors
            logger.error("!!! Trail.build for {}: Preparing to throw FailedCheckException due to {} semantic errors. First error: {}", filename, semanticErrors.size(), semanticErrors.isEmpty() ? "N/A" : semanticErrors.getFirst().toString());
            throw new FailedCheckException(errorMessages.toString().trim()); // Throw if errors are present
        }

        logger.debug("Semantic checks passed for: {}", filename);
        return astNode; // Return the fully checked AST
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Trail()).execute(args);
        System.exit(exitCode);
    }

    /**
     * Основной метод для компиляции и выполнения файла, реализующий интерфейс Callable.
     *
     * @return Код возврата программы (0 = успех, 1 = ошибка)
     */
    @Override
    public Integer call() {
        try {
            // Если указан файл для отладочного просмотра байткода
            if (debugBytecodeFile != null) {
                // Для отладочного просмотра байткода исходный файл не требуется
                return debugBytecode(debugBytecodeFile);
            }

            // Начать отладочный вывод, если включен режим отладки
            if (debug) {
                logger.debug("Starting compiler with source file: {}", sourceFile);
            }

            // Проверяем наличие исходного файла
            if (sourceFile == null) {
                logger.error("Не указан исходный файл для компиляции");
                return 1;
            }

            boolean hasErrors = false; // Флаг для отслеживания возникновения ошибок
            AST astNode = null; // Инициализировать astNode как null

            // 1. Построение AST (включая семантические проверки)
            if (debug) {
                logger.debug("Starting AST build process (including semantic checks) for source: {}", sourceFile);
            }
            try {
                astNode = build(sourceFile); // build теперь выбрасывает FailedCheckException при ошибках парсинга или семантических ошибках, или UncheckedIOException

                // Если build() завершается без исключений, AST валидно и семантические проверки пройдены.
                logger.info("AST build and semantic checks completed successfully.");

                if (emitSource) {
                    // System.out.println(SourceBuilder.toSourceCode(astNode));
                }

            } catch (FailedCheckException e) {
                if (e.getErrors() != null) {
                    for (CompilationError error : e.getErrors()) {
                        System.err.println(error.toString()); // Print to System.err for test compatibility
                    }
                } else {
                    // Запасной вариант, если getErrors() возвращает null, хотя наши изменения нацелены на предотвращение этого
                    System.err.println(e.getMessage());
                }
                hasErrors = true; // Errors occurred, astNode might be null or partially built
            } catch (UncheckedIOException e) { // Перехват специфичного IO исключения из build()
                logger.error("Ошибка чтения файла: {} - {}", sourceFile, e.getMessage());
                System.err.println("File Read Error: " + e.getCause().getMessage()); // Вывести сообщение о причине для ясности
                if (debug) {
                    e.printStackTrace();
                }
                return 1; // Завершить досрочно при ошибках ввода-вывода
            }

            // 2. Проверка мертвого кода (Dead Code Analysis)
            if (astNode != null && !hasErrors) { // Проверить astNode и отсутствие предыдущих ошибок
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
                        // TODO: Обработать предупреждения из deadCodeResults, если применимо (вывести в System.out)
                    }
                    if (foundDeadCodeErrorThisPass) {
                        hasErrors = true;
                    }
                }
            } // Закрывает 'if (astNode != null && !hasErrors)' для анализа мертвого кода

            // 3. Проверка неиспользуемых переменных и функций (Unused Symbol Checks)
            if (astNode != null && !hasErrors) { // Проверить astNode и отсутствие предыдущих ошибок
                if (debug) {
                    logger.debug("Starting unused symbol checks for source: {}", sourceFile);
                }
                List<io.github.snaill.result.Warning> unusedSymbolWarnings = astNode.root().getUnusedSymbolWarnings();
                if (unusedSymbolWarnings != null && !unusedSymbolWarnings.isEmpty()) {
                    for (io.github.snaill.result.Warning warning : unusedSymbolWarnings) {
                        // System.out.println(warning.toString()); // Вывести предупреждения в System.out
                    }
                    // Примечание: Предупреждения о неиспользуемых символах обычно не приводят к сбою компиляции (код выхода 1)
                    // если не установлены специальные флаги компилятора. Текущие тесты ожидают код выхода 0, если присутствуют только предупреждения.
                }
            } // Закрывающая скобка для 'if (astNode != null && !hasErrors)' для проверки неиспользуемых символов

            // Если были ошибки мертвого кода или семантические ошибки, выходим с кодом 1
            if (hasErrors) {
                return 1;
            }

            // Если ошибок не было, продолжаем генерацию байткода
            // Эта проверка уже неявно обрабатывается блоком 'if (hasErrors) { return 1; }' перед этим блоком
            String outputFile;
            if (emitBytecodeFile != null) {
                outputFile = emitBytecodeFile;
            } else {
                String baseName = sourceFile.replaceFirst("\\.[^.]+$", "");
                outputFile = baseName + ".snail";
            }

            // Генерируем байткод
            try {
                // Убедитесь, что astNode не равен null перед попыткой его использования для генерации байткода
                if (astNode == null) {
                    // Этот случай в идеале должен быть перехвачен флагом 'hasErrors' ранее,
                    // но в качестве меры предосторожности:
                    logger.error("AST node is null, cannot proceed to bytecode emission. This indicates an earlier error.");
                    return 1;
                }
                BytecodeEmitter emitter = new BytecodeEmitter();
                // Логика ASTImpl обрабатывается в методе build(), astNode должен быть AST (который реализует ASTImpl)
                emitter.emitBytecode(astNode, outputFile);
                logger.info("Bytecode emission completed successfully to {}", outputFile);
                return 0; // Успешное выполнение
            } catch (BytecodeEmitterException e) {
                logger.error("Ошибка генерации байткода: {}", e.getMessage());
                System.err.println("Bytecode Emitter Error: " + e.getMessage()); // Для видимости в тестах
                if (debug) {
                    e.printStackTrace();
                }
                return 1;
            } catch (Exception e) { // Это теперь для действительно непредвиденных ошибок во время генерации байткода
                logger.error("Непредвиденная ошибка во время генерации байткода: {}", e.getMessage());
                System.err.println("Unexpected Bytecode Generation Error: " + e.getMessage()); // Для видимости в тестах
                if (debug) {
                    e.printStackTrace();
                }
                return 1;
            }
        } catch (
                Exception e) { // Этот внешний блок catch обрабатывает действительно непредвиденные ошибки, не перехваченные более специфичными обработчиками выше.
            logger.error("Общая ошибка компиляции: {}", e.getMessage());
            System.err.println("Compiler Error: " + e.getMessage()); // Для видимости в тестах
            if (debug) {
                e.printStackTrace();
            }
            return 1;
        }
    }

    /**
     * Проверяет исходный код на синтаксические ошибки.
     *
     * @param sourceCode Исходный код для проверки.
     * @param sourceName Имя источника (например, имя файла) для контекста ошибок.
     * @return Список обнаруженных ошибок компиляции.
     */
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
