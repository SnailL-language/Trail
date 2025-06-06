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
     * Создает исключение с указанным списком ошибок компиляции
     * @param errors список ошибок компиляции
     */
    public FailedCheckException(List<CompilationError> errors) {
        super(errors != null && !errors.isEmpty() ? errors.getFirst().toString() : "Ошибка проверки");
        if (errors != null) {
            this.errors.addAll(errors);
        }
    }
    
    /**
     * Возвращает список ошибок компиляции
     * @return список ошибок компиляции
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
