//package org.example.presentation.controllers.invoiceFeatureTests;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.jayway.jsonpath.JsonPath;
//import net.minidev.json.JSONArray;
//import net.minidev.json.parser.JSONParser;
//import net.minidev.json.parser.ParseException;
//import org.example.AuthorisationControllerAdvice;
//import org.example.presentation.view.InvoiceDPO;
//import org.example.presentation.view.InvoiceDTO;
//import org.example.filters.TokenValidationFilter;
//import org.example.persistence.collections.Company;
//import org.example.persistence.collections.Invoice;
//import org.example.persistence.collections.Item;
//import org.example.persistence.repository.CompanyRepository;
//import org.example.persistence.repository.InvoiceRepository;
//import org.example.persistence.utils.InvoiceStatus;
//import org.example.persistence.utils.data.OrderStatus;
//import org.example.presentation.controllers.utils.InvoiceActionsPermissions;
//import org.example.presentation.controllers.utils.ResourceActionType;
//import org.example.presentation.view.CompanyDTO;
//import org.example.presentation.view.OrderResponseDTO;
//import org.example.utils.TokenHandler;
//import org.example.utils.data.Roles;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.RequestBuilder;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//
//import java.io.UnsupportedEncodingException;
//import java.util.Set;
//import java.util.UUID;
//
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//class InvoiceControllerIntTest {
//
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private WebApplicationContext wac;
//
//    private final ObjectMapper mapper = new ObjectMapper();
//
//    private JSONParser jsonParser;
//
//    @Autowired
//    private InvoiceRepository invoiceRepository;
//
//    @Autowired
//    private CompanyRepository companyRepository;
//
//    @BeforeEach
//    public void setUp() {
//        this.jsonParser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
//
//        TokenValidationFilter tokenValidationFilter = new TokenValidationFilter(new AuthorisationControllerAdvice());
//
//        mockMvc = MockMvcBuilders.webAppContextSetup(wac).addFilters(tokenValidationFilter).build();
//
//        this.companyRepository.save(getStoredCompany());
//    }
//
//    @AfterEach
//    public void clean(){
//        this.companyRepository.delete(getStoredCompany());
//    }
//
//    @Test
//    void createInvoice() throws Exception {
//
//        InvoiceDPO invoiceDPO = InvoiceDPO.builder()
//                .buyerId(getStoredCompany().getCompanyIdentifier())
//                .sellerId(getStoredCompany().getCompanyIdentifier())
//                .items(createItems())
//                .build();
//
//        String jwt = TokenHandler.createToken("username", getStoredCompany().getCompanyIdentifier(), InvoiceActionsPermissions.VALID_ROLES.get(ResourceActionType.CREATE));
//        RequestBuilder insertInvoiceRequest = InvoiceRequestBuilder.createPostRequest(invoiceDPO, jwt);
//
//
//        MvcResult mvcResult = this.mockMvc.perform(insertInvoiceRequest)
//                .andExpect(status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.invoiceStatus").value(InvoiceStatus.CREATED.toString()))
//                .andReturn();
//
//        UUID invoiceUUID = UUID.fromString(getInvoiceIdFromMvcResult(mvcResult));
//        invoiceRepository.deleteById(String.valueOf(invoiceUUID));
//    }
//
//
//    @Test
//    void createInvoiceFromPurchaseOrder() throws Exception {
//
//
//        UUID uuid = UUID.randomUUID();
//        OrderResponseDTO orderResponseDTO = OrderResponseDTO.builder()
//                .identifier(uuid)
//                .buyer(new CompanyDTO(getStoredCompany().getCompanyIdentifier(), "name"))
//                .seller(new CompanyDTO(getStoredCompany().getCompanyIdentifier(), "name"))
//                .orderStatus(OrderStatus.CREATED)
//                .items(createItems())
//                .version(0)
//                .build();
//
//        String jwt = TokenHandler.createToken("username", getStoredCompany().getCompanyIdentifier(), Set.of(Roles.SUPPLIER_ACCOUNTING)); //InvoiceActionsPermissions.VALID_ROLES.get(ResourceActionType.CREATE_FROM_OR));
//        RequestBuilder createInvoiceFromPORequest = InvoiceRequestBuilder.createInvoiceFromORRequest(orderResponseDTO, jwt);
//
//
//        MvcResult mvcResult = this.mockMvc.perform(createInvoiceFromPORequest)
//                .andExpect(status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.invoiceStatus").value(InvoiceStatus.CREATED.toString()))
//                .andReturn();
//
//        UUID invoiceUUID = UUID.fromString(getInvoiceIdFromMvcResult(mvcResult));
//        invoiceRepository.deleteById(String.valueOf(invoiceUUID));
//    }
//
//    @Test
//    void getInvoiceById() throws Exception {
//
//        UUID uuid = UUID.randomUUID();
//
//        Invoice invoice = Invoice.builder()
//                .identifier(uuid)
//                .buyerId(getStoredCompany().getCompanyIdentifier())
//                .sellerId(getStoredCompany().getCompanyIdentifier())
//                .invoiceStatus(InvoiceStatus.CREATED)
//                .version(0)
//                .build();
//
//        invoiceRepository.save(invoice);
//
//        String jwt = TokenHandler.createToken("username", getStoredCompany().getCompanyIdentifier(), InvoiceActionsPermissions.VALID_ROLES.get(ResourceActionType.GET));
//
//        RequestBuilder getInvoiceRequest = InvoiceRequestBuilder.createIndividualGetRequest(uuid, jwt);
//
//        this.mockMvc.perform(getInvoiceRequest)
//                .andExpect(status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.identifier").value(uuid.toString()))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.buyer.companyIdentifier").value(invoice.getBuyerId().toString()))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.seller.companyIdentifier").value(invoice.getSellerId().toString()));
//
//
//        invoiceRepository.deleteByIdentifier(uuid);
//    }
//
//    @Test
//    void deleteInvoiceById() throws Exception {
//
//        UUID uuid = UUID.randomUUID();
//
//        Invoice invoice = Invoice.builder()
//                .identifier(uuid)
//                .buyerId(getStoredCompany().getCompanyIdentifier())
//                .sellerId(getStoredCompany().getCompanyIdentifier())
//                .invoiceStatus(InvoiceStatus.CREATED)
//                .version(0)
//                .build();
//
//        invoiceRepository.save(invoice);
//
//        String jwt = TokenHandler.createToken("username", getStoredCompany().getCompanyIdentifier(), InvoiceActionsPermissions.VALID_ROLES.get(ResourceActionType.CREATE));
//
//        RequestBuilder deleteOrderRequest = InvoiceRequestBuilder.createDeleteRequest(uuid, jwt);
//        this.mockMvc.perform(deleteOrderRequest)
//                .andExpect(status().isOk());
//
//        deleteOrderRequest = InvoiceRequestBuilder.createDeleteRequest(uuid);
//        this.mockMvc.perform(deleteOrderRequest)
//                .andExpect(status().isUnauthorized());
//
//        deleteOrderRequest = InvoiceRequestBuilder.createDeleteRequest(uuid, jwt);
//        this.mockMvc.perform(deleteOrderRequest)
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    public void updateInvoice() throws Exception {
//        UUID uuid = UUID.randomUUID();
//
//        Invoice invoice = Invoice.builder()
//                .identifier(uuid)
//                .buyerId(getStoredCompany().getCompanyIdentifier())
//                .sellerId(getStoredCompany().getCompanyIdentifier())
//                .invoiceStatus(InvoiceStatus.CREATED)
//                .version(0)
//                .build();
//
//        invoiceRepository.save(invoice);
//
//        String jwt = TokenHandler.createToken("username", getStoredCompany().getCompanyIdentifier(), InvoiceActionsPermissions.VALID_ROLES.get(ResourceActionType.CREATE));
//
//        InvoiceDTO invoiceDTO = InvoiceDTO.builder()
//                .identifier(invoice.getIdentifier())
//                .buyer(new CompanyDTO(invoice.getBuyerId(), "name"))
//                .seller(new CompanyDTO(invoice.getBuyerId(), "name"))
//                .invoiceStatus(InvoiceStatus.SENT)
//                .version(invoice.getVersion())
//                .build();
//
//        RequestBuilder updateInvoiceRequest = InvoiceRequestBuilder.createPutRequest(uuid, invoiceDTO, jwt);
//
//        this.mockMvc.perform(updateInvoiceRequest)
//                .andExpect(status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.invoiceStatus").value(InvoiceStatus.SENT.toString()));
//
//
//        invoiceDTO.setVersion(invoiceDTO.getVersion() + 1);
//        updateInvoiceRequest = InvoiceRequestBuilder.createPutRequest(uuid, invoiceDTO);
//
//        this.mockMvc.perform(updateInvoiceRequest)
//                .andExpect(status().isUnauthorized());
//
//        invoiceRepository.deleteByIdentifier(uuid);
//    }
//
//    private String getInvoiceIdFromMvcResult(MvcResult mvcResult) throws UnsupportedEncodingException {
//        String response = mvcResult.getResponse().getContentAsString();
//        return JsonPath.parse(response).read("$.identifier");
//    }
//
//    private JSONArray parseItemSetToJSONArray(Set<Item> items) throws JsonProcessingException, ParseException {
//        return (JSONArray) jsonParser.parse(this.mapper.writeValueAsString(items));
//    }
//
//    private Company getStoredCompany(){
//        return Company.builder().companyIdentifier(UUID.fromString("2c70891c-50b5-436d-9496-7c3722adcab0")).name("Company name").build();
//    }
//
//    private Set<Item> createItems() {
//        return Set.of(
//                new Item("Item1", 10, 99.99F),
//                new Item("Item2", 30, 230F),
//                new Item("Item3", 15, 15.89F)
//        );
//    }
//}
