package org.example.business.services;

import org.example.business.errorhandling.ErrorMessages;
import org.example.business.errorhandling.customexceptions.InvalidUpdateException;
import org.example.business.errorhandling.customexceptions.OrderNotFoundException;
import org.example.persistence.collections.PurchaseOrder;
import org.example.persistence.repository.PurchaseOrderRepository;
import org.example.persistence.utils.OrderStatus;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * This service is used for performing CRUD operations
 */
@Service
public class PurchaseOrderService {
    private final PurchaseOrderRepository purchaseOrderRepository;

    public PurchaseOrderService(PurchaseOrderRepository purchaseOrderRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
    }

    /**
     * It creates a new purchase order with random identifier and CREATED status
     *
     * @param purchaseOrder order to be saved
     * @return saved order
     */
    public PurchaseOrder createPurchaseOrder(PurchaseOrder purchaseOrder) {
        initOrderProperties(purchaseOrder);

        return purchaseOrderRepository.save(purchaseOrder);
    }

    /**
     * It verifies if the received order is in CREATED state, and then it replaces its content
     *
     * @param identifier       order UUID
     * @param newPurchaseOrder updated order content
     * @return updated order
     */
    public PurchaseOrder updatePurchaseOrder(UUID identifier, PurchaseOrder newPurchaseOrder) {
        PurchaseOrder oldPurchaseOrder = getPurchaseOrder(identifier);

        checkOrderVersion(oldPurchaseOrder, newPurchaseOrder);
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
     * It searches for an order with requested identifier
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
     * It searches all existing purchase orders
     *
     * @return the list of existing orders
     */
    public List<PurchaseOrder> getPurchaseOrders() {
        return purchaseOrderRepository.findAll();
    }

    /**
     * It removes the order identified by the given UUID from existing order list
     *
     * @param identifier order UUID
     */
    public void deletePurchaseOrder(UUID identifier) {
        int deletedRowsCount = this.purchaseOrderRepository.customDeleteById(identifier);

        if(deletedRowsCount == 0){
            throw new OrderNotFoundException(ErrorMessages.ORDER_NOT_FOUND, identifier);
        }
    }

    private void checkOrderVersion(PurchaseOrder oldPurchaseOrder, PurchaseOrder newPurchaseOrder) {
        if(!Objects.equals(oldPurchaseOrder.getVersion(), newPurchaseOrder.getVersion())){
            throw new OptimisticLockingFailureException(ErrorMessages.INVALID_VERSION);
        }
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
        //newPurchaseOrder.setVersion(oldPurchaseOrder.getVersion());
    }
}
