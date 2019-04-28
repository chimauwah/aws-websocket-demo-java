package com.chimauwah.aws.websocket;


import com.chimauwah.aws.websocket.shared.model.HttpStatusCode;
import com.chimauwah.aws.websocket.shared.model.WSEventType;
import com.chimauwah.aws.websocket.shared.model.WSRequest;
import com.chimauwah.aws.websocket.shared.model.WSRequestContext;
import com.chimauwah.aws.websocket.shared.model.WSResponse;
import com.chimauwah.aws.websocket.shared.service.WebSocketIntegrationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class WebsocketErrorHandlerTest {

    private WebsocketErrorHandler handler;
    @Mock
    private WebSocketIntegrationService service;
    @Spy
    private ObjectMapper objectMapper;
    private WSRequest request;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        objectMapper = new ObjectMapper();
        handler = new WebsocketErrorHandler(service, objectMapper);

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
        String message = "Bad request body. Route selection expression does not match any of the known route keys.";
        WSResponse expectedResponse =
                new WSResponse(message, Collections.singletonMap("Content-Type",
                        "application/json"), HttpStatusCode.BAD_REQUEST);
        WSResponse actualResponse = handler.handleRequest(request, null);

        verify(service, times(1)).onError(any(), any());
        assertEquals(objectMapper.writeValueAsString(expectedResponse), objectMapper.writeValueAsString(actualResponse));
    }


    @Test
    void shouldThrowExceptionBecauseOfInvalidRequest() throws Exception {
        request.getRequestContext().setConnectionId(null);

//        String errMsg = "Incorrect Request";
//        ConstraintViolation<?> constraintViolation = ConstraintViolationImpl.forBeanValidation("",
//                new HashMap<>(), new HashMap<>(), "must not be null", WSRequestContext.class,
//                request.getRequestContext(), request.getRequestContext(), null,
//                PathImpl.createPathFromString("connectionId"), null,
//                ElementType.FIELD, null);
//        Set<ConstraintViolation<?>> errors = ImmutableSet.of(constraintViolation);
//        @SuppressWarnings("unchecked") WSResponse expectedResponse =
//                new WSResponse(objectMapper.writeValueAsString(new ErrorMessage(errMsg, errors)),
//                        Collections.singletonMap("Content-Type", "application/json"), HttpStatusCode.SC_BAD_REQUEST);
//        WSResponse actualResponse = handler.handleRequest(request, null);
//        verify(service, times(0)).onError(any(), any());
//        assertEquals(objectMapper.writeValueAsString(expectedResponse), objectMapper.writeValueAsString(actualResponse));
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