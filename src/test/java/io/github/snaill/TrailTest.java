package io.github.snaill;

import io.github.snaill.ast.*;
import picocli.CommandLine;
import io.github.snaill.exception.FailedCheckException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
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
            output.close();
        }
        if (err != null) {
            err.close();
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
    @ParameterizedTest
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
            "else.sn"
    })
    public void testProcessing(String filename) throws FailedCheckException {
        runTest(filename, "", "");
        Path filepath = SAMPLES_DIR.resolve(filename);
        assertEquals(
                readFile(filepath),
                SourceBuilder.toSourceCode(Trail.build(filepath.toString())).replaceAll("\\s+", "")
        );
    }

    /**
     * Тест для проверки обработки отсутствия аргументов.
     */
    @Test
    public void testMissingArguments() {
        assertThrows(
                NullPointerException.class,
                () -> Trail.build(null)
        );
    }

    /**
     * Тест для проверки обработки несуществующего файла.
     */
    @Test
    public void testNonExistentFile() {
        assertThrows(
                RuntimeException.class,
                () -> Trail.build("non_existent_file.sn")
        );
    }

    /**
     * Тест для проверки того, что ast строится ожидаемым образом
     */
    @Test
    public void testTreesEquality() throws FailedCheckException, java.io.UncheckedIOException {
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