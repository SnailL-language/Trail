package io.github.snaill;

import io.github.snaill.ast.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

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
            Files.deleteIfExists(outputFile);
            Files.deleteIfExists(errFile);
            Files.deleteIfExists(tempDir);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete temporary files", e);
        }
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
    public void testProcessing(String filename) {
        runTest(filename, "", "");
        Path filepath = SAMPLES_DIR.resolve(filename);
        assertEquals(
            SourceBuilder.toSourceCode(Trail.build(filepath.toString())).replaceAll("\\s+", ""), 
            readFile(filepath)
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
    public void testTreesEquality() {
        Node expected = new Scope(List.of(
                new VariableDeclaration("dp",
                        new ArrayType(
                                new PrimitiveType("i32"),
                                new NumberLiteral(23577L)),
                        new ArrayLiteral(List.of())),
                new FunctionDeclaration("main",
                        List.of(),
                        new PrimitiveType("void"),
                        new Scope(List.of(
                                new VariableDeclaration("x",
                                        new PrimitiveType("i32"),
                                        new NumberLiteral(4564L)),
                                new ReturnStatement(null)
                        )))
        ));
        runBuilding(expected);
    }

    @Test
    public void testDeadIf() {
        runTest(
            "dead_if.sn", 
            "ERROR:else{DEAD_CODE================================letr:i32=245;================================}", 
            "Fatal.Aborting..."
        );
    }

    @Test
    public void testAfterReturn() {
        runTest(
            "after_return.sn", 
            "ERROR:returnresult;DEAD_CODE================================result=235================================",
            "Fatal.Aborting..."    
        );
    }

    @Test
    public void testUnusedFunction() {
        runTest(
            "extra_function.sn",
            "Warning:UNUSED~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~fnextra()->bool{returntrue;}~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~", 
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
            return Files.readString(path).replaceAll("\\s+", "");
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
        Path sourceFile = SAMPLES_DIR.resolve(filename);
        assertTrue(Files.exists(sourceFile), "Test file does not exist: " + sourceFile);

        String[] args = {sourceFile.toString()};
        Trail.main(args);

        assertEquals(expectedOut, readFile(outputFile), "Unexpected standard output for " + filename);
        assertEquals(expectedErr, readFile(errFile), "Unexpected error output for " + filename);
    }

    private void runBuilding(Node expected) {
        final Path tmpFile;
        try {
            tmpFile = Files.createTempFile("test", "tree");
        } catch (IOException e) {
            throw new RuntimeException("Failed to create file: ", e);
        }
        try (BufferedWriter writer = Files.newBufferedWriter(tmpFile)) {
            String source = SourceBuilder.toSourceCode(expected);
            writer.write(source);
            System.out.println(source);
        } catch (IOException e) {
            throw new RuntimeException("Cannot write to file: ", e);
        }
        Node actual = Trail.build(tmpFile.toString());
        try {
            Files.delete(tmpFile);
        } catch (IOException e) {
            throw new RuntimeException("Cannot delete tmp file: ", e);
        }
        assertEquals(expected, actual, "Expected equality of builded trees");
    }
}