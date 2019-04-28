package com.chimauwah.aws.websocket.shared.model;

/**
 * ENUM for the different supported HTTP status codes
 */
public final class HttpStatusCode {

    // Success
    public static final int OK = 200;
    public static final int CREATED = 201;

    // Client Error
    public static final int BAD_REQUEST = 400;
    public static final int NOT_FOUND = 404;
    public static final int GONE = 410;

    // Server Error
    public static final int INTERNAL_SERVER_ERROR = 500;
}
