package io.github.karMiguel.capzip.exceptions;

public class PasswordInvalidException extends RuntimeException {
    public PasswordInvalidException(String message) {
        super(message);
    }
}