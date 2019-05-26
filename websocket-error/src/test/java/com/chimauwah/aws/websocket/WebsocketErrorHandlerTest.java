package com.chimauwah.aws.websocket;

import com.chimauwah.aws.websocket.shared.exception.CustomException;
import com.chimauwah.aws.websocket.shared.model.HttpStatusCode;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class WebsocketErrorHandlerTest {

    private final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    private WebsocketErrorHandler handler;
    private ObjectMapper objectMapper;
    private WSRequest request;
    @Mock
    private WebSocketIntegrationService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        objectMapper = new ObjectMapper();
        environmentVariables.set("PROFILE", "local");
        handler = new WebsocketErrorHandler();
        handler.setWebSocketService(service);

        WSRequestContext requestContext = WSRequestContext.builder()
                .connectionId("testConnectId")
                .domainName("demo.execute-api.eu.west-3.amazonaws.com")
                .eventType(WSEventType.MESSAGE)
                .routeKey("$default")
                .stage("demo")
                .build();

        request = WSRequest.builder()
                .requestContext(requestContext)
                .build();
    }

    @Test
    void shouldReturnSuccessWithBadRequestBodyMessage() throws Exception {
        WSResponse expectedResponse = new WSResponse(objectMapper.writeValueAsString(Map.entry("success", true)),
                Collections.singletonMap("Content-Type", "application/json"), 200);
        WSResponse actualResponse = handler.handleRequest(request, null);
        verify(service, times(1)).onError(any(WSRequest.class), any(CustomException.class));
        assertEquals(objectMapper.writeValueAsString(expectedResponse),
                objectMapper.writeValueAsString(actualResponse));

    }


    @Test
    void shouldThrowExceptionBecauseOfInvalidRequest() throws Exception {
        request.getRequestContext().setConnectionId(null);
        WSResponse expectedResponse = new WSResponse("Invalid Request. connectionId must not be null",
                Collections.singletonMap("Content-Type", "application/json"), 400);
        WSResponse actualResponse = handler.handleRequest(request, null);
        verify(service, times(0)).onError(any(), any());
        assertEquals(objectMapper.writeValueAsString(expectedResponse), objectMapper.writeValueAsString(actualResponse));
    }

    @Test
    void shouldThrowExceptionBecauseInternalError() throws Exception {
        WSResponse expectedResponse =
                new WSResponse(null, Collections.singletonMap("Content-Type",
                        "application/json"), HttpStatusCode.INTERNAL_SERVER_ERROR);
        doThrow(Exception.class).when(service).onError(any(), any());
        WSResponse actualResponse = handler.handleRequest(request, null);
        verify(service, times(1)).onError(any(), any());
        assertEquals(objectMapper.writeValueAsString(expectedResponse), objectMapper.writeValueAsString(actualResponse));
    }
}