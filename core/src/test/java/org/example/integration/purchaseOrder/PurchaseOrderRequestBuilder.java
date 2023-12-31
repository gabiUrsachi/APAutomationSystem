package org.example.integration.purchaseOrder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.presentation.view.OrderRequestDTO;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

public class PurchaseOrderRequestBuilder {
    private static final String ORDER_API_URL = "/api/orders";

    private static final ObjectMapper mapper = new ObjectMapper();

    public static RequestBuilder createPostRequest(OrderRequestDTO orderRequestDTO, String... authHeader) throws JsonProcessingException {

        MockMultipartFile order = new MockMultipartFile("order", "", "application/json", mapper.writeValueAsString(orderRequestDTO).getBytes());
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "PDF content".getBytes());

        if (authHeader.length != 0) {

            return MockMvcRequestBuilders
                    .multipart(ORDER_API_URL)
                    .file(order)
                    .file(file)
                    .header("authorization", "Bearer " + authHeader[0]);
        }

        return MockMvcRequestBuilders
                .multipart(ORDER_API_URL)
                .file(order)
                .file(file);
    }

    public static RequestBuilder createPutRequest(UUID orderIdentifier, OrderRequestDTO orderRequestDTO, String... authHeader) throws JsonProcessingException {
        if (authHeader.length != 0) {
            return MockMvcRequestBuilders
                    .put(ORDER_API_URL + "/{identifier}", orderIdentifier)
                    .content(mapper.writeValueAsString(orderRequestDTO))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("authorization", "Bearer " + authHeader[0]);
        }

        return MockMvcRequestBuilders
                .put(ORDER_API_URL + "/{identifier}", orderIdentifier)
                .content(mapper.writeValueAsString(orderRequestDTO))
                .contentType(MediaType.APPLICATION_JSON);
    }

    public static RequestBuilder createDeleteRequest(UUID orderIdentifier, String... authHeader) {
        if (authHeader.length != 0) {
            return MockMvcRequestBuilders
                    .delete(ORDER_API_URL + "/{identifier}", orderIdentifier)
                    .header("authorization", "Bearer " + authHeader[0]);
        }

        return MockMvcRequestBuilders
                .delete(ORDER_API_URL + "/{identifier}", orderIdentifier);
    }

    public static RequestBuilder createGetRequest(UUID orderIdentifier, String... authHeader) {
        if (authHeader.length != 0) {

            return MockMvcRequestBuilders
                    .get(ORDER_API_URL + "/{identifier}", orderIdentifier)
                    .header("authorization", "Bearer " + authHeader[0]);
        }

        return MockMvcRequestBuilders
                .get(ORDER_API_URL + "/{identifier}", orderIdentifier);
    }
}
