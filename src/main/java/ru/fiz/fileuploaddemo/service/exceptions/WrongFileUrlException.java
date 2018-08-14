package ru.fiz.fileuploaddemo.service.exceptions;

public class WrongFileUrlException extends RuntimeException {

    public WrongFileUrlException(String msg) {
        super(msg);
    }

    public WrongFileUrlException(String message, Throwable cause) {
        super(message, cause);
    }
}
