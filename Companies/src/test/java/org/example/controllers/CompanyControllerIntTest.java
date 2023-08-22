package org.example.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.presentation.view.CompanyDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class CompanyControllerIntTest {

    private final String CONTROLLER_REQUIRED_ROUTE = "/api/companies";
    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void createCompany() throws Exception {

        CompanyDTO companyDTO = CompanyDTO.builder()
                .name("Company S.A.")
                .build();


        this.mockMvc.perform(post(CONTROLLER_REQUIRED_ROUTE)
                        .content(this.mapper.writeValueAsString(companyDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{'name':'Company S.A.'}"));
    }

    @Test
    void getCompanies() throws Exception {

        MvcResult result = this.mockMvc.perform(get(CONTROLLER_REQUIRED_ROUTE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();
        assertNotNull(result.getResponse());
    }

    @Test
    void getById() throws Exception {

        CompanyDTO companyDTO = CompanyDTO.builder()
                .name("Company S.A.")
                .build();

        MvcResult result = this.mockMvc.perform(post(CONTROLLER_REQUIRED_ROUTE)
                        .content(this.mapper.writeValueAsString(companyDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();


        String jsonResponse = result.getResponse().getContentAsString();
        CompanyDTO companyResponse = new ObjectMapper().readValue(jsonResponse, CompanyDTO.class);
        this.mockMvc.perform(get(CONTROLLER_REQUIRED_ROUTE + "/" + companyResponse.getCompanyIdentifier())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @Test
    void deleteById() throws Exception {
        CompanyDTO companyDTO = CompanyDTO.builder()
                .name("Company S.A.")
                .build();

        MvcResult result = this.mockMvc.perform(post(CONTROLLER_REQUIRED_ROUTE)
                        .content(this.mapper.writeValueAsString(companyDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();


        String jsonResponse = result.getResponse().getContentAsString();
        CompanyDTO companyResponse = new ObjectMapper().readValue(jsonResponse, CompanyDTO.class);
        this.mockMvc.perform(delete(CONTROLLER_REQUIRED_ROUTE + "/" + companyResponse.getCompanyIdentifier())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }


}