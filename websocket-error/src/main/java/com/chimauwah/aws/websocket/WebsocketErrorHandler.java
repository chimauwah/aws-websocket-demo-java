package com.chimauwah.aws.websocket;

import com.amazonaws.services.lambda.runtime.Context;
import com.chimauwah.aws.websocket.shared.exception.CustomException;
import com.chimauwah.aws.websocket.shared.exception.CustomInvalidRequestException;
import com.chimauwah.aws.websocket.shared.handler.CustomHandler;
import com.chimauwah.aws.websocket.shared.logging.CustomLoggerFactory;
import com.chimauwah.aws.websocket.shared.model.HttpStatusCode;
import com.chimauwah.aws.websocket.shared.model.WSRequest;
import com.chimauwah.aws.websocket.shared.model.WSResponse;
import com.chimauwah.aws.websocket.shared.service.WebSocketIntegrationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import org.apache.logging.log4j.Logger;

/**
 * Handles when the selected route key does not match any of the known route keys.
 */
public class WebsocketErrorHandler extends CustomHandler<WSRequest> {

    private static final Logger LOGGER = CustomLoggerFactory.getLogger(WebsocketErrorHandler.class);
    private static final String ERROR_MESSAGE = "Bad request body. Route selection expression does not match any of " +
            "the known route keys.";

    @Setter
    private WebSocketIntegrationService webSocketService;
    private ObjectMapper objectMapper;

    /**
     * Creates {@link WebsocketErrorHandler} and initializes
     */
    public WebsocketErrorHandler() {
        this.webSocketService = new WebSocketIntegrationService();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public WSResponse handleRequest(WSRequest request, Context context) {
        try {
            LOGGER.info("Input Request: " + objectMapper.writeValueAsString(request));
            validateRequest(request);
            webSocketService.onError(request, new CustomException(ERROR_MESSAGE));
            return successResponse(objectMapper.writeValueAsString(getSuccessResponseAsMap(true)), HttpStatusCode.OK);
        } catch (CustomInvalidRequestException cire) {
            return failureResponse("Invalid Request. " + cire.getMessage(), HttpStatusCode.BAD_REQUEST);
        } catch (Exception ex) {
            LOGGER.error("Error occurred: " + ex.getMessage());
            return failureResponse(ex.getMessage(), HttpStatusCode.INTERNAL_SERVER_ERROR);
        }
    }

}
