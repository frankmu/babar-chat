package com.babar.chat.exception;

public class InvalidUserInfoException extends RuntimeException {
    public InvalidUserInfoException(String message) {
        super(message);
    }
}
