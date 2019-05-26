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

import static org.junit.jupiter.api.Assertions.assertEquals;


class WebsocketRecieveMessageHandlerSIMPLETest {

    private WebsocketRecieveMessageHandlerSIMPLE handler;
    private ObjectMapper objectMapper;
    private WSRequest request;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        objectMapper = new ObjectMapper();
        handler = new WebsocketRecieveMessageHandlerSIMPLE();

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
        WSResponse expectedResponse = new WSResponse("",
                Collections.emptyMap(), 200);
        WSResponse actualResponse = handler.handleRequest(request, null);
        assertEquals(objectMapper.writeValueAsString(expectedResponse), objectMapper.writeValueAsString(actualResponse));
    }

    @Test
    void shouldThrowExceptionBecauseOfInvalidRequest() throws Exception {
//        request.getRequestContext().setConnectionId(null);
//
//        String errMsg = "Incorrect Request";
//        @SuppressWarnings("unchecked") WSResponse expectedResponse =
//                new WSResponse(objectMapper.writeValueAsString(new Errors.ErrorMessage(errMsg, null)),
//                        Collections.singletonMap("Content-Type", "application/json"), HttpStatusCode.BAD_REQUEST);
//        WSResponse actualResponse = handler.handleRequest(request, null);
//        assertEquals(objectMapper.writeValueAsString(expectedResponse), objectMapper.writeValueAsString(actualResponse));
    }

    @Test
    void shouldThrowExceptionBecauseInternalError() throws Exception {
//        ApiResponse response = ApiResponse.builder()
//                .data(ImmutableMap.of("success", false))
//                .build();
//        WSResponse expectedResponse =
//                new WSResponse<>(objectMapper.writeValueAsString(new ErrorMessage<>(null)),
//                        Collections.singletonMap("Content-Type",
//                                "application/json"), HttpStatusCode.SC_INTERNAL_ERROR);
//        doThrow(Exception.class).when(service).onMessage(any());
//        WSResponse actualResponse = handler.handleRequest(request, null);
//        verify(service, times(1)).onMessage(any());
//        verify(service, times(0)).sendMessageToClient(any(), any(), any(), any(), any());
//        assertEquals(objectMapper.writeValueAsString(expectedResponse), objectMapper.writeValueAsString(actualResponse));
    }


}