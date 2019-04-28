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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.validation.ConstraintViolation;
import java.lang.annotation.ElementType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class WebsocketReceiveMessageHandlerWTFTest {

    private WebsocketReceiveMessageHandler handler;
    private ObjectMapper objectMapper;
    @Mock
    private WebSocketIntegrationService webSocketIntegrationService;
    private WSRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        objectMapper = new ObjectMapper();
        handler = new WebsocketReceiveMessageHandler(webSocketIntegrationService, objectMapper);

        WSRequestContext requestContext = WSRequestContext.builder()
                .connectionId("testConnectId")
                .domainName("test.execute-api.eu.west-3.amazonaws.com")
                .eventType(WSEventType.MESSAGE)
                .routeKey("sendMessage")
                .stage("test")
                .build();

        request = WSRequest.builder()
                .requestContext(requestContext)
                .body("{\"action\":\"sendMessage\",\"message\":\"testing is awesome!!\"}")
                .build();
    }

    @Test
    void shouldReturnSuccess() throws Exception {
        WSResponse expectedResponse = new WSResponse("error existings",
                Collections.singletonMap("Content-Type",
                        "application/json"), HttpStatusCode.OK);
        WSResponse actualResponse = handler.handleRequest(request, null);

        assertEquals(objectMapper.writeValueAsString(expectedResponse), objectMapper.writeValueAsString(actualResponse));
    }

    @Test
    void shouldThrowExceptionBecauseOfInvalidRequest() throws Exception {
        request.getRequestContext().setConnectionId(null);

        String errMsg = "Incorrect Request";
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
                new WSResponse("error things ",
                        Collections.singletonMap("Content-Type",
                                "application/json"), HttpStatusCode.INTERNAL_SERVER_ERROR);
        WSResponse actualResponse = handler.handleRequest(request, null);
        assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
    }

}