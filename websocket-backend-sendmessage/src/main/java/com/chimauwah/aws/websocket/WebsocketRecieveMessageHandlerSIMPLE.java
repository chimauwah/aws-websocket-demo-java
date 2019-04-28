package com.chimauwah.aws.websocket;

import com.amazonaws.services.lambda.runtime.Context;
import com.chimauwah.aws.websocket.shared.handler.CustomHandler;
import com.chimauwah.aws.websocket.shared.model.WSMessage;
import com.chimauwah.aws.websocket.shared.model.WSRequest;
import com.chimauwah.aws.websocket.shared.model.WSRequestContext;
import com.chimauwah.aws.websocket.shared.model.WSResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;

/**
 * Sample handler that receives a message sent from a connected client to the WebSocket API.
 * In order for this handler to be invoked, the sent message must match a defined route key.
 * The message and connection ID is echoed back to the client.
 */
public class WebsocketRecieveMessageHandlerSIMPLE extends CustomHandler<WSRequest> {

    /**
     * Creates a {@link WebsocketRecieveMessageHandlerSIMPLE}
     */
    public WebsocketRecieveMessageHandlerSIMPLE() {
    }

    @Override
    public WSResponse handleRequest(WSRequest request, Context context) {
        WSMessage message = null;
        try {
            message = new ObjectMapper().readValue(request.getBody(), WSMessage.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        WSRequestContext requestContext = request.getRequestContext();
        String connectionId = requestContext.getConnectionId();
        // TODO: handle incoming message from connected client
        return new WSResponse("", new HashMap<>(), 200);
    }

}
