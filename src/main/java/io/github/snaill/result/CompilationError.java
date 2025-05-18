package io.github.snaill.result;

public class CompilationError implements Result {

    private static final String NEW_LINE = System.lineSeparator();
    private static final String SEPARATION = "=".repeat(32) + NEW_LINE;
    
    private final ErrorType type;
    private final String before;
    private final String error;
    private final String after;

    public CompilationError(ErrorType type, String before, String error, String after) {
        this.type = type;
        this.before = before;
        this.error = error;
        this.after = after;
    }

    @Override
    public String toString() {
        return String.format(
            "ERROR: %s%s%s%s%s%s%s%s%s%s%s",  
            NEW_LINE,
            before,
            NEW_LINE, 
            type,
            NEW_LINE,
            SEPARATION,
            error,
            NEW_LINE,
            SEPARATION,
            after,
            NEW_LINE
        );
    }

    @Override
    public boolean isCritical() {
        return true;
    }
}
