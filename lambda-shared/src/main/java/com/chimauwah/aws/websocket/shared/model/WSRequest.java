package com.chimauwah.aws.websocket.shared.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * Custom object for client requests to websocket connection
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Valid
public class WSRequest {
    /**
     * General headers sent in the client request
     */
    private Map<String, String> headers;
    /**
     * Custom object containing the client request context
     */
    @NotNull
    private WSRequestContext requestContext;
    /**
     * Query string parameters in the client request
     */
    private Map<String, String> queryStringParameters;
    /**
     * Message body sent in the client request
     */
    private String body;
}
