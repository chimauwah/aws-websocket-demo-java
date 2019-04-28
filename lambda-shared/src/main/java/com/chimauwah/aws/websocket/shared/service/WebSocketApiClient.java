package com.chimauwah.aws.websocket.shared.service;

import com.chimauwah.aws.websocket.shared.exception.CustomException;
import com.chimauwah.aws.websocket.shared.exception.CustomNotFoundException;
import com.chimauwah.aws.websocket.shared.model.WSMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.apigatewaymanagementapi.ApiGatewayManagementApiClient;
import software.amazon.awssdk.services.apigatewaymanagementapi.model.ApiGatewayManagementApiException;
import software.amazon.awssdk.services.apigatewaymanagementapi.model.PostToConnectionRequest;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * Service client for accessing WebSocket API deployed in Amazon API Gateway
 *
 * <p>
 * This client allows you to push a message to connected client via the WebSocket API using the callback URL. It also
 * allows you to retrieve an instance of jedis client to access the redis cache where the websocket connection context
 * is stored.
 * </p>
 */
public class WebSocketApiClient {

    /**
     * Format string for WebSocket API callback URL
     */
    private static final String CALLBACK_URL_FORMAT = "https://%s/%s/";

    /**
     * HTTP response status code indicating connection is no longer available.
     */
    private static final int STATUS_CODE_GONE = 410;

    /**
     * Format string for error message if failed to push message
     */
    private static final String ERROR_MESSAGE_FORMAT = "Failed to push message to connected client [%s]: %s";

    /**
     * Json array name of cache values
     */
    public static final String CONNECTIONS_CACHE_KEY = "connections";

    /**
     * Pushes a message to the connected client using the provided params.
     *
     * @param domainName   URI of the WebSocket API
     * @param stage        logical reference to where the WebSocket API is deployed
     * @param connectionId connectionId of client to push message
     * @param message      message to push to client
     * @param objectMapper object mapper to use for converting message to string
     * @throws CustomException thrown if error occurred pushing message to client
     */
    public void pushMessageToClient(String domainName, String stage, String
            connectionId, WSMessage message, ObjectMapper objectMapper) throws CustomException {
        String callbackUrl = buildCallbackUrl(domainName, stage);
        pushMessageToClient(callbackUrl, connectionId, message, objectMapper);
    }

    /**
     * Pushes a message to the connected client using the provided callback url and connection id.
     *
     * @param callbackUrl  endpoint to use to push messages to the client from backend system
     * @param connectionId connectionId of client to push message
     * @param message      message to push to client
     * @param objectMapper object mapper to use for converting message to string
     * @throws CustomException thrown if error occurred pushing message to client. check the cause of the exception
     *                         as it may be because the connection no longer exists
     */
    public void pushMessageToClient(String callbackUrl, String connectionId, WSMessage message,
                                    ObjectMapper objectMapper) throws CustomException {
        try {
            ByteBuffer data = ByteBuffer.wrap(objectMapper.writeValueAsString(message)
                    .getBytes(Charset.defaultCharset()));

            PostToConnectionRequest request = PostToConnectionRequest.builder()
                    .connectionId(connectionId)
                    .data(SdkBytes.fromByteBuffer(data))
                    .build();
            getApiClient(callbackUrl).postToConnection(request);
        } catch (ApiGatewayManagementApiException apie) {
            if (STATUS_CODE_GONE == apie.statusCode()) {
                throw new CustomNotFoundException(String.format("Connection ID [%s] not found. " +
                        "Message not sent: " + apie.getMessage(), connectionId));
            }
            throw new CustomException(String.format(ERROR_MESSAGE_FORMAT, connectionId, apie.getMessage()), apie);
        } catch (Exception ex) {
            throw new CustomException(String.format(ERROR_MESSAGE_FORMAT, connectionId, ex.getMessage()), ex);
        }
    }

    /**
     * Returns service client for accessing AmazonApiGatewayManagementApi.
     *
     * @param callbackUrl endpoint to use to push messages to the client from backend system
     * @return ApiGatewayManagementApiClient
     */
    private ApiGatewayManagementApiClient getApiClient(String callbackUrl) {
        return ApiGatewayManagementApiClient.builder()
                .endpointOverride(URI.create(callbackUrl))
                .build();
    }

    /**
     * Dynamically builds the callback URL used to push messages to the client from the backend system.
     *
     * @param domainName URI of the WebSocket API
     * @param stage      logical reference to where the WebSocket API is deployed
     * @return endpoint of deployed API
     */
    private String buildCallbackUrl(String domainName, String stage) {
        return String.format(CALLBACK_URL_FORMAT, domainName, stage);
    }

}
