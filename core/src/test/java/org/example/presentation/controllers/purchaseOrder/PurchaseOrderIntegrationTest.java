package org.example.presentation.controllers.purchaseOrder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.example.AuthorisationControllerAdvice;
import org.example.filters.TokenValidationFilter;
import org.example.persistence.collections.Item;
import org.example.persistence.collections.PurchaseOrder;
import org.example.persistence.repository.PurchaseOrderRepository;
import org.example.persistence.utils.data.OrderStatus;
import org.example.presentation.utils.ActionsPermissions;
import org.example.presentation.utils.ResourceActionType;
import org.example.presentation.view.OrderRequestDTO;
import org.example.utils.TokenHandler;
import org.hamcrest.Matchers;
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
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@SpringBootTest(classes = {org.example.Main.class})
public class PurchaseOrderIntegrationTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    private ObjectMapper mapper;

    private JSONParser jsonParser;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @BeforeEach
    public void setUp() {
        this.mapper = new ObjectMapper();
        this.jsonParser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);

        TokenValidationFilter tokenValidationFilter = new TokenValidationFilter(new AuthorisationControllerAdvice());

        mockMvc = MockMvcBuilders.webAppContextSetup(wac).addFilters(tokenValidationFilter).build();
    }

    @Test
    public void getPurchaseOrderById() throws Exception {
        UUID uuid = UUID.randomUUID();
        PurchaseOrder purchaseOrder = createPurchaseOrderWithStatusAndUUID(OrderStatus.CREATED, uuid);

        purchaseOrderRepository.save(purchaseOrder);

        String jwt = TokenHandler.createToken("random username", generateBuyerIdentifier(), ActionsPermissions.VALID_ROLES.get(ResourceActionType.GET));
        RequestBuilder getOrderRequest = PurchaseOrderRequestBuilder.createGetRequest(uuid, jwt);

        JSONArray orderItems = parseItemSetToJSONArray(purchaseOrder.getItems());

        this.mockMvc.perform(getOrderRequest)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.identifier").value(uuid.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.buyer.companyIdentifier").value(purchaseOrder.getBuyer().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.seller.companyIdentifier").value(purchaseOrder.getSeller().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items").value(Matchers.containsInAnyOrder(orderItems.toArray())));

        purchaseOrderRepository.deleteById(uuid);
    }

    @Test
    public void createPurchaseOrder() throws Exception {
        OrderRequestDTO orderDTO = createOrderDTO(UUID.randomUUID(), 0);

        String jwt = TokenHandler.createToken("random username", generateBuyerIdentifier(), ActionsPermissions.VALID_ROLES.get(ResourceActionType.CREATE));
        RequestBuilder addOrderRequest = PurchaseOrderRequestBuilder.createPostRequest(orderDTO, jwt);

        MvcResult mvcResult = this.mockMvc.perform(addOrderRequest)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderStatus").value(OrderStatus.CREATED.toString()))
                .andReturn();

        UUID orderUUID = getOrderIdFromMvcResult(mvcResult);
        purchaseOrderRepository.deleteById(orderUUID);
    }

    @Test
    public void updatePurchaseOrder() throws Exception {
        UUID uuid = UUID.randomUUID();
        PurchaseOrder purchaseOrder = createPurchaseOrderWithStatusAndUUID(OrderStatus.CREATED, uuid);

        purchaseOrderRepository.save(purchaseOrder);

        String jwt = TokenHandler.createToken("random username", generateBuyerIdentifier(), ActionsPermissions.VALID_ROLES.get(ResourceActionType.UPDATE));

        // first update
        OrderRequestDTO orderDTO = createOrderDTO(purchaseOrder.getIdentifier(), purchaseOrder.getVersion());
        RequestBuilder updateOrderRequest = PurchaseOrderRequestBuilder.createPutRequest(uuid, orderDTO, jwt);
        JSONArray initialUpdatedOrderItems = parseItemSetToJSONArray(orderDTO.getItems());

        this.mockMvc.perform(updateOrderRequest)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderStatus").value(OrderStatus.CREATED.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items").value(Matchers.containsInAnyOrder(initialUpdatedOrderItems.toArray())));

        // second update
        updateOrderItems(orderDTO);
        orderDTO.setVersion(orderDTO.getVersion() + 1);
        updateOrderRequest = PurchaseOrderRequestBuilder.createPutRequest(uuid, orderDTO, jwt);
        JSONArray intermediateUpdatedOrderItems = parseItemSetToJSONArray(orderDTO.getItems());

        this.mockMvc.perform(updateOrderRequest)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderStatus").value(OrderStatus.CREATED.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items").value(Matchers.containsInAnyOrder(intermediateUpdatedOrderItems.toArray())));

        // third update (without JWT)
        updateOrderItems(orderDTO);
        orderDTO.setVersion(orderDTO.getVersion() + 1);
        updateOrderRequest = PurchaseOrderRequestBuilder.createPutRequest(uuid, orderDTO);
        JSONArray finalUpdatedOrderItems = parseItemSetToJSONArray(orderDTO.getItems());

        this.mockMvc.perform(updateOrderRequest)
                .andExpect(status().isUnauthorized());

        purchaseOrderRepository.deleteById(uuid);
    }


    @Test
    public void deletePurchaseOrder() throws Exception {
        UUID uuid = UUID.randomUUID();
        PurchaseOrder purchaseOrder = createPurchaseOrderWithStatusAndUUID(OrderStatus.CREATED, uuid);

        purchaseOrderRepository.save(purchaseOrder);

        String jwt = TokenHandler.createToken("random username", generateBuyerIdentifier(), ActionsPermissions.VALID_ROLES.get(ResourceActionType.UPDATE));

        // delete purchase order with jwt
        RequestBuilder deleteOrderRequest = PurchaseOrderRequestBuilder.createDeleteRequest(uuid, jwt);
        this.mockMvc.perform(deleteOrderRequest)
                .andExpect(status().isOk());

        // try to delete again (without jwt)
        deleteOrderRequest = PurchaseOrderRequestBuilder.createDeleteRequest(uuid);
        this.mockMvc.perform(deleteOrderRequest)
                .andExpect(status().isUnauthorized());

        // try to delete again (with jwt)
        deleteOrderRequest = PurchaseOrderRequestBuilder.createDeleteRequest(uuid, jwt);
        this.mockMvc.perform(deleteOrderRequest)
                .andExpect(status().isNotFound());
    }

    private PurchaseOrder createPurchaseOrderWithStatusAndUUID(OrderStatus orderStatus, UUID uuid) {
        return PurchaseOrder.builder()
                .identifier(uuid)
                .buyer(generateCompanyIdentifier())
                .seller(generateCompanyIdentifier())
                .orderStatus(orderStatus)
                .items(Set.of())
                .version(0)
                .build();
    }

    private void updateOrderItems(OrderRequestDTO orderDTO) {
        Set<Item> newItems = new HashSet<>(orderDTO.getItems());
        newItems.add(new Item("New item", 100, 100F));

        orderDTO.setItems(newItems);
    }

    private JSONArray parseItemSetToJSONArray(Set<Item> items) throws JsonProcessingException, ParseException {
        return (JSONArray) jsonParser.parse(this.mapper.writeValueAsString(items));
    }

    private UUID getOrderIdFromMvcResult(MvcResult mvcResult) throws UnsupportedEncodingException {
        String response = mvcResult.getResponse().getContentAsString();

        return UUID.fromString(JsonPath.parse(response).read("$.identifier"));
    }

    private OrderRequestDTO createOrderDTO(UUID identifier, Integer version) {
        return OrderRequestDTO.builder()
                .identifier(identifier)
                .buyer(generateBuyerIdentifier())
                .seller(generateSellerIdentifier())
                .items(createItems())
                .orderStatus(OrderStatus.CREATED)
                .version(version)
                .build();
    }

    private Set<Item> createItems() {
        return Set.of(
                new Item("Item1", 10, 99.99F),
                new Item("Item2", 30, 230F),
                new Item("Item3", 15, 15.89F)
        );
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
}
