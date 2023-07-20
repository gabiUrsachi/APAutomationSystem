package org.example.business.services;

import org.example.business.errorhandling.customexceptions.OrderNotFoundException;
import org.example.business.models.OrderRequestDTO;
import org.example.persistence.collections.PurchaseOrder;
import org.example.persistence.repository.PurchaseOrderRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class OrderOperationsServiceShould {
    @Mock PurchaseOrderRepository purchaseOrderRepository;
    @Mock MapperService mapperService;
    @Mock OrderRequestDTO orderRequestDTO;
    @Mock PurchaseOrder purchaseOrder;

    OrderOperationsService orderOperationsService;

    @Before
    public void initialize(){
        orderOperationsService = new OrderOperationsService(purchaseOrderRepository, mapperService);
    }

    @Test
    public void storeAPurchaseOrder(){
        given(mapperService.mapToEntity(orderRequestDTO)).willReturn(purchaseOrder);

        orderOperationsService.createPurchaseOrder(orderRequestDTO);

        verify(purchaseOrderRepository).save(purchaseOrder);
    }

    @Test
    public void notUpdateNonExistingPurchaseOrder(){
        UUID uuid = UUID.randomUUID();
        given(purchaseOrderRepository.findById(uuid)).willReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, ()->orderOperationsService.updatePurchaseOrder(uuid, orderRequestDTO));
    }

    @Test
    public void updateAnExistingPurchaseOrder(){
        UUID uuid = UUID.randomUUID();
        given(purchaseOrderRepository.findById(uuid)).willReturn(Optional.of(purchaseOrder));
        given(mapperService.mapToEntity(orderRequestDTO)).willReturn(purchaseOrder);

        orderOperationsService.updatePurchaseOrder(uuid, orderRequestDTO);

        verify(purchaseOrderRepository).save(purchaseOrder);
    }

}