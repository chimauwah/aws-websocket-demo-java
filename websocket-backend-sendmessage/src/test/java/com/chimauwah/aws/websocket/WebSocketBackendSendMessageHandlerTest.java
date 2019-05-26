package com.chimauwah.aws.websocket;


import com.chimauwah.aws.websocket.shared.datasource.DatasourceResource;
import com.chimauwah.aws.websocket.shared.exception.CustomException;
import com.chimauwah.aws.websocket.shared.exception.CustomNotFoundException;
import com.chimauwah.aws.websocket.shared.model.WSEventType;
import com.chimauwah.aws.websocket.shared.model.WSRequest;
import com.chimauwah.aws.websocket.shared.model.WSRequestContext;
import com.chimauwah.aws.websocket.shared.model.WSResponse;
import com.chimauwah.aws.websocket.shared.service.WebSocketApiClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class WebSocketBackendSendMessageHandlerTest {

    private final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    private WebSocketBackendSendMessageHandler handler;
    private ObjectMapper objectMapper;
    private WSRequest request;
    @Mock
    private DatasourceResource datasourceResource;
    @Mock
    private WebSocketApiClient webSocketApiClient;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        environmentVariables.set("PROFILE", "local");
        objectMapper = new ObjectMapper();
        handler = new WebSocketBackendSendMessageHandler();
        handler.setWsConnectionUrl("https://ws-api.com/test/@connections");
        handler.setWebSocketApiClient(webSocketApiClient);
        handler.setDatasource(datasourceResource);

        Set<String> connectionIds = Set.of("123", "456");
        when(datasourceResource.getAll()).thenReturn(connectionIds);

        WSRequestContext requestContext = WSRequestContext.builder()
                .connectionId("testConnectId")
                .domainName("demo.execute-api.eu.west-3.amazonaws.com")
                .eventType(WSEventType.MESSAGE)
                .routeKey("sendMessage")
                .stage("demo")
                .build();

        request = WSRequest.builder()
                .requestContext(requestContext)
                .body("{\"action\":\"sendMessage\",\"message\":\"testing is awesome!!\"}")
                .build();
    }

    @Test
    void shouldReturnSuccess() throws Exception {
        WSResponse expectedResponse = new WSResponse(objectMapper.writeValueAsString(Map.entry("success", true)),
                Collections.singletonMap("Content-Type", "application/json"), 200);
        WSResponse actualResponse = handler.handleRequest(request, null);
        assertEquals(objectMapper.writeValueAsString(expectedResponse), objectMapper.writeValueAsString(actualResponse));
    }

    @Test
    void shouldReturnError() throws Exception {
        doThrow(new CustomException("Error message."))
                .when(webSocketApiClient).pushMessageToClient(any(), any(), any(), any());
        WSResponse expectedResponse = new WSResponse("Error message.",
                Collections.singletonMap("Content-Type", "application/json"), 500);
        WSResponse actualResponse = handler.handleRequest(request, null);
        assertEquals(objectMapper.writeValueAsString(expectedResponse), objectMapper.writeValueAsString(actualResponse));
    }

    @Test
    void shouldReturnSuccessWhenConnectionNoLongerExists() throws Exception {
        doThrow(new CustomNotFoundException("Not found."))
                .when(webSocketApiClient).pushMessageToClient(any(), any(), any(), any());
        WSResponse expectedResponse = new WSResponse(objectMapper.writeValueAsString(Map.entry("success", true)),
                Collections.singletonMap("Content-Type", "application/json"), 200);
        WSResponse actualResponse = handler.handleRequest(request, null);
        assertEquals(objectMapper.writeValueAsString(expectedResponse), objectMapper.writeValueAsString(actualResponse));
    }

}