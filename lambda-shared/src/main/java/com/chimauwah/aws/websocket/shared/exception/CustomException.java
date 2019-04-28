package com.chimauwah.aws.websocket.shared.exception;

/**
 * Custom exception thrown for application errors
 */
public class CustomException extends Exception {

    /**
     * Constructs a new runtime exception with {@code null} as its detail message.
     */
    public CustomException() {
        super();
    }

    /**
     * Constructs a new runtime exception with the specified detail message.
     *
     * @param errorMessage the detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     */
    public CustomException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * Constructs a new runtime exception with the specified detail message and cause.
     *
     * @param errorMessage the detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     * @param err          cause the cause (which is saved for later retrieval by the  {@link #getCause()} method).
     */
    public CustomException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

}
