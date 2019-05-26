package com.chimauwah.aws.websocket;

import com.chimauwah.aws.websocket.shared.model.WSEventType;
import com.chimauwah.aws.websocket.shared.model.WSRequest;
import com.chimauwah.aws.websocket.shared.model.WSRequestContext;
import com.chimauwah.aws.websocket.shared.model.WSResponse;
import com.chimauwah.aws.websocket.shared.service.WebSocketIntegrationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;

class WebsocketConnectHandlerTest {

    private final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    private WebsocketConnectHandler handler;
    private ObjectMapper objectMapper;
    private WSRequest request;
    @Mock
    private WebSocketIntegrationService service;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        environmentVariables.set("PROFILE", "local");
        objectMapper = new ObjectMapper();
        handler = new WebsocketConnectHandler();
        handler.setWebSocketService(service);

        WSRequestContext requestContext = WSRequestContext.builder()
                .connectionId("testConnectionId")
                .domainName("demo.execute-api.eu.west-3.amazonaws.com")
                .eventType(WSEventType.CONNECT)
                .routeKey("$connect")
                .stage("demo")
                .build();

        request = WSRequest.builder()
                .requestContext(requestContext)
                .build();
    }

    @Test
    void shouldReturnSuccessResponse() throws Exception {
        WSResponse expectedResponse = new WSResponse(objectMapper.writeValueAsString(Map.entry("success", true)),
                Collections.singletonMap("Content-Type", "application/json"), 200);
        WSResponse actualResponse = handler.handleRequest(request, null);
        assertEquals(objectMapper.writeValueAsString(expectedResponse), objectMapper.writeValueAsString(actualResponse));
    }

    @Test
    void shouldThrowExceptionBecauseOfInvalidRequest() throws Exception {
        request.getRequestContext().setConnectionId(null);
        WSResponse expectedResponse = new WSResponse("Invalid Request. connectionId must not be null",
                Collections.singletonMap("Content-Type", "application/json"), 400);
        WSResponse actualResponse = handler.handleRequest(request, null);
        assertEquals(objectMapper.writeValueAsString(expectedResponse), objectMapper.writeValueAsString(actualResponse));
    }

    @Test
    void shouldThrowExceptionBecauseInternalError() throws Exception {
        doAnswer(invocation -> {
            throw new Exception("test server error.");
        }).when(service).onConnect(any());
        WSResponse expectedResponse = new WSResponse("test server error.",
                Collections.singletonMap("Content-Type", "application/json"), 500);
        WSResponse actualResponse = handler.handleRequest(request, null);
        assertEquals(objectMapper.writeValueAsString(expectedResponse), objectMapper.writeValueAsString(actualResponse));
    }
}