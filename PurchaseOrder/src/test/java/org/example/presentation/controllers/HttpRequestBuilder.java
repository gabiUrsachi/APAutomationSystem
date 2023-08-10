package org.example.presentation.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.presentation.view.OrderRequestDTO;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

public class HttpRequestBuilder {
    private static final String ORDER_API_URL = "/api/orders";

    private static final ObjectMapper mapper = new ObjectMapper();

    public static RequestBuilder createPostRequest(OrderRequestDTO orderRequestDTO) throws JsonProcessingException {
        return MockMvcRequestBuilders
                .post(ORDER_API_URL)
                .content(mapper.writeValueAsString(orderRequestDTO))
                .contentType(MediaType.APPLICATION_JSON);
    }

    public static RequestBuilder createPutRequest(UUID orderIdentifier, OrderRequestDTO orderRequestDTO) throws JsonProcessingException {
        return MockMvcRequestBuilders
                .put(ORDER_API_URL + "/{identifier}", orderIdentifier)
                .content(mapper.writeValueAsString(orderRequestDTO))
                .contentType(MediaType.APPLICATION_JSON);
    }

    public static RequestBuilder createDeleteRequest(UUID orderIdentifier) {
        return MockMvcRequestBuilders
                .delete(ORDER_API_URL + "/{identifier}", orderIdentifier);
    }

    public static RequestBuilder createGetRequest(UUID orderIdentifier) {
        return MockMvcRequestBuilders
                .get(ORDER_API_URL + "/{identifier}", orderIdentifier);
    }
}
