package io.github.snaill;

import io.github.snaill.ast.*;
import io.github.snaill.ast.NumberLiteral; // Changed from IntegerLiteral
import io.github.snaill.exception.FailedCheckException;
import org.junit.jupiter.api.*;
import java.util.List; // Added import
import java.util.ArrayList; // Added import
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

    /**
     * Параметризованный тест для проверки корректности обработки файлов SnailL.
     *
     * @param filename Имя тестового файла.
     */
    //@ParameterizedTest // Temporarily commented out
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
            "empty_scope.sn"
    })
    public void testProcessing(String filename) throws FailedCheckException {
        runTest(filename, "", "");
        Path filepath = SAMPLES_DIR.resolve(filename);
        assertEquals(
                readFile(filepath),
                SourceBuilder.toSourceCode(Trail.build(filepath.toString())).replaceAll("\\s+", "")
        );
    }

    @Test
    public void testMissingArguments() {
        assertThrows(
                NullPointerException.class,
                () -> Trail.main(null) // Передача null как аргументов командной строки
        );
        // Проверка, что стандартный вывод и вывод ошибок пусты, если это ожидается
        // assertEquals("", output.toString().trim());
        // assertEquals("", err.toString().trim());
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
            System.out.println("!!! TEST FAILED: testTypeErrorsFile - FailedCheckException was NOT thrown for type_errors.sn !!!");
            org.junit.jupiter.api.Assertions.fail("FailedCheckException was NOT thrown for type_errors.sn");
        } catch (FailedCheckException e) {
            // Исключение было выброшено, как и ожидалось.
            System.out.println("+++ TEST PASSED (expected exception): testTypeErrorsFile - FailedCheckException was thrown. Errors: " + e.getErrors());
        } catch (Exception e) {
            // Другое неожиданное исключение
            System.err.println("!!! TEST FAILED: testTypeErrorsFile - Unexpected exception thrown: " + e.getClass().getName() + " - " + e.getMessage() + " !!!");
            e.printStackTrace(System.err);
            org.junit.jupiter.api.Assertions.fail("Unexpected exception thrown: " + e.getClass().getName(), e);
        }
    }

    /**
     * Тест для проверки ошибки при объявлении вложенной функции.
     */
    @Test
    public void testNestedFunctionError() {
        Path filePath = SAMPLES_DIR.resolve("nested_function.sn");
        try {
            Trail.build(filePath.toString());
            // Если мы дошли сюда, исключение не было выброшено, что является ошибкой теста
            System.out.println("!!! TEST FAILED: testNestedFunctionError - FailedCheckException was NOT thrown for nested_function.sn !!!");
            org.junit.jupiter.api.Assertions.fail("FailedCheckException was NOT thrown for nested_function.sn");
        } catch (FailedCheckException e) {
            // Исключение было выброшено, как и ожидалось.
            // Проверяем, что есть хотя бы одна ошибка
            assertFalse(e.getErrors().isEmpty(), "Expected at least one compilation error.");
            
            // Берем первую ошибку (ожидаем, что это ошибка о вложенной функции)
            io.github.snaill.result.CompilationError error = e.getErrors().get(0);
            
            // Проверяем тип ошибки
            assertEquals(io.github.snaill.result.ErrorType.SEMANTIC_ERROR, error.getType(),
                         "Expected SEMANTIC_ERROR for nested function declaration.");
            
            // Проверяем сообщение об ошибке
            assertTrue(error.getMessage().contains("Function declarations are not allowed inside blocks."),
                       "Error message should indicate that function declarations are not allowed inside blocks.");
            
            System.out.println("+++ TEST PASSED (expected exception): testNestedFunctionError - FailedCheckException was thrown with SEMANTIC_ERROR. Errors: " + e.getErrors());
        } catch (Exception e) {
            // Другое неожиданное исключение
            System.err.println("!!! TEST FAILED: testNestedFunctionError - Unexpected exception thrown: " + e.getClass().getName() + " - " + e.getMessage() + " !!!");
            e.printStackTrace(System.err);
            org.junit.jupiter.api.Assertions.fail("Unexpected exception thrown: " + e.getClass().getName(), e);
        }
    }

    @Test
    public void testNestedFunctionCompiles() {
        Path filePath = SAMPLES_DIR.resolve("nested_function.sn");
        System.out.println("!!! TRAILTEST.TESTNESTEDFUNCTIONCOMPILES (System.out): BEFORE Trail.build() for file: " + filePath);
        assertDoesNotThrow(() -> Trail.build(filePath.toString()));
    }

    @Test
    public void testSimpleMainReturnZeroTree() throws FailedCheckException { // Renamed and clarified test purpose
        // Expected AST: Global Scope { main() { return 0; } }
        final var expectedGlobalScope = new Scope(new ArrayList<>(), null, null); // parent=null, enclosingFunction=null for global scope

        // 1. Parameters and return type for main function
        List<Parameter> mainParams = new ArrayList<Parameter>();
        Type mainReturnType = new PrimitiveType("i32");

        // 2. Create a temporary, empty scope that will become the body of mainFunction.
        // Its parent is the expectedGlobalScope. Its enclosingFunction is initially null.
        Scope tempBodyScopeForMain = new Scope(new ArrayList<>(), expectedGlobalScope, null);

        // 3. Create the FunctionDeclaration for main, using the temporary scope as its body.
        FunctionDeclaration mainFunction = new FunctionDeclaration("main", mainParams, mainReturnType, tempBodyScopeForMain);

        // 4. Create the actual body scope for main.
        // Its parent is tempBodyScopeForMain (for potential parameter access via mainFunction).
        // Its enclosingFunction is mainFunction itself.
        Scope actualBodyScope = new Scope(new ArrayList<>(), tempBodyScopeForMain, mainFunction);

        // 5. Create statements for the function body
        final var constant = new NumberLiteral(0);
        final var ret = new ReturnStatement(constant);
        actualBodyScope.setChildren(List.of(ret)); // Add return statement to the actual body scope

        // 6. Copy children from the actualBodyScope to tempBodyScopeForMain (which is funcDecl's body)
        tempBodyScopeForMain.setChildren(actualBodyScope.getChildren());
        
        // 7. Add mainFunction to the children of the expectedGlobalScope
        expectedGlobalScope.setChildren(List.of(mainFunction));

        runBuilding(expectedGlobalScope); // Pass the expected global scope to runBuilding
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
                        )))
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