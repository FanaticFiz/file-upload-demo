package ru.fiz.fileuploaddemo.service.exceptions;

public class FileExecutionException extends RuntimeException {

    public FileExecutionException(String msg) {
        super(msg);
    }

    public FileExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
