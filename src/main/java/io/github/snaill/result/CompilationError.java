package io.github.snaill.result;

public record CompilationError(ErrorType type, String before, String error, String after) implements Result {

    private static final String NEW_LINE = System.lineSeparator();
    private static final String SEPARATION = "=".repeat(32) + NEW_LINE;

    @Override
    public String toString() {
        return String.format(
                "ERROR:%s%s%s",
                before,
                type == ErrorType.DEAD_CODE ? error + ";DEAD_CODE================================" : error,
                after.isEmpty() ? "" : ";" + after + ";================================"
        );
    }

    @Override
    public boolean isCritical() {
        return true;
    }

    public String getMessage() {
        return error;
    }
}
