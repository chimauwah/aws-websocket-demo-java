package com.chimauwah.aws.websocket;

import com.amazonaws.services.lambda.runtime.Context;
import com.chimauwah.aws.websocket.shared.exception.CustomInvalidRequestException;
import com.chimauwah.aws.websocket.shared.handler.CustomHandler;
import com.chimauwah.aws.websocket.shared.logging.CustomLoggerFactory;
import com.chimauwah.aws.websocket.shared.model.HttpStatusCode;
import com.chimauwah.aws.websocket.shared.model.WSRequest;
import com.chimauwah.aws.websocket.shared.model.WSResponse;
import com.chimauwah.aws.websocket.shared.service.WebSocketIntegrationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.Setter;
import org.apache.logging.log4j.Logger;

/**
 * Sample handler that receives a message from a connected client.
 */
public class WebsocketReceiveMessageHandler extends CustomHandler<WSRequest> {

    private static final Logger LOGGER = CustomLoggerFactory.getLogger(WebsocketReceiveMessageHandler.class);

    @Setter
    private WebSocketIntegrationService webSocketService;
    private ObjectMapper objectMapper;

    /**
     * Creates a {@link WebsocketReceiveMessageHandler} and initializes aa object mapper
     */
    public WebsocketReceiveMessageHandler() {
        this.webSocketService = new WebSocketIntegrationService();
        this.objectMapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * For testing with mocks
     *
     * @param webSocketService mock service for websocket
     * @param objectMapper     mock object mapper
     */
    public WebsocketReceiveMessageHandler(WebSocketIntegrationService webSocketService, ObjectMapper objectMapper) {
        this.webSocketService = webSocketService;
        this.objectMapper = objectMapper;
    }

    @Override
    public WSResponse handleRequest(WSRequest request, Context context) {
        try {
            LOGGER.info("Input Request: " + objectMapper.writeValueAsString(request));
            validateRequest(request);
            webSocketService.onMessage(request);
            return successResponse(objectMapper.writeValueAsString(getSuccessResponseAsMap(true)), HttpStatusCode.OK);
        } catch (CustomInvalidRequestException cire) {
            return failureResponse("Invalid Request. " + cire.getMessage(), HttpStatusCode.BAD_REQUEST);
        } catch (Exception ex) {
            LOGGER.error("Error occurred: " + ex.getMessage());
            return failureResponse(ex.getMessage(), HttpStatusCode.INTERNAL_SERVER_ERROR);
        }
    }

}
