package com.chimauwah.aws.websocket.shared.exception;

/**
 * Custom exception thrown when requested resource could not be found
 */
public class CustomNotFoundException extends CustomException {

    /**
     * Constructs a new exception for 404 status code.
     */
    public CustomNotFoundException() {
        super();
    }

    /**
     * Constructs a new exception for 404 status code with the specified detail message.
     *
     * @param errorMessage Exception for 404 status code with errorMessage content
     */
    public CustomNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
