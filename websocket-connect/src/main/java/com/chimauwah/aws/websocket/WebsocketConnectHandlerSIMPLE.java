package com.chimauwah.aws.websocket;

import com.amazonaws.services.lambda.runtime.Context;
import com.chimauwah.aws.websocket.shared.handler.CustomHandler;
import com.chimauwah.aws.websocket.shared.model.WSRequest;
import com.chimauwah.aws.websocket.shared.model.WSRequestContext;
import com.chimauwah.aws.websocket.shared.model.WSResponse;

/**
 * Handles when a client first connects to WebSocket API. Connection is not established until this handler completes.
 */
public class WebsocketConnectHandlerSIMPLE extends CustomHandler<WSRequest> {

    /**
     * Creates a {@link WebsocketConnectHandlerSIMPLE}
     */
    public WebsocketConnectHandlerSIMPLE() {
    }

    @Override
    public WSResponse handleRequest(WSRequest request, Context context) {
        WSRequestContext requestContext = request.getRequestContext();
//        if (!isValid(requestContext)) {
//            return new WSResponse("Désolé", APPLICATION_JSON,
//                    400);
//        }
//        String connectionId = requestContext.getConnectionId();
//        // TODO: store connection id in database table
        return new WSResponse("Bonjour le monde", APPLICATION_JSON,
                200);
    }

}