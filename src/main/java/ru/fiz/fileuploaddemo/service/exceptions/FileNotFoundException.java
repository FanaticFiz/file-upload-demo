package ru.fiz.fileuploaddemo.service.exceptions;

public class FileNotFoundException extends RuntimeException {

    public FileNotFoundException(String msg) {
        super(msg);
    }

    public FileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
