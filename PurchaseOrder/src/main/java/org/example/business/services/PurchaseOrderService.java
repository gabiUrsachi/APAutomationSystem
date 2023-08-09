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
     * It replaces the content of an existing order if it has CREATED status and its version is updated
     *
     * @param identifier       order UUID
     * @param newPurchaseOrder updated order content
     * @return updated order
     */
    public PurchaseOrder updatePurchaseOrder(UUID identifier, PurchaseOrder newPurchaseOrder) {
        setOrderProperties(identifier, newPurchaseOrder);

        int oldVersion = updateOrderVersion(newPurchaseOrder);
        int updateCount = purchaseOrderRepository.updateByIdentifierAndVersionAndOrderStatus(identifier, oldVersion, OrderStatus.CREATED, newPurchaseOrder);

        if(updateCount == 0){
            verifyInvalidUpdateReasons(identifier);
        }

        return newPurchaseOrder;
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
        updateOrderVersion(purchaseOrder);

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

    private void verifyInvalidUpdateReasons(UUID identifier) {
        PurchaseOrder purchaseOrder = getPurchaseOrder(identifier);

        if (!purchaseOrder.getOrderStatus().equals(OrderStatus.CREATED)) {
            throw new InvalidUpdateException(ErrorMessages.INVALID_UPDATE, purchaseOrder.getIdentifier());
        }

        throw new OptimisticLockingFailureException(ErrorMessages.INVALID_VERSION);
    }

    private void initOrderProperties(PurchaseOrder purchaseOrder) {
        UUID purchaseOrderIdentifier = UUID.randomUUID();

        purchaseOrder.setIdentifier(purchaseOrderIdentifier);
        purchaseOrder.setOrderStatus(OrderStatus.CREATED);
        purchaseOrder.setVersion(0);
    }

    private void setOrderProperties(UUID identifier, PurchaseOrder purchaseOrder){
        purchaseOrder.setIdentifier(identifier);
        purchaseOrder.setOrderStatus(OrderStatus.CREATED);
    }

    private int updateOrderVersion(PurchaseOrder purchaseOrder){
        int oldVersion = purchaseOrder.getVersion();
        int newVersion = oldVersion + 1;

        purchaseOrder.setVersion(newVersion);

        return oldVersion;
    }
}
