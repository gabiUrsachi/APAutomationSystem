package org.example.integration.invoiceFeatureTests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.business.models.InvoiceDPO;
import org.example.business.models.InvoiceDTO;
import org.example.presentation.view.OrderResponseDTO;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

public class InvoiceRequestBuilder {
    private static final String REQUEST_ROUTE = "/api/invoices";

    private static final ObjectMapper mapper = new ObjectMapper();

    public static RequestBuilder createPostRequest(InvoiceDPO invoiceDPO, String... authHeader) throws JsonProcessingException {
        if (authHeader.length != 0) {
            return MockMvcRequestBuilders
                    .post(REQUEST_ROUTE)
                    .content(mapper.writeValueAsString(invoiceDPO))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("authorization", "Bearer " + authHeader[0]);
        }

        return MockMvcRequestBuilders
                .post(REQUEST_ROUTE)
                .content(mapper.writeValueAsString(invoiceDPO))
                .contentType(MediaType.APPLICATION_JSON);
    }

    public static RequestBuilder createInvoiceFromORRequest(OrderResponseDTO orderResponseDTO, String... authHeader) throws JsonProcessingException {
        if (authHeader.length != 0) {
            return MockMvcRequestBuilders
                    .post(REQUEST_ROUTE + "/fromOR")
                    .content(mapper.writeValueAsString(orderResponseDTO))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("authorization", "Bearer " + authHeader[0]);
        }

        return MockMvcRequestBuilders
                .post(REQUEST_ROUTE + "/fromOR")
                .content(mapper.writeValueAsString(orderResponseDTO))
                .contentType(MediaType.APPLICATION_JSON);
    }
    public static RequestBuilder createPutRequest(UUID invoiceIdentifier, InvoiceDTO invoiceDTO, String... authHeader) throws JsonProcessingException {
        if (authHeader.length != 0) {
            return MockMvcRequestBuilders
                    .put(REQUEST_ROUTE + "/{identifier}", invoiceIdentifier)
                    .content(mapper.writeValueAsString(invoiceDTO))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("authorization", "Bearer " + authHeader[0]);
        }

        return MockMvcRequestBuilders
                .put(REQUEST_ROUTE + "/{identifier}", invoiceIdentifier)
                .content(mapper.writeValueAsString(invoiceDTO))
                .contentType(MediaType.APPLICATION_JSON);
    }

    public static RequestBuilder createDeleteRequest(UUID orderIdentifier, String... authHeader) {
        if (authHeader.length != 0) {
            return MockMvcRequestBuilders
                    .delete(REQUEST_ROUTE + "/{identifier}", orderIdentifier)
                    .header("authorization", "Bearer " + authHeader[0]);
        }

        return MockMvcRequestBuilders
                .delete(REQUEST_ROUTE + "/{identifier}", orderIdentifier);
    }

    public static RequestBuilder createGetRequest(String... authHeader) {
        if (authHeader.length != 0) {

            return MockMvcRequestBuilders
                    .get(REQUEST_ROUTE)
                    .header("authorization", "Bearer " + authHeader[0]);
        }

        return MockMvcRequestBuilders
                .get(REQUEST_ROUTE);
    }

    public static RequestBuilder createIndividualGetRequest(UUID orderIdentifier, String... authHeader) {
        if (authHeader.length != 0) {

            return MockMvcRequestBuilders
                    .get(REQUEST_ROUTE + "/{identifier}", orderIdentifier)
                    .header("authorization", "Bearer " + authHeader[0]);
        }

        return MockMvcRequestBuilders
                .get(REQUEST_ROUTE + "/{identifier}", orderIdentifier);
    }
}
