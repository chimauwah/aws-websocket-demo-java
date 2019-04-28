package com.chimauwah.aws.websocket;

import com.amazonaws.services.lambda.runtime.Context;
import com.chimauwah.aws.websocket.shared.model.WSRequest;
import com.chimauwah.aws.websocket.shared.model.WSRequestContext;
import com.chimauwah.aws.websocket.shared.model.WSResponse;

import java.util.Collections;

/**
 * Handles when a client disconnects from WebSocket API. The connection can be closed by the server or by the client.
 * Since the connection is already closed when it is executed, the disconnect event is a best-effort event. API Gateway
 * will try its best to deliver the event to this integration, but it cannot guarantee delivery.
 */
public class WebocketDisconnectHandlerSIMPLE {

    /**
     * Creates a {@link WebocketDisconnectHandlerSIMPLE}
     */
    public WebocketDisconnectHandlerSIMPLE() {
    }

    /**
     * @param request
     * @param context
     * @return
     */
    public WSResponse handleRequest(WSRequest request, Context context) {
        WSRequestContext requestContext = request.getRequestContext();
        String connectionId = requestContext.getConnectionId();
        return new WSResponse("Au revoir le monde",
                Collections.singletonMap("Content-Type", "application/xml"),
                200);
    }

}
