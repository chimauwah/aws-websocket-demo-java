package com.chimauwah.aws.websocket.shared.model;

import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Wraps the response sent back to the WebSocket API
 */
@Getter
//@JsonAutoDetect
public class WSResponse {

    private final String body;
    private final Map<String, String> headers;
    private final int statusCode;

    /**
     * Creates a WSResponse object.
     *
     * @param responseBody       body of the response
     * @param responseHeaders    headers of the response
     * @param responseStatusCode status code of the response
     */
    public WSResponse(final String responseBody, final Map<String, String> responseHeaders,
                      final int responseStatusCode) {
        this.body = responseBody;
        this.headers = Collections.unmodifiableMap(new HashMap<>(responseHeaders));
        this.statusCode = responseStatusCode;
    }
}