package io.github.snaill;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TrailTest {

    private static final Path samples_dir = Path.of("resourses", "test_samples");
    
    private static Path outputFile;
    private static Path errFile;
    private static PrintStream output;
    private static PrintStream err;

    @BeforeAll
    public static void createOutput() {
        try {
            outputFile = Files.createTempFile("test", "output");
            errFile = Files.createTempFile("test", "err");
        } catch (IOException e) {
            throw new RuntimeException("Cannot create temp file for tests");
        }
    }

    @BeforeEach
    public void setOutput() {
        try {
            output = new PrintStream(Files.newOutputStream(outputFile));
            err = new PrintStream(Files.newOutputStream(errFile));
        } catch (IOException e) {
            throw new RuntimeException("Cannot open temp file for tests");
        }
        System.setOut(output);
        System.setErr(err);
    }

    @AfterEach
    public void closeOutput() {
        output.close();
    }

    @AfterAll
    public static void removeOutput() {
        try {
            Files.delete(outputFile);
        } catch (IOException e) {
            throw new RuntimeException("Cannot remove temp file");
        }
    }

    private String collectOut(Path file) {
        try {
            return String.join("", Files.readAllLines(file));
        } catch (IOException e) {
            throw new RuntimeException(String.format("Cannot open %s for checking answer", file));
        }
    }

    private void run(String filename, String expectedOut, String expectedErr) {
        String[] args = {samples_dir.resolve(filename).toString()};
        Trail.main(args);
        output.close();
        assertEquals(expectedErr, collectOut(errFile));
        assertEquals(expectedOut, collectOut(outputFile));
    }
    
    @Test
    public void test01_onlyMain() {
        run("only_main.sn", "fnmain()->void{lethello:i32=32;}", "");
    }

    @Test
    public void test02_extraFunction() {
        run("extra_function.sn", "fnmain()->void{lethello:i32=32;}", "");
    }

    @Test
    public void test03_stringInMain() {
        run("string.sn", "fnmain()->void{lethello:string=\"Hello World!\";}", "");
    }


}
