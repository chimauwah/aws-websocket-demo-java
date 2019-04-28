package com.chimauwah.aws.websocket.shared.exception;

/**
 * Custom exception thrown for when request is not valid
 */
public class CustomInvalidRequestException extends CustomException {

    /**
     * Constructs a new exception for 400 status code.
     */
    public CustomInvalidRequestException() {
        super();
    }

    /**
     * Constructs a new exception for 400 status code with the specified detail message.
     *
     * @param errorMessage Exception for 400 status code with errorMessage content
     */
    public CustomInvalidRequestException(String errorMessage) {
        super(errorMessage);
    }
}
