package com.chimauwah.aws.websocket;

import com.amazonaws.services.lambda.runtime.Context;
import com.chimauwah.aws.websocket.shared.dao.ConnectionsDao;
import com.chimauwah.aws.websocket.shared.exception.CustomNotFoundException;
import com.chimauwah.aws.websocket.shared.handler.CustomHandler;
import com.chimauwah.aws.websocket.shared.logging.CustomLoggerFactory;
import com.chimauwah.aws.websocket.shared.model.HttpStatusCode;
import com.chimauwah.aws.websocket.shared.model.WSMessage;
import com.chimauwah.aws.websocket.shared.model.WSResponse;
import com.chimauwah.aws.websocket.shared.service.WebSocketApiClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Sample handler that pushes a message to a connected client.
 * Can be triggered by an SQS, API, or other AWS event. Can also push message based on business logic code.
 */
public class WebSocketBackendSendMessageHandler extends CustomHandler<Object> {

    private static final Logger LOGGER = CustomLoggerFactory.getLogger(WebSocketBackendSendMessageHandler.class);

    private static final String WEBSOCKET_CONNECTION_URL_KEY = "WEBSOCKET_CONNECTION_URL";

    @Setter
    private WebSocketApiClient webSocketApiClient;
    private ConnectionsDao connectionsDao;
    private ObjectMapper objectMapper;

    private String wsConnectionUrl;

    /**
     * Initializes {@link WebSocketBackendSendMessageHandler}, {@link WebSocketApiClient}, Jedis and object mapper
     */
    public WebSocketBackendSendMessageHandler() {
        this.webSocketApiClient = new WebSocketApiClient();
        this.connectionsDao = new ConnectionsDao();
        this.objectMapper = new ObjectMapper();

        wsConnectionUrl = System.getenv(WEBSOCKET_CONNECTION_URL_KEY);
    }

    /**
     * For testing with mocks
     *
     * @param webSocketApiClient mock service for websocket
     * @param objectMapper       mock object mapper
     */
    public WebSocketBackendSendMessageHandler(WebSocketApiClient webSocketApiClient,
                                              ObjectMapper objectMapper) {
        this.webSocketApiClient = webSocketApiClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public WSResponse handleRequest(Object request, Context context) {
        try {
            LOGGER.info("Input Request: " + objectMapper.writeValueAsString(request));

            // retrieve all connection ids in db
            List<String> connectionIds = connectionsDao.getAll();

            // an action understood by client to perform
            String action = "RELOAD_DATA";
            // message body to send to client
            String body = "nothing really to say";

            // create message to send
            WSMessage message = WSMessage.builder()
                    .action(action)
                    .message(body)
                    .build();

            // send message to all connected clients
            for (String connectionId : connectionIds) {
                try {
                    webSocketApiClient.pushMessageToClient(wsConnectionUrl,
                            connectionId, message, objectMapper);
                    LOGGER.info(String.format("Message sent to connection [%s]", connectionId));
                } catch (CustomNotFoundException cnfe) {
                    LOGGER.info(String.format("Connection [%s] no longer exists: %s ", connectionId, cnfe.getMessage()));
                }
            }

            return new WSResponse(objectMapper.writeValueAsString(getSuccessResponseAsMap(true)),
                    APPLICATION_JSON, HttpStatusCode.OK);
        } catch (Exception ex) {
            LOGGER.error("Error occurred: " + ex.getMessage());
            return new WSResponse(ex.getMessage(), APPLICATION_JSON, HttpStatusCode.INTERNAL_SERVER_ERROR);
        }
    }
}