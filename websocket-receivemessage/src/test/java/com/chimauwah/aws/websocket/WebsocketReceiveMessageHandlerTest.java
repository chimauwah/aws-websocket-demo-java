package com.chimauwah.aws.websocket;

import com.chimauwah.aws.websocket.shared.model.HttpStatusCode;
import com.chimauwah.aws.websocket.shared.model.WSEventType;
import com.chimauwah.aws.websocket.shared.model.WSRequest;
import com.chimauwah.aws.websocket.shared.model.WSRequestContext;
import com.chimauwah.aws.websocket.shared.model.WSResponse;
import com.chimauwah.aws.websocket.shared.service.WebSocketIntegrationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.validation.ConstraintViolation;
import java.lang.annotation.ElementType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;

class WebsocketReceiveMessageHandlerTest {

    private final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    private WebsocketReceiveMessageHandler handler;
    private ObjectMapper objectMapper;
    private WSRequest request;
    @Mock
    private WebSocketIntegrationService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        environmentVariables.set("PROFILE", "local");
        objectMapper = new ObjectMapper();
        handler = new WebsocketReceiveMessageHandler();
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
    void shouldReturnSuccess() throws Exception {
        WSResponse expectedResponse = new WSResponse(objectMapper.writeValueAsString(Map.entry("success", true)),
                Collections.singletonMap("Content-Type", "application/json"), 200);
        WSResponse actualResponse = handler.handleRequest(request, null);
        assertEquals(objectMapper.writeValueAsString(expectedResponse),
                objectMapper.writeValueAsString(actualResponse));
    }

    @Test
    void shouldThrowExceptionBecauseOfInvalidRequest() throws Exception {
        request.getRequestContext().setConnectionId(null);

        String errMsg = "Invalid Request. connectionId must not be null";
        ConstraintViolation<?> constraintViolation = ConstraintViolationImpl.forBeanValidation("",
                new HashMap<>(), new HashMap<>(), "must not be null", WSRequestContext.class,
                request.getRequestContext(), request.getRequestContext(), null,
                PathImpl.createPathFromString("connectionId"), null,
                ElementType.FIELD, null);
        Set<ConstraintViolation<?>> errors = Set.of(constraintViolation);
        @SuppressWarnings("unchecked") WSResponse expectedResponse =
                new WSResponse(errMsg,
                        Collections.singletonMap("Content-Type", "application/json"), HttpStatusCode.BAD_REQUEST);
        WSResponse actualResponse = handler.handleRequest(request, null);
        assertEquals(objectMapper.writeValueAsString(expectedResponse), objectMapper.writeValueAsString(actualResponse));
    }

    @Test
    void shouldThrowExceptionBecauseInternalError() throws Exception {
        WSResponse expectedResponse =
                new WSResponse("test server error.", Collections.singletonMap("Content-Type",
                        "application/json"), HttpStatusCode.INTERNAL_SERVER_ERROR);
        doAnswer(invocation -> {
            throw new Exception("test server error.");
        }).when(service).onMessage(any());
        WSResponse actualResponse = handler.handleRequest(request, null);
        assertEquals(objectMapper.writeValueAsString(expectedResponse), objectMapper.writeValueAsString(actualResponse));
    }

}