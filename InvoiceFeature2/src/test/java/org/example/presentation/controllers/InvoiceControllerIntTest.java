package org.example.presentation.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.business.exceptions.InvoiceNotFoundException;
import org.example.business.models.CompanyDTO;
import org.example.business.models.InvoiceDPO;
import org.example.business.models.InvoiceDTO;
import org.example.business.models.OrderResponseDTO;
import org.example.business.services.InvoiceMapperService;
import org.example.business.services.InvoiceService;
import org.example.persistence.collections.Invoice;
import org.example.persistence.repository.InvoiceRepository;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class InvoiceControllerIntTest {

    private final String CONTROLLER_REQUIRED_ROUTE = "/api/invoices";
    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void createInvoice() throws Exception {

        InvoiceDPO invoiceDPO = InvoiceDPO.builder()
                .buyerId(UUID.fromString("8be9a53b-997b-4559-8fb7-d120209e63e9"))
                .sellerId(UUID.fromString("8be9a53b-997b-4559-8fb7-d120209e63e2"))
                .build();


        this.mockMvc.perform(post(CONTROLLER_REQUIRED_ROUTE)
                        .content(this.mapper.writeValueAsString(invoiceDPO))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                //de ver obiect nenul
                .andExpect(content().json("{'buyerId':'8be9a53b-997b-4559-8fb7-d120209e63e9'}"));
    }

    @Test
    void getInvoices() throws Exception {
        MvcResult result = this.mockMvc.perform(get(CONTROLLER_REQUIRED_ROUTE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();
        assertNotNull(result.getResponse());
        //inserat un invoice si verificat  ca e prezent in get

    }

    @Test
    void createInvoiceFromPurchaseOrder() throws Exception {

        String routeSuffix = "/fromOR";

        String requestBody = new JSONObject()
                .put("identifier", "4a9d8ff0-bc00-4282-bb18-f9c192d4ee47")
                .put("buyer", new JSONObject()
                        .put("companyIdentifier", "0c37ff0d-6c32-4850-ae01-1ca022b89442")
                        .put("name", "CompanyA"))
                .put("seller", new JSONObject()
                        .put("companyIdentifier", "0c37ff0d-6c32-4850-ae01-1ca022b89443")
                        .put("name", "CompanyB"))
                .toString();

        this.mockMvc.perform(post(CONTROLLER_REQUIRED_ROUTE + routeSuffix)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{'identifier':'4a9d8ff0-bc00-4282-bb18-f9c192d4ee47'}"));
    }


    @Test
    void deleteById() throws Exception {

        InvoiceDPO invoiceDPO = InvoiceDPO.builder()
                .buyerId(UUID.fromString("8be9a53b-997b-4559-8fb7-d120209e63e9"))
                .sellerId(UUID.fromString("8be9a53b-997b-4559-8fb7-d120209e63e2"))
                .build();


        MvcResult result = this.mockMvc.perform(post(CONTROLLER_REQUIRED_ROUTE)
                        .content(this.mapper.writeValueAsString(invoiceDPO))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();


        String jsonResponse = result.getResponse().getContentAsString();
        InvoiceDPO invoiceResponse = new ObjectMapper().readValue(jsonResponse, InvoiceDPO.class);
        this.mockMvc.perform(delete(CONTROLLER_REQUIRED_ROUTE + "/" + invoiceResponse.getIdentifier())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }


    @Test
    void getById() throws Exception {

        InvoiceDPO invoiceDPO = InvoiceDPO.builder()
                .buyerId(UUID.fromString("8be9a53b-997b-4559-8fb7-d120209e63e9"))
                .sellerId(UUID.fromString("8be9a53b-997b-4559-8fb7-d120209e63e2"))
                .build();


        MvcResult result = this.mockMvc.perform(post(CONTROLLER_REQUIRED_ROUTE)
                        .content(this.mapper.writeValueAsString(invoiceDPO))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();


        String jsonResponse = result.getResponse().getContentAsString();
        InvoiceDPO invoiceResponse = new ObjectMapper().readValue(jsonResponse, InvoiceDPO.class);
        this.mockMvc.perform(get(CONTROLLER_REQUIRED_ROUTE + "/" + invoiceResponse.getIdentifier())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

}