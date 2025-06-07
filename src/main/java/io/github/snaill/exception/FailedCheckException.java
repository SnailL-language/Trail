package io.github.snaill.exception;

import io.github.snaill.result.CompilationError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FailedCheckException extends Exception {
    private final List<CompilationError> errors = new ArrayList<>();

    public FailedCheckException() { super(); }

    public FailedCheckException(String message) { super(message); }

    public FailedCheckException(Throwable cause) { super(cause); }

    public FailedCheckException(String message, Throwable cause) { super(message, cause); }
    
    /**
     * Creates an exception with the specified list of compilation errors
     * @param errors list of compilation errors
     */
    public FailedCheckException(List<CompilationError> errors) {
        super(errors != null && !errors.isEmpty() ? errors.getFirst().toString() : "Check failed");
        if (errors != null) {
            this.errors.addAll(errors);
        }
    }
    
    /**
     * Returns the list of compilation errors
     * @return list of compilation errors
     */
    public List<CompilationError> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    @Override
    public String getMessage() {
        if (!errors.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (CompilationError error : errors) {
                sb.append(error.toString()).append("\n");
            }
            return sb.toString();
        }
        return super.getMessage();
    }

    @Override
    public String toString() {
        return getMessage();
    }
}
