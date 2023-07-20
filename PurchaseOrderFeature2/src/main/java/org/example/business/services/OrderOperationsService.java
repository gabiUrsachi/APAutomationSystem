package org.example.business.services;

import org.example.business.errorhandling.ErrorMessages;
import org.example.business.errorhandling.customexceptions.OrderNotFoundException;
import org.example.business.models.OrderRequestDTO;
import org.example.business.models.OrderResponseDTO;
import org.example.persistence.collections.PurchaseOrder;
import org.example.persistence.repository.PurchaseOrderRepository;
import org.example.persistence.utils.OrderStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class OrderOperationsService {
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final MapperService mapperService;

    public OrderOperationsService(PurchaseOrderRepository purchaseOrderRepository, MapperService mapperService) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.mapperService = mapperService;
    }

    public OrderResponseDTO createPurchaseOrder(OrderRequestDTO orderRequestDTO){
        PurchaseOrder purchaseOrder = mapperService.mapToEntity(orderRequestDTO);
        initOrderProperties(purchaseOrder);

        PurchaseOrder savedPurchaseOrder = purchaseOrderRepository.save(purchaseOrder);

        return mapperService.mapToDTO(savedPurchaseOrder);
    }

    public void updatePurchaseOrder(UUID identifier, OrderRequestDTO orderRequestDTO){
        PurchaseOrder oldPurchaseOrder = verifyOrderExistence(identifier);

        PurchaseOrder newPurchaseOrder = mapperService.mapToEntity(orderRequestDTO);
        copyOrderProperties(newPurchaseOrder, oldPurchaseOrder);

        purchaseOrderRepository.save(newPurchaseOrder);
    }

    public List<OrderResponseDTO> getPurchaseOrders(){
        List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findAll();

        return mapperService.mapToDTO(purchaseOrders);
    }

    private PurchaseOrder verifyOrderExistence(UUID identifier){
        Optional<PurchaseOrder> oldPurchaseOrder = purchaseOrderRepository.findById(identifier);

        if(oldPurchaseOrder.isEmpty()){
            throw new OrderNotFoundException(ErrorMessages.ORDER_NOT_FOUND, identifier);
        }

        return oldPurchaseOrder.get();
    }

    private void initOrderProperties(PurchaseOrder purchaseOrder){
        UUID purchaseOrderIdentifier = UUID.randomUUID();

        purchaseOrder.setIdentifier(purchaseOrderIdentifier);
        purchaseOrder.setOrderStatus(OrderStatus.CREATED);
    }

    private void copyOrderProperties(PurchaseOrder newPurchaseOrder, PurchaseOrder oldPurchaseOrder){
        newPurchaseOrder.setIdentifier(oldPurchaseOrder.getIdentifier());
        newPurchaseOrder.setOrderStatus(oldPurchaseOrder.getOrderStatus());
    }
}
