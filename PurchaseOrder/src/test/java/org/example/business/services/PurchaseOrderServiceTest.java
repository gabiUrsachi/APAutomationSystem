package org.example.business.services;

import org.example.persistence.collections.PurchaseOrder;
import org.example.persistence.repository.PurchaseOrderRepository;
import org.example.persistence.utils.OrderStatus;
import org.example.persistence.utils.PurchaseOrderFilter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
public class PurchaseOrderServiceTest {
    @Mock
    PurchaseOrderRepository purchaseOrderRepository;

    PurchaseOrderService purchaseOrderService;

    @Before
    public void initialize() {
        purchaseOrderService = new PurchaseOrderService(purchaseOrderRepository);
    }

    @Test
    public void testQuery(){
        List<PurchaseOrderFilter> filters = List.of(
                PurchaseOrderFilter.builder()
                        .requiredStatus(OrderStatus.CREATED)
                        .companyUUID(UUID.fromString("2c70891c-50b5-436d-9496-7c3722adcab0"))
                        .companyType("buyer")
                        .build());

        List<PurchaseOrder> purchaseOrders = purchaseOrderService.getPurchaseOrders(filters);

        assertEquals(1, purchaseOrders.size());
    }


}