package org.system.business.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.system.business.models.OrderRequestDTO;
import org.system.business.models.OrderResponseDTO;
import org.system.persistence.collections.PurchaseOrder;
import org.system.persistence.repository.PurchaseOrderRepository;
import org.system.persistence.utils.OrderStatus;

import java.util.UUID;

@Service
public class PurchaseOrderOpsService {
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    public OrderResponseDTO createPurchaseOrder(OrderRequestDTO orderRequestDTO){
        // map dto to entity
        PurchaseOrder purchaseOrder = mapToEntity(orderRequestDTO);
        purchaseOrder.setOrderStatus(OrderStatus.CREATED);

        // persist
        PurchaseOrder savedPurchaseOrder = purchaseOrderRepository.insert(purchaseOrder);

        return mapToDTO(savedPurchaseOrder);
    }

    private PurchaseOrder mapToEntity(OrderRequestDTO orderRequestDTO){
        UUID purchaseOrderIdentifier = UUID.randomUUID();

        return PurchaseOrder.builder()
                .identifier(purchaseOrderIdentifier)
                .buyer(orderRequestDTO.getBuyer())
                .seller(orderRequestDTO.getSeller())
                .items(orderRequestDTO.getItems())
                .build();
    }

    private OrderResponseDTO mapToDTO(PurchaseOrder purchaseOrder){
        return OrderResponseDTO.builder()
                .identifier(purchaseOrder.getIdentifier())
                .buyer(purchaseOrder.getBuyer())
                .seller(purchaseOrder.getSeller())
                .items(purchaseOrder.getItems())
                .orderStatus(purchaseOrder.getOrderStatus())
                .build();
    }

}
