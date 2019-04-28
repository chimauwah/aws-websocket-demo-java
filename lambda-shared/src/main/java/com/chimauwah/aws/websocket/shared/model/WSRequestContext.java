package com.chimauwah.aws.websocket.shared.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * Custom object for client request context
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class WSRequestContext {
    /**
     * A specified property indicating which integration should be triggered.
     */
    @NotNull
    private String routeKey;
    /**
     * The type of WebSocket request from the client.
     */
    @NotNull
    private WSEventType eventType;
    /**
     * The snapshot of the deployed WebSocket API available for client apps to call.
     */
    @NotNull
    private String stage;
    /**
     * The URI of the WebSocket API.
     */
    @NotNull
    private String domainName;
    /**
     * Unique identifier allocated when a client is successfully connected through WebSocket API.
     * It Is persisted throughout the lifetime of the connection.
     */
    @NotNull
    private String connectionId;
}
