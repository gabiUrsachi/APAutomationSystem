package org.example.presentation.controllers.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.example.persistence.collections.Item;
import org.example.persistence.utils.OrderStatus;
import org.example.presentation.view.OrderRequestDTO;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class PurchaseOrderControllerIntegrationTest {
    private final String ORDER_API_URL = "/api/orders";

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper;

    private JSONParser jsonParser;

    @BeforeEach
    public void setUp() {
        this.mapper = new ObjectMapper();
        this.jsonParser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
    }

    @Test
    public void testController() throws Exception {
        // create purchase order
        OrderRequestDTO orderDTO = createOrderDTO();
        RequestBuilder addOrderRequest = createPostRequest(orderDTO);

        MvcResult mvcResult = this.mockMvc.perform(addOrderRequest)
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderStatus").value(OrderStatus.CREATED.toString()))
                .andReturn();

        // get newly created purchase order
        UUID orderUUID = getOrderIdFromMvcResult(mvcResult);
        RequestBuilder getOrderRequest = createGetRequest(orderUUID);
        JSONArray initialOrderItems = parseItemSetToJSONArray(orderDTO.getItems());

        this.mockMvc.perform(getOrderRequest)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.identifier").value(orderUUID.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.buyer.companyIdentifier").value(orderDTO.getBuyer().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.seller.companyIdentifier").value(orderDTO.getSeller().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items").value(Matchers.containsInAnyOrder(initialOrderItems.toArray())))
        ;

        // update purchase order
        updateOrderItems(orderDTO);
        RequestBuilder updateOrderRequest = createPutRequest(orderUUID, orderDTO);

        this.mockMvc.perform(updateOrderRequest)
                .andExpect(status().isOk());

        // get updated order
        JSONArray updatedOrderItems = parseItemSetToJSONArray(orderDTO.getItems());

        this.mockMvc.perform(getOrderRequest)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.identifier").value(orderUUID.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items").value(Matchers.containsInAnyOrder(updatedOrderItems.toArray())))
        ;

        // save purchase order
        RequestBuilder saveOrderRequest = createPatchRequest(orderUUID);

        this.mockMvc.perform(saveOrderRequest)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.identifier").value(orderUUID.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderStatus").value(OrderStatus.SAVED.toString()));

        // delete purchase order
        RequestBuilder deleteOrderRequest = createDeleteRequest(orderUUID);

        this.mockMvc.perform(deleteOrderRequest)
                .andExpect(status().isOk());

        // delete again
        this.mockMvc.perform(deleteOrderRequest)
                .andExpect(status().isNotFound());
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

    private OrderRequestDTO createOrderDTO() {
        return OrderRequestDTO.builder()
                .buyer(generateCompanyIdentifier())
                .seller(generateCompanyIdentifier())
                .items(createItems())
                .build();
    }

    private Set<Item> createItems() {
        return Set.of(
                new Item("Item1", 10, 99.99F),
                new Item("Item2", 30, 230F),
                new Item("Item3", 15, 15.89F)
        );
    }

    private UUID generateCompanyIdentifier() {
        return UUID.fromString("2c70891c-50b5-436d-9496-7c3722adcab0");
    }

    private RequestBuilder createPostRequest(OrderRequestDTO orderRequestDTO) throws JsonProcessingException {
        return MockMvcRequestBuilders
                .post(ORDER_API_URL)
                .content(this.mapper.writeValueAsString(orderRequestDTO))
                .contentType(MediaType.APPLICATION_JSON);
    }

    private RequestBuilder createPutRequest(UUID orderIdentifier, OrderRequestDTO orderRequestDTO) throws JsonProcessingException {
        return MockMvcRequestBuilders
                .put(ORDER_API_URL + "/{identifier}", orderIdentifier)
                .content(this.mapper.writeValueAsString(orderRequestDTO))
                .contentType(MediaType.APPLICATION_JSON);
    }

    private RequestBuilder createDeleteRequest(UUID orderIdentifier) {
        return MockMvcRequestBuilders
                .delete(ORDER_API_URL + "/{identifier}", orderIdentifier);
    }

    private RequestBuilder createGetRequest(UUID orderIdentifier) {
        return MockMvcRequestBuilders
                .get(ORDER_API_URL + "/{identifier}", orderIdentifier);
    }

    private RequestBuilder createPatchRequest(UUID orderIdentifier) {
        return MockMvcRequestBuilders
                .patch(ORDER_API_URL + "/{identifier}", orderIdentifier);
    }
}
