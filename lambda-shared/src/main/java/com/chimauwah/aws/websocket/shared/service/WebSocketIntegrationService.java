package com.chimauwah.aws.websocket.shared.service;

import com.chimauwah.aws.websocket.shared.dao.ConnectionsDao;
import com.chimauwah.aws.websocket.shared.exception.CustomException;
import com.chimauwah.aws.websocket.shared.logging.CustomLoggerFactory;
import com.chimauwah.aws.websocket.shared.model.WSMessage;
import com.chimauwah.aws.websocket.shared.model.WSRequest;
import com.chimauwah.aws.websocket.shared.model.WSRequestContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

/**
 * Service for handling client and server side communications with API Gateway WebSocket API
 */
public class WebSocketIntegrationService {

    private static final Logger LOGGER = CustomLoggerFactory.getLogger(WebSocketIntegrationService.class);

    private static final String WEBSOCKET_CONNECTION_URL_KEY = "WEBSOCKET_CONNECTION_URL";

    private WebSocketApiClient apiClient;
    private ConnectionsDao connectionsDao;
    private ObjectMapper objectMapper;

    private String wsConnectionUrl;

    /**
     * Creates a {@link WebSocketIntegrationService} and initializes dependencies.
     */
    public WebSocketIntegrationService() {
        this.apiClient = new WebSocketApiClient();
        this.connectionsDao = new ConnectionsDao();
        this.objectMapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT);

        wsConnectionUrl = System.getenv(WEBSOCKET_CONNECTION_URL_KEY);
    }

    /**
     * Testing constructor
     *
     * @param apiClient the websocket api client mock
     */
    WebSocketIntegrationService(WebSocketApiClient apiClient) {
        this.apiClient = apiClient;
        this.objectMapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * Should be called after a new web socket connection is opened.
     * Stores the connection id
     *
     * @param clientRequest request object from the client that connected to web socket server.
     */
    public void onConnect(WSRequest clientRequest) {
        String connectionId = clientRequest.getRequestContext().getConnectionId();
        try {
            connectionsDao.insert(connectionId);
            LOGGER.info(String.format("[%s] successfully connected and stored in database.", connectionId));
        } catch (Exception e) {
            String error = String.format("Error storing connection id [%s]: %s", connectionId, e.getMessage());
            LOGGER.error(error);
        }
    }

    /**
     * Should be called when a websocket connection is closed.
     * Deletes the connection id
     *
     * @param clientRequest request object from the client that disconnected from web socket server.
     */
    public void onDisconnect(WSRequest clientRequest) {
        String connectionId = clientRequest.getRequestContext().getConnectionId();
        try {
            connectionsDao.delete(connectionId);
            LOGGER.info(String.format("[%s] successfully disconnected and deleted from database.", connectionId));
        } catch (Exception e) {
            String error = String.format("Error deleting connection id [%s]: %s", connectionId, e.getMessage());
            LOGGER.error(error);
        }
    }

    /**
     * Should be called when an error occurred that is not modeled in the web socket protocol.
     * Pushes message to client with error message.
     *
     * @param clientRequest request object from the client in use when the error occurred.
     * @param throwable     throwable representing the problem.
     */
    public void onError(WSRequest clientRequest, Throwable throwable) {
        WSRequestContext requestContext = clientRequest.getRequestContext();
        String errorMessage = String.format("Message not sent to destination: %s",
                throwable.getMessage());
        HashMap<String, Object> meta = new HashMap<>();
        meta.put("connectionId", requestContext.getConnectionId());
        WSMessage message = WSMessage.builder()
                .action("error")
                .message(errorMessage)
                .meta(meta)
                .build();
        LOGGER.error(errorMessage);
        try {
            // push message to connected client that an error occurred because of unknown action
            apiClient.pushMessageToClient(wsConnectionUrl, requestContext.getConnectionId(), message, objectMapper);
        } catch (CustomException e) {
            LOGGER.fatal(e.getMessage());
        }
    }

    /**
     * Should be called when a message that matches a defined route key associated with this integration Lambda is
     * sent from connected client to the WebSocket API.
     * Just echoes message back to the client.
     *
     * @param clientRequest request object from the client that sent the message.
     */
    public void onMessage(WSRequest clientRequest) {
        WSRequestContext requestContext = clientRequest.getRequestContext();
        HashMap<String, Object> meta = new HashMap<>();
        meta.put("connectionId", requestContext.getConnectionId());
        WSMessage message = WSMessage.builder()
                .action("echo")
                .message(clientRequest.getBody())
                .meta(meta)
                .build();
        LOGGER.info("Message successfully received.");
        try {
            // echoes received message back to connected client
            apiClient.pushMessageToClient(wsConnectionUrl, requestContext.getConnectionId(), message, objectMapper);
        } catch (CustomException e) {
            LOGGER.fatal(e.getMessage());
        }
    }

}