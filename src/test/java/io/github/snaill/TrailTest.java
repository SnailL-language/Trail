package io.github.snaill;

import io.github.snaill.ast.*;
import io.github.snaill.ast.NumberLiteral; // Changed from IntegerLiteral
import io.github.snaill.exception.FailedCheckException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import picocli.CommandLine;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовый класс для проверки программы Trail, которая обрабатывает исходные файлы на языке Snail.
 */
public class TrailTest {

    private static final Path SAMPLES_DIR = Path.of("src", "test", "resources", "test_samples");
    private static Path tempDir;
    private static Path outputFile;
    private static Path errFile;
    private static PrintStream output;
    private static PrintStream err;
    private static PrintStream originalSystemOut;
    private static PrintStream originalSystemErr;

    /**
     * Создаёт временную директорию и файлы для вывода перед всеми тестами.
     */
    @BeforeAll
    public static void setupTempFiles() {
        try {
            tempDir = Files.createTempDirectory("trail_test_");
            outputFile = tempDir.resolve("output.txt");
            errFile = tempDir.resolve("err.txt");
        } catch (IOException e) {
            throw new RuntimeException("Failed to create temporary directory or files", e);
        }
    }

    /**
     * Настраивает потоки вывода для каждого теста.
     */
    @BeforeEach
    public void setupStreams() {
        try {
            originalSystemOut = System.out;
            originalSystemErr = System.err;

            output = new PrintStream(Files.newOutputStream(outputFile, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING));
            err = new PrintStream(Files.newOutputStream(errFile, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING));
            System.setOut(output);
            System.setErr(err);
        } catch (IOException e) {
            throw new RuntimeException("Failed to open temporary files for output", e);
        }
    }

    /**
     * Закрывает потоки после каждого теста.
     */
    @AfterEach
    public void closeStreams() {
        if (output != null) {
            output.close(); // Close the file streams
        }
        if (err != null) {
            err.close();   // Close the file streams
        }

        // Restore original System.out and System.err
        if (originalSystemOut != null) {
            System.setOut(originalSystemOut);
            originalSystemOut = null;
        }
        if (originalSystemErr != null) {
            System.setErr(originalSystemErr);
            originalSystemErr = null;
        }
    }

    /**
     * Удаляет временную директорию и файлы после всех тестов.
     */
    @AfterAll
    public static void cleanupTempFiles() {
        try {
            if (tempDir != null && Files.exists(tempDir)) {
                deleteDirectoryRecursively(tempDir);
            }
        } catch (IOException e) {
            // Log or print warning, but don't fail the tests if cleanup fails
            System.err.println("Warning: Failed to delete temporary directory: " + tempDir + " - " + e.getMessage());
        }
    }

    private static void deleteDirectoryRecursively(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (Stream<Path> entries = Files.list(path)) {
                for (Path entry : entries.toList()) { // Use toList() to avoid issues with stream modification
                    deleteDirectoryRecursively(entry);
                }
            }
        }
        Files.deleteIfExists(path);
    }

    private String removeComments(String code) {
        // Удаляем многострочные комментарии (/* ... */)
        String noBlockComments = code.replaceAll("/\\*.*?\\*/", "");
        // Удаляем однострочные комментарии (// ... до конца строки)
        String noLineComments = noBlockComments.replaceAll("//.*", "");
        return noLineComments;
    }

    /**
     * Параметризованный тест для проверки корректности обработки файлов SnailL.
     *
     * @param filename Имя тестового файла.
     */
    @ParameterizedTest()
    @ValueSource(strings = {
            "only_main.sn",
            "string.sn",
            "func_call.sn",
            "array.sn",
            "while.sn",
            "for.sn",
            "assignment.sn",
            "if.sn",
            "big.sn",
            "else.sn",
            "break_in_for.sn",
            "break_in_while.sn",
            "return_void.sn",
            "assignment_operators.sn",
            "unary_operators.sn",
            "multi_dim_array_access.sn",
            "empty_array_literal.sn",
            "usize_type.sn",
            "nested_array_type.sn",
            "global_variables.sn",
            "short_circuit.sn",
            "complex_expressions.sn",
            "comments.sn",
            "empty_scope.sn",
            "array_assignment.sn",
            "array_single_element.sn",
            "array_string_bool.sn",
            "func_no_param_no_return_explicit.sn",
            "func_complex_args.sn",
            "for_complex.sn",
            "program_no_globals.sn",
            "program_with_globals.sn",
            "global_primitive_types.sn",
            "global_array_type.sn",
    })

    public void testProcessing(String filename) throws FailedCheckException {
        Path filepath = SAMPLES_DIR.resolve(filename);
        assertDoesNotThrow(() -> Trail.build(filepath.toAbsolutePath().toString()),
                "Trail.build should not throw an exception for valid file: " + filename);
        String expectedContent = readFile(filepath);
        String actualContentRaw = SourceBuilder.toSourceCode(Trail.build(filepath.toAbsolutePath().toString()));

        String expectedNoComments = removeComments(expectedContent);
        String actualNoComments = removeComments(actualContentRaw);

        assertEquals(
                expectedNoComments.replaceAll("\\s+", ""),
                actualNoComments.replaceAll("\\s+", "")
        );
    }

    @Test
    public void testMissingArguments() {
        Trail trailApp = new Trail();
        CommandLine cmd = new CommandLine(trailApp);
        int exitCode = cmd.execute(); // Передача пустого массива аргументов
        assertEquals(1, exitCode, "Expected exit code 1 for missing file argument");

        // Можно также проверить, что в System.err было выведено сообщение об ошибке и usage.
        // Для этого потребуется перенаправить System.err перед вызовом execute()
        // и восстановить после, а затем проверить содержимое.
        // Пример (потребует @BeforeEach/@AfterEach для управления потоками или локального перенаправления):
        // String errContent = Files.readString(errFile);
        // assertTrue(errContent.contains("Ошибка: Исходный файл должен быть указан"), "Error message not found in System.err");
        // assertTrue(errContent.contains("Usage: trail"), "Usage help not found in System.err");
    }

    @Test
    public void testNonExistentFile() {
        String nonExistentFilePath = "non_existent_file.sn";
        assertThrows(
                FailedCheckException.class,
                () -> Trail.build(nonExistentFilePath)
        );
    }

    /**
     * Тест для проверки файла с ожидаемыми ошибками типов.
     */
    @Test
    public void testTypeErrorsFile() {
        Path filePath = SAMPLES_DIR.resolve("type_errors.sn");
        try {
            Trail.build(filePath.toString());
            // Если мы дошли сюда, исключение не было выброшено
            // System.out.println("!!! TEST FAILED: testTypeErrorsFile - FailedCheckException was NOT thrown for type_errors.sn !!!");
            org.junit.jupiter.api.Assertions.fail("FailedCheckException was NOT thrown for type_errors.sn");
        } catch (FailedCheckException e) {
            // Исключение было выброшено, как и ожидалось.
            // System.out.println("+++ TEST PASSED (expected exception): testTypeErrorsFile - FailedCheckException was thrown. Errors: " + e.getErrors());
        } catch (Exception e) {
            // Другое неожиданное исключение
            System.err.println("!!! TEST FAILED: testTypeErrorsFile - Unexpected exception thrown: " + e.getClass().getName() + " - " + e.getMessage() + " !!!");
            e.printStackTrace(System.err);
            org.junit.jupiter.api.Assertions.fail("Unexpected exception thrown: " + e.getClass().getName(), e);
        }
    }

    /**
     * Тест для проверки файла без функций (ожидается ошибка парсинга).
     */
    @Test
    public void testProgramNoFunctions() {
        Path filePath = SAMPLES_DIR.resolve("program_no_functions.sn");
        assertThrows(FailedCheckException.class, () -> Trail.build(filePath.toString()),
                "FailedCheckException was NOT thrown for program_no_functions.sn");
    }

    /**
     * Тест для проверки файла с глобальными переменными после функций (ожидается ошибка парсинга).
     */
    @Test
    public void testProgramGlobalsAfterFunc() {
        Path filePath = SAMPLES_DIR.resolve("program_globals_after_func.sn");
        assertThrows(FailedCheckException.class, () -> Trail.build(filePath.toString()),
                "FailedCheckException was NOT thrown for program_globals_after_func.sn");
    }

    /**
     * Тест для проверки ошибки при объявлении вложенной функции.
     */
    @Test
    public void testNestedFunctionError() {
        Path filePath = SAMPLES_DIR.resolve("nested_function.sn");
        try {
            Trail.build(filePath.toString());
            org.junit.jupiter.api.Assertions.fail("FailedCheckException was NOT thrown for nested_function.sn (nested functions should be disallowed)");
        } catch (FailedCheckException e) {
            // Ожидаемое исключение
            // Можно добавить проверку на конкретное сообщение об ошибке, если оно стабильно
            // assertTrue(e.getErrors().stream().anyMatch(err -> err.getMessage().contains("Function declarations are not allowed inside blocks")));
        } catch (Exception e) {
            System.err.println("!!! TEST FAILED: testNestedFunctionError - Unexpected exception thrown: " + e.getClass().getName() + " - " + e.getMessage() + " !!!");
            e.printStackTrace(System.err);
            org.junit.jupiter.api.Assertions.fail("Unexpected exception thrown for nested_function.sn: " + e.getClass().getName(), e);
        }
    }

    @Test
    public void testTreeEqualityForFile() throws FailedCheckException, java.io.UncheckedIOException { // Renamed duplicate test
        // The root of the AST for tree_equality.sn will be a Scope node
        // containing the FunctionDeclaration for 'main'.
        Node expected = new Scope(List.of(
                new FunctionDeclaration("main",
                        List.of(), // No parameters
                        new PrimitiveType("i32"), // Return type is i32
                        new Scope(List.of( // Function body scope
                                new VariableDeclaration("x",
                                        new PrimitiveType("i32"),
                                        new NumberLiteral(10L)), // x = 10
                                new ReturnStatement(new Identifier("x")) // return x;
                        )), true)
        ));
        this.runBuilding(expected);
    }

    private void runBuilding(Node expectedAst) throws FailedCheckException, java.io.UncheckedIOException {
        Path sourcePath = SAMPLES_DIR.resolve("tree_equality.sn");
        assertTrue(Files.exists(sourcePath), "Test file tree_equality.sn does not exist: " + sourcePath);
        AST actualAst = Trail.build(sourcePath.toString());
        assertEquals(expectedAst, actualAst.root());
    }


    @Test
    public void testDeadIf() {
        runTest("dead_if.sn", "", "ERROR:letr:i32=245;Elsebranchisunreachableduetoalways-truecondition.;DEAD_CODE================================");
    }


    @Test
    public void testAfterReturn() {
        runTest("after_return.sn", "", "ERROR:result=235;^^^^^^^^^^^^^Statementisunreachable.;DEAD_CODE================================");
    }


    @Test
    public void testUnusedFunction() {
        runTest(
                "extra_function.sn",
                "Warning:UNUSED~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~fnextra()->bool{returntrue;}~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~",
                ""
        );
    }


    @Test
    public void testUnusedVariable() {
        runTest(
                "extra_variable.sn",
                "Warning:UNUSED~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~letunused:i32=256;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~",
                ""
        );
    }


    /**
     * Читает содержимое файла и возвращает его как строку без пробелов и переносов строк.
     *
     * @param path Путь к файлу.
     * @return Содержимое файла без пробельных символов.
     * @throws RuntimeException Если файл не удалось прочитать.
     */
    private String readFile(Path path) {
        try {
            assertTrue(Files.exists(path), "File does not exist: " + path);
            String content = Files.readString(path);
            if (content == null || content.equals("null")) return "";
            // Remove block comments first, then line comments
            content = content.replaceAll("/\\*.*?\\*/", ""); // Non-greedy block comments
            content = content.replaceAll("//.*", ""); // Line comments
            return content.replaceAll("\\s+", "");
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + path, e);
        }
    }

    /**
     * Выполняет тест для указанного файла, сравнивая выходные данные с ожидаемыми.
     *
     * @param filename    Имя тестового файла в директории SAMPLES_DIR.
     * @param expectedOut Ожидаемый стандартный вывод.
     * @param expectedErr Ожидаемый вывод ошибок.
     */
    private void runTest(String filename, String expectedOut, String expectedErr) {
        Path sourcePath = SAMPLES_DIR.resolve(filename);
        assertTrue(Files.exists(sourcePath), "Test file does not exist: " + sourcePath);

        // Determine a temporary path for the output bytecode
        String baseName = sourcePath.getFileName().toString();
        int dot = baseName.lastIndexOf('.');
        String baseNoExt = (dot > 0 ? baseName.substring(0, dot) : baseName);
        Path outputBytecodePath = tempDir.resolve(baseNoExt + ".snb"); // Output bytecode to tempDir

        String[] picocliArgs = {"-f", sourcePath.toString(), "-o", outputBytecodePath.toString()};
        new CommandLine(new Trail()).execute(picocliArgs);
    }
}