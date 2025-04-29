package io.github.snaill;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тестовый класс для проверки программы Trail, которая обрабатывает исходные файлы на языке Snail.
 */
public class TrailTest {

    private static final Path SAMPLES_DIR = Path.of("resources", "test_samples");
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
     * @param filename Имя тестового файла в директории SAMPLES_DIR.
     * @param builderType Тип сборщика ("flatten" или "reflection").
     * @param expectedOut Ожидаемый стандартный вывод.
     * @param expectedErr Ожидаемый вывод ошибок.
     */
    private void runTest(String filename, String builderType, String expectedOut, String expectedErr) {
        Path sourceFile = SAMPLES_DIR.resolve(filename);
        assertTrue(Files.exists(sourceFile), "Test file does not exist: " + sourceFile);

        String[] args = {builderType, sourceFile.toString()};
        Trail.main(args);

        assertEquals(expectedErr, readFile(errFile), "Unexpected error output for " + filename + " with builder " + builderType);
        assertEquals(expectedOut, readFile(outputFile), "Unexpected standard output for " + filename + " with builder " + builderType);
    }

    /**
     * Параметризованный тест для проверки корректности обработки файлов Snail с использованием flatten builder.
     *
     * @param filename Имя тестового файла.
     */
    @ParameterizedTest
    @ValueSource(strings = {
            "only_main.sn",
            "extra_function.sn",
            "string.sn",
            "func_call.sn",
            "array.sn",
            "while.sn",
            "for.sn",
            "assignment.sn",
            "if.sn",
            "big.sn"
    })
    public void testProcessingWithFlatten(String filename) {
        Path sourceFile = SAMPLES_DIR.resolve(filename);
        String expectedOut = readFile(sourceFile);
        runTest(filename, "flatten", expectedOut, "");
    }

    /**
     * Параметризованный тест для проверки корректности обработки файлов Snail с использованием reflection builder.
     *
     * @param filename Имя тестового файла.
     */
    @ParameterizedTest
    @ValueSource(strings = {
            "only_main.sn",
            "extra_function.sn",
            "string.sn",
            "func_call.sn",
            "array.sn",
            "while.sn",
            "for.sn",
            "assignment.sn",
            "if.sn",
            "big.sn"
    })
    public void testProcessingWithReflection(String filename) {
        Path sourceFile = SAMPLES_DIR.resolve(filename);
        String expectedOut = readFile(sourceFile);
        runTest(filename, "reflection", expectedOut, "");
    }

    /**
     * Тест для проверки обработки отсутствия аргументов.
     */
    @Test
    public void testMissingArguments() {
        String[] args = {};
        Trail.main(args);

        assertEquals("trail[options]builderType(flatten,reflection)<file_to_compile>", readFile(errFile), "Unexpected error output for missing arguments");
        assertEquals("", readFile(outputFile), "Unexpected standard output for missing arguments");
    }

    /**
     * Тест для проверки обработки неверного типа сборщика.
     */
    @Test
    public void testInvalidBuilderType() {
        Path sourceFile = SAMPLES_DIR.resolve("only_main.sn");
        String[] args = {"invalid", sourceFile.toString()};
        Trail.main(args);

        assertEquals("trail[options]builderType(flatten,reflection)<file_to_compile>", readFile(errFile), "Unexpected error output for invalid builder type");
        assertEquals("", readFile(outputFile), "Unexpected standard output for invalid builder type");
    }

    /**
     * Тест для проверки обработки несуществующего файла.
     */
    @Test
    public void testNonExistentFile() {
        String[] args = {"reflection", "non_existent_file.sn"};
        Trail.main(args);

        assertEquals("Cannotreadfile", readFile(errFile), "Unexpected error output for non-existent file");
        assertEquals("", readFile(outputFile), "Unexpected standard output for non-existent file");
    }
}