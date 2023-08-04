package org.example.business.services;

import org.example.business.errorhandling.ErrorMessages;
import org.example.business.errorhandling.customexceptions.InvalidUpdateException;
import org.example.business.errorhandling.customexceptions.OrderNotFoundException;
import org.example.persistence.collections.PurchaseOrder;
import org.example.persistence.repository.PurchaseOrderRepository;
import org.example.persistence.utils.OrderStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderOperationsService {
    private final PurchaseOrderRepository purchaseOrderRepository;

    public OrderOperationsService(PurchaseOrderRepository purchaseOrderRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
    }

    public PurchaseOrder createPurchaseOrder(PurchaseOrder purchaseOrder) {
        initOrderProperties(purchaseOrder);

        return purchaseOrderRepository.save(purchaseOrder);
    }

    public PurchaseOrder updatePurchaseOrder(UUID identifier, PurchaseOrder newPurchaseOrder) {
        PurchaseOrder oldPurchaseOrder = getPurchaseOrder(identifier);

        validateOrderUpdate(oldPurchaseOrder);
        copyOrderProperties(newPurchaseOrder, oldPurchaseOrder);

        return purchaseOrderRepository.save(newPurchaseOrder);
    }

    public PurchaseOrder savePurchaseOrder(UUID identifier) {
        PurchaseOrder purchaseOrder = getPurchaseOrder(identifier);

        purchaseOrder.setOrderStatus(OrderStatus.SAVED);

        return purchaseOrderRepository.save(purchaseOrder);
    }

    public PurchaseOrder getPurchaseOrder(UUID identifier) {
        Optional<PurchaseOrder> existingPurchaseOrder = purchaseOrderRepository.findById(identifier);

        return existingPurchaseOrder
                .orElseThrow(() -> new OrderNotFoundException(ErrorMessages.ORDER_NOT_FOUND, identifier));
    }

    public List<PurchaseOrder> getPurchaseOrders() {
        return purchaseOrderRepository.findAll();
    }

    public void deletePurchaseOrder(UUID identifier){
        PurchaseOrder purchaseOrder = getPurchaseOrder(identifier);

        this.purchaseOrderRepository.delete(purchaseOrder);
    }

    private void validateOrderUpdate(PurchaseOrder purchaseOrder) {
        if (!purchaseOrder.getOrderStatus().equals(OrderStatus.CREATED)) {
            throw new InvalidUpdateException(ErrorMessages.INVALID_UPDATE, purchaseOrder.getIdentifier());
        }
    }

    private void initOrderProperties(PurchaseOrder purchaseOrder) {
        UUID purchaseOrderIdentifier = UUID.randomUUID();

        purchaseOrder.setIdentifier(purchaseOrderIdentifier);
        purchaseOrder.setOrderStatus(OrderStatus.CREATED);
    }

    private void copyOrderProperties(PurchaseOrder newPurchaseOrder, PurchaseOrder oldPurchaseOrder) {
        newPurchaseOrder.setIdentifier(oldPurchaseOrder.getIdentifier());
        newPurchaseOrder.setOrderStatus(oldPurchaseOrder.getOrderStatus());
    }
}
