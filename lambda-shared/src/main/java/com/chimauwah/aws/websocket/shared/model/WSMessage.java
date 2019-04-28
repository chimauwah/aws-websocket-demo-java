package com.chimauwah.aws.websocket.shared.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * Custom message object for client and server messaging
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class WSMessage {
    /**
     * Specified custom action to be performed by message recipient.
     */
    private String action;
    /**
     * Message sent to recipient.
     */
    private String message;
    /**
     * Map for additional descriptive message data
     */
    private Map<String, Object> meta;
}
