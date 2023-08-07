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

/**
 * This service is used for performing database CRUD operations
 */
@Service
public class OrderOperationsService {
    private final PurchaseOrderRepository purchaseOrderRepository;

    public OrderOperationsService(PurchaseOrderRepository purchaseOrderRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
    }

    /**
     * It creates a new purchase order with random identifier and CREATED status
     *
     * @param purchaseOrder order to be saved
     * @return inserted order
     */
    public PurchaseOrder createPurchaseOrder(PurchaseOrder purchaseOrder) {
        initOrderProperties(purchaseOrder);

        return purchaseOrderRepository.save(purchaseOrder);
    }

    /**
     * It verifies if the received order is not already in SAVED state, and then it replaces its content
     *
     * @param identifier       order UUID
     * @param newPurchaseOrder updated order content
     * @return updated order
     */
    public PurchaseOrder updatePurchaseOrder(UUID identifier, PurchaseOrder newPurchaseOrder) {
        PurchaseOrder oldPurchaseOrder = getPurchaseOrder(identifier);

        validateOrderUpdate(oldPurchaseOrder);
        copyOrderProperties(newPurchaseOrder, oldPurchaseOrder);

        return purchaseOrderRepository.save(newPurchaseOrder);
    }

    /**
     * It changes state of an existing order
     *
     * @param identifier order UUID
     * @return updated order
     */
    public PurchaseOrder savePurchaseOrder(UUID identifier) {
        PurchaseOrder purchaseOrder = getPurchaseOrder(identifier);

        purchaseOrder.setOrderStatus(OrderStatus.SAVED);

        return purchaseOrderRepository.save(purchaseOrder);
    }

    /**
     * It queries database for an order with requested identifier
     *
     * @param identifier order UUID
     * @return searched order if exists, otherwise it throws OrderNotFoundException
     */
    public PurchaseOrder getPurchaseOrder(UUID identifier) {
        Optional<PurchaseOrder> existingPurchaseOrder = purchaseOrderRepository.findById(identifier);

        return existingPurchaseOrder
                .orElseThrow(() -> new OrderNotFoundException(ErrorMessages.ORDER_NOT_FOUND, identifier));
    }

    /**
     * It queries database for all existing purchase orders
     *
     * @return the list of orders
     */
    public List<PurchaseOrder> getPurchaseOrders() {
        return purchaseOrderRepository.findAll();
    }

    /**
     * It removes the order identified by the given UUID from database
     *
     * @param identifier order UUID
     */
    public void deletePurchaseOrder(UUID identifier) {
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
