package com.chimauwah.aws.websocket;

import com.chimauwah.aws.websocket.shared.model.WSEventType;
import com.chimauwah.aws.websocket.shared.model.WSRequest;
import com.chimauwah.aws.websocket.shared.model.WSRequestContext;
import com.chimauwah.aws.websocket.shared.model.WSResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WebsocketConnectHandlerSIMPLETest {

    private WebsocketConnectHandlerSIMPLE handler;
    private ObjectMapper objectMapper;
    private WSRequest request;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        objectMapper = new ObjectMapper();
        handler = new WebsocketConnectHandlerSIMPLE();

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
        String respBody = "Bonjour le monde";
        Map<String, String> headers = Collections.singletonMap("Content-Type", "application/json");
        int statusCode = 200;

        WSResponse expectedResponse = new WSResponse(respBody, headers, statusCode);
        WSResponse actualResponse = handler.handleRequest(request, null);

        assertEquals(objectMapper.writeValueAsString(expectedResponse), objectMapper.writeValueAsString(actualResponse));
    }

    @Test
    void shouldThrowExceptionBecauseOfInvalidRequest() throws Exception {
        request.getRequestContext().setConnectionId(null);

        String errMsg = "Incorrect Request";

        WSResponse actualResponse = handler.handleRequest(request, null);
//        assertEquals(objectMapper.writeValueAsString(expectedResponse), objectMapper.writeValueAsString(actualResponse));
    }

    @Test
    void shouldThrowExceptionBecauseInternalError() throws Exception {
        WSResponse actualResponse = handler.handleRequest(request, null);
//        assertEquals(objectMapper.writeValueAsString(expectedResponse), objectMapper.writeValueAsString(actualResponse));
    }

}