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
        purchaseOrder.setIdentifier(UUID.randomUUID());
        purchaseOrder.setVersion(0);
        purchaseOrder.setOrderStatus(OrderStatus.CREATED);

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
     * It replaces the content of an existing order only if the received version corresponds to the stored one
     *
     * @param purchaseOrder updated order content
     * @return updated order
     */
    public PurchaseOrder updatePurchaseOrder(PurchaseOrder purchaseOrder) {
        int oldVersion = purchaseOrder.getVersion();

        PurchaseOrder updatedPurchaseOrder = PurchaseOrder.builder()
                .identifier(purchaseOrder.getIdentifier())
                .buyer(purchaseOrder.getBuyer())
                .seller(purchaseOrder.getSeller())
                .items(purchaseOrder.getItems())
                .orderStatus(purchaseOrder.getOrderStatus())
                .version(oldVersion + 1)
                .build();

        int updateCount = purchaseOrderRepository.updateByIdentifierAndVersion(purchaseOrder.getIdentifier(), oldVersion, updatedPurchaseOrder);

        if (updateCount == 0) {
            Optional<PurchaseOrder> existingPurchaseOrder = purchaseOrderRepository.findById(purchaseOrder.getIdentifier());

            if(existingPurchaseOrder.isEmpty()){
                throw new OrderNotFoundException(ErrorMessages.ORDER_NOT_FOUND, purchaseOrder.getIdentifier());
            }

            if(!Objects.equals(existingPurchaseOrder.get().getVersion(), purchaseOrder.getVersion())){
                throw new OptimisticLockingFailureException(ErrorMessages.INVALID_VERSION);
            }

            if (!existingPurchaseOrder.get().getOrderStatus().equals(OrderStatus.CREATED)) {
                throw new InvalidUpdateException(ErrorMessages.INVALID_UPDATE, existingPurchaseOrder.get().getIdentifier());
            }
        }

        return updatedPurchaseOrder;
    }

    /**
     * It removes the order identified by the given UUID from existing order list
     *
     * @param identifier order UUID
     */
    public void deletePurchaseOrder(UUID identifier) {
        int deletedRowsCount = this.purchaseOrderRepository.customDeleteById(identifier);

        if (deletedRowsCount == 0) {
            throw new OrderNotFoundException(ErrorMessages.ORDER_NOT_FOUND, identifier);
        }
    }

}
