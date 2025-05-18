package io.github.snaill.exception;

public class FailedCheckException extends Exception {

    public FailedCheckException() { super(); }

    public FailedCheckException(String message) { super(message); }

    public FailedCheckException(Throwable cause) { super(cause); }

    public FailedCheckException(String message, Throwable cause) { super(message, cause); }
}
