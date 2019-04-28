package com.chimauwah.aws.websocket.shared.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.chimauwah.aws.websocket.shared.exception.CustomInvalidRequestException;
import com.chimauwah.aws.websocket.shared.model.WSRequest;
import com.chimauwah.aws.websocket.shared.model.WSRequestContext;
import com.chimauwah.aws.websocket.shared.model.WSResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Abstract class extended by all handler functions. Provides common constants
 * and error message creation functions. Defines handleRequest for all handlers.
 *
 * @param <T> Generic type
 */
public abstract class CustomHandler<T> {
    protected static final Map<String, String> APPLICATION_JSON = Collections.singletonMap("Content-Type",
            "application/json");

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();
    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Handle requests coming into the lambda handlers
     *
     * @param request POJO request object
     * @param context The Lambda execution environment context object.
     * @return WSResponse
     */
    public abstract WSResponse handleRequest(T request,
                                             Context context);

    /**
     * Validates constraints on request
     *
     * @param request request object from client
     * @throws CustomInvalidRequestException thrown if request is invalid
     * @throws JsonProcessingException       thrown if error writing object to string
     */
    protected void validateRequest(WSRequest request) throws CustomInvalidRequestException, JsonProcessingException {
        WSRequestContext requestContext = request.getRequestContext();
        if (!isValid(requestContext)) {
            throw new CustomInvalidRequestException(objectMapper.writeValueAsString(VALIDATOR.validate(requestContext)));
        }
    }


    protected WSResponse successResponse(String responseBody, int statusCode) {
        return new WSResponse(responseBody, APPLICATION_JSON, statusCode);
    }

    protected WSResponse failureResponse(String errorMessage, int statusCode) {
        return new WSResponse(errorMessage, APPLICATION_JSON, statusCode);
    }

    /**
     * Checks if item in event is valid or not
     *
     * @param <U>  possible type other than T
     * @param item generic item
     * @return boolean
     */
    protected <U> boolean isValid(U item) {
        Set<ConstraintViolation<U>> violations = VALIDATOR.validate(item);
        return violations.isEmpty();
    }

    /**
     * Checks if Set of items is valid
     *
     * @param <U>   possible type other than T
     * @param items items to check
     * @return boolean
     */
    protected <U> boolean isValid(Set<U> items) {
        for (U item : items) {
            if (!isValid(item))
                return false;
        }
        return true;
    }

    /**
     * Return success or failure response in a map
     *
     * @param success success or failure
     * @return map of success response
     */
    protected Map getSuccessResponseAsMap(boolean success) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("success", success);
        return response;
    }

}
