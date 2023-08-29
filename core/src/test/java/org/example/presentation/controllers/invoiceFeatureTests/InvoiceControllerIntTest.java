package org.example.presentation.controllers.invoiceFeatureTests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.example.AuthorisationControllerAdvice;
import org.example.business.models.InvoiceDPO;
import org.example.business.models.InvoiceDTO;
import org.example.filters.TokenValidationFilter;
import org.example.persistence.collections.Invoice;
import org.example.persistence.collections.Item;
import org.example.persistence.repository.InvoiceRepository;
import org.example.persistence.utils.InvoiceStatus;
import org.example.persistence.utils.data.OrderStatus;
import org.example.presentation.controllers.utils.InvoiceActionsPermissions;
import org.example.presentation.controllers.utils.ResourceActionType;
import org.example.presentation.view.CompanyDTO;
import org.example.presentation.view.OrderResponseDTO;
import org.example.utils.TokenHandler;
import org.example.utils.data.Roles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class InvoiceControllerIntTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    private final ObjectMapper mapper = new ObjectMapper();

    private JSONParser jsonParser;

    @Autowired
    private InvoiceRepository invoiceRepository;


    @BeforeEach
    public void setUp() {
        this.jsonParser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);

        TokenValidationFilter tokenValidationFilter = new TokenValidationFilter(new AuthorisationControllerAdvice());

        mockMvc = MockMvcBuilders.webAppContextSetup(wac).addFilters(tokenValidationFilter).build();


    }

    @Test
    void createInvoice() throws Exception {

        InvoiceDPO invoiceDPO = InvoiceDPO.builder()
                .buyerId(generateBuyerIdentifier())
                .sellerId(generateSellerIdentifier())
                .build();

        String jwt = TokenHandler.createToken("username", generateBuyerIdentifier(), InvoiceActionsPermissions.VALID_ROLES.get(ResourceActionType.CREATE));
        RequestBuilder insertInvoiceRequest = InvoiceRequestBuilder.createPostRequest(invoiceDPO, jwt);


        MvcResult mvcResult = this.mockMvc.perform(insertInvoiceRequest)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.invoiceStatus").value(InvoiceStatus.CREATED.toString()))
                .andReturn();

        UUID invoiceUUID = UUID.fromString(getInvoiceIdFromMvcResult(mvcResult));
        invoiceRepository.deleteById(String.valueOf(invoiceUUID));
    }


    @Test
    void createInvoiceFromPurchaseOrder() throws Exception {


        UUID uuid = UUID.randomUUID();
        OrderResponseDTO orderResponseDTO = OrderResponseDTO.builder()
                .identifier(uuid)
                .buyer(new CompanyDTO(generateBuyerIdentifier(), "name"))
                .seller(new CompanyDTO(generateSellerIdentifier(), "name"))
                .orderStatus(OrderStatus.CREATED)
                .version(0)
                .build();

        String jwt = TokenHandler.createToken("username", generateBuyerIdentifier(), Set.of(Roles.SUPPLIER_MANAGEMENT)); //InvoiceActionsPermissions.VALID_ROLES.get(ResourceActionType.CREATE_FROM_OR));
        RequestBuilder createInvoiceFromPORequest = InvoiceRequestBuilder.createInvoiceFromORRequest(orderResponseDTO, jwt);


        MvcResult mvcResult = this.mockMvc.perform(createInvoiceFromPORequest)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.invoiceStatus").value(InvoiceStatus.CREATED.toString()))
                .andReturn();

        UUID invoiceUUID = UUID.fromString(getInvoiceIdFromMvcResult(mvcResult));
        invoiceRepository.deleteById(String.valueOf(invoiceUUID));
//
//        String requestBody = new JSONObject()
//                .put("identifier", "4a9d8ff0-bc00-4282-bb18-f9c192d4ee47")
//                .put("buyer", new JSONObject()
//                        .put("companyIdentifier", "0c37ff0d-6c32-4850-ae01-1ca022b89442")
//                        .put("name", "CompanyA"))
//                .put("seller", new JSONObject()
//                        .put("companyIdentifier", "0c37ff0d-6c32-4850-ae01-1ca022b89443")
//                        .put("name", "CompanyB"))
//                .toString();
//
//        this.mockMvc.perform(post(CONTROLLER_REQUIRED_ROUTE + routeSuffix)
//                        .content(requestBody)
//                        .contentType(MediaType.APPLICATION_JSON)
//                )
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(content().json("{'identifier':'4a9d8ff0-bc00-4282-bb18-f9c192d4ee47'}"));
    }

    @Test
    void getInvoiceById() throws Exception {

        UUID uuid = UUID.randomUUID();

        Invoice invoice = Invoice.builder()
                .identifier(uuid)
                .buyerId(generateBuyerIdentifier())
                .sellerId(generateSellerIdentifier())
                .invoiceStatus(InvoiceStatus.CREATED)
                .version(0)
                .build();

        invoiceRepository.save(invoice);

        String jwt = TokenHandler.createToken("username", generateBuyerIdentifier(), InvoiceActionsPermissions.VALID_ROLES.get(ResourceActionType.GET));

        RequestBuilder getInvoiceRequest = InvoiceRequestBuilder.createIndividualGetRequest(uuid, jwt);

        this.mockMvc.perform(getInvoiceRequest)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.identifier").value(uuid.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.buyer.companyIdentifier").value(invoice.getBuyerId().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.seller.companyIdentifier").value(invoice.getSellerId().toString()));


        invoiceRepository.deleteByIdentifier(uuid);
    }

    @Test
    void deleteInvoiceById() throws Exception {

        UUID uuid = UUID.randomUUID();

        Invoice invoice = Invoice.builder()
                .identifier(uuid)
                .buyerId(generateBuyerIdentifier())
                .sellerId(generateSellerIdentifier())
                .invoiceStatus(InvoiceStatus.CREATED)
                .version(0)
                .build();

        invoiceRepository.save(invoice);

        String jwt = TokenHandler.createToken("username", generateBuyerIdentifier(), InvoiceActionsPermissions.VALID_ROLES.get(ResourceActionType.CREATE));

        RequestBuilder deleteOrderRequest = InvoiceRequestBuilder.createDeleteRequest(uuid, jwt);
        this.mockMvc.perform(deleteOrderRequest)
                .andExpect(status().isOk());

        deleteOrderRequest = InvoiceRequestBuilder.createDeleteRequest(uuid);
        this.mockMvc.perform(deleteOrderRequest)
                .andExpect(status().isUnauthorized());

        deleteOrderRequest = InvoiceRequestBuilder.createDeleteRequest(uuid, jwt);
        this.mockMvc.perform(deleteOrderRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateInvoice() throws Exception {
        UUID uuid = UUID.randomUUID();

        Invoice invoice = Invoice.builder()
                .identifier(uuid)
                .buyerId(generateBuyerIdentifier())
                .sellerId(generateSellerIdentifier())
                .invoiceStatus(InvoiceStatus.CREATED)
                .version(0)
                .build();

        invoiceRepository.save(invoice);

        String jwt = TokenHandler.createToken("username", generateBuyerIdentifier(), InvoiceActionsPermissions.VALID_ROLES.get(ResourceActionType.CREATE));

        InvoiceDTO invoiceDTO = InvoiceDTO.builder()
                .identifier(invoice.getIdentifier())
                .buyer(new CompanyDTO(invoice.getBuyerId(), "name"))
                .seller(new CompanyDTO(invoice.getBuyerId(), "name"))
                .invoiceStatus(invoice.getInvoiceStatus())
                .version(invoice.getVersion())
                .build();

        RequestBuilder updateInvoiceRequest = InvoiceRequestBuilder.createPutRequest(uuid, invoiceDTO, jwt);

        this.mockMvc.perform(updateInvoiceRequest)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.invoiceStatus").value(InvoiceStatus.PAID.toString()));


        invoiceDTO.setVersion(invoiceDTO.getVersion() + 1);
        updateInvoiceRequest = InvoiceRequestBuilder.createPutRequest(uuid, invoiceDTO);

        this.mockMvc.perform(updateInvoiceRequest)
                .andExpect(status().isUnauthorized());

        invoiceRepository.deleteByIdentifier(uuid);
    }

    private UUID generateBuyerIdentifier() {
        return UUID.fromString("9be9a53b-997b-4559-8fb7-d120209e63e2");
    }

    private UUID generateSellerIdentifier() {
        return UUID.fromString("9be9a53b-997b-4559-8fb7-d120209e63e2");
    }

    private UUID generateCompanyIdentifier() {
        return UUID.fromString("9be9a53b-997b-4559-8fb7-d120209e63e2");
    }

    private String getInvoiceIdFromMvcResult(MvcResult mvcResult) throws UnsupportedEncodingException {
        String response = mvcResult.getResponse().getContentAsString();
        return JsonPath.parse(response).read("$.identifier");
    }

    private JSONArray parseItemSetToJSONArray(Set<Item> items) throws JsonProcessingException, ParseException {
        return (JSONArray) jsonParser.parse(this.mapper.writeValueAsString(items));
    }

}
