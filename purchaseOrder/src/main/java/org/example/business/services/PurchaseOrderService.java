package org.example.business.services;

import org.example.SQSOps;
import org.example.business.utils.PurchaseOrderStatusPrecedence;
import org.example.customexceptions.InvalidResourceUpdateException;
import org.example.customexceptions.ResourceNotFoundException;
import org.example.persistence.collections.PurchaseOrder;
import org.example.persistence.repository.PurchaseOrderRepository;
import org.example.persistence.utils.data.OrderStatus;
import org.example.persistence.utils.data.PurchaseOrderFilter;
import org.example.utils.ErrorMessages;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;


import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

import static org.example.business.utils.PurchaseOrderHistoryHelper.*;


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

        purchaseOrder.setUri(purchaseOrder.getIdentifier() + "." + purchaseOrder.getUri());

        purchaseOrder.setStatusHistory(generateOrderHistoryList(OrderStatus.CREATED));

        return purchaseOrderRepository.save(purchaseOrder);

    }

    /**
     * Retrieves a purchase order based on its identifier
     *
     * @param identifier order UUID
     * @return the retrieved PurchaseOrder object
     * @throws ResourceNotFoundException if the purchase order with the given identifier is not found
     */
    public PurchaseOrder getPurchaseOrder(UUID identifier) {
        Optional<PurchaseOrder> existingPurchaseOrder = purchaseOrderRepository.findById(identifier);

        return existingPurchaseOrder
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ORDER_NOT_FOUND, identifier.toString()));
    }

    /**
     * Retrieves a purchase order based on its identifier and filters
     *
     * @param identifier order UUID
     * @param filters    list of filters to apply while retrieving the purchase order
     * @return the retrieved PurchaseOrder object
     * @throws ResourceNotFoundException if the purchase order with the given identifier and filters is not found
     */
    public PurchaseOrder getPurchaseOrder(UUID identifier, List<PurchaseOrderFilter> filters) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findByUUIDAndFilters(identifier, filters);

        if (purchaseOrder == null) {
            throw new ResourceNotFoundException(ErrorMessages.ORDER_NOT_FOUND, identifier.toString());
        }

        return purchaseOrder;
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
     * It searches all existing purchase orders according to given filters
     *
     * @return the list of existing orders
     */
    public List<PurchaseOrder> getPurchaseOrders(List<PurchaseOrderFilter> filters) {
        return purchaseOrderRepository.findByFilters(filters);
    }

    /**
     * It replaces the content of an existing order only if the received version corresponds to the stored one
     *
     * @param purchaseOrder updated order content
     * @return updated order
     */
    public PurchaseOrder updatePurchaseOrder(PurchaseOrder purchaseOrder) {
        int oldVersion = purchaseOrder.getVersion();

        OrderStatus requiredOldStatus = PurchaseOrderStatusPrecedence.PREDECESSORS.get(getLatestOrderHistoryObject(purchaseOrder.getStatusHistory()).getStatus());

        PurchaseOrder updatedPurchaseOrder = PurchaseOrder.builder()
                .identifier(purchaseOrder.getIdentifier())
                .buyer(purchaseOrder.getBuyer())
                .seller(purchaseOrder.getSeller())
                .items(purchaseOrder.getItems())
                .statusHistory(purchaseOrder.getStatusHistory())
                .version(oldVersion + 1)
                .uri(purchaseOrder.getUri())
                .build();

        int updateCount = purchaseOrderRepository.updateByIdentifierAndVersion(purchaseOrder.getIdentifier(), oldVersion, requiredOldStatus, updatedPurchaseOrder);

        if (updateCount == 0) {
            Optional<PurchaseOrder> existingPurchaseOrder = purchaseOrderRepository.findById(purchaseOrder.getIdentifier());

            if (existingPurchaseOrder.isEmpty()) {
                throw new ResourceNotFoundException(ErrorMessages.ORDER_NOT_FOUND, purchaseOrder.getIdentifier().toString());
            }

            if (!Objects.equals(existingPurchaseOrder.get().getVersion(), purchaseOrder.getVersion())) {
                throw new OptimisticLockingFailureException(ErrorMessages.INVALID_VERSION);
            }

            OrderStatus latestOrderStatus = getLatestOrderHistoryObject(existingPurchaseOrder.get().getStatusHistory()).getStatus();
            if (!latestOrderStatus.equals(requiredOldStatus)) {
                throw new InvalidResourceUpdateException(ErrorMessages.INVALID_UPDATE, existingPurchaseOrder.get().getIdentifier());
            }
        }

        if (getLatestOrderHistoryObject(updatedPurchaseOrder.getStatusHistory()).equals(OrderStatus.SAVED)) {
            // buyerCompany/documentId/sellerCompany
            SQSOps.sendMessage(updatedPurchaseOrder.getBuyer() + "/" + updatedPurchaseOrder.getIdentifier() + "/" + updatedPurchaseOrder.getSeller());
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
            throw new ResourceNotFoundException(ErrorMessages.ORDER_NOT_FOUND, identifier.toString());
        }
    }


    /**
     * Compute purchase order company taxes for a given month
     *
     * @param month  Integer
     * @param year   Integer
     * @param filter PurchaseOrderFilter Filter containing the company uuid
     */
    public Float computePurchaseOrderTax(Integer month, Integer year, PurchaseOrderFilter filter) {

        try {
            ZonedDateTime firstDay = ZonedDateTime.of(year, month, 1, 0, 0, 0, 0, ZoneId.of("Z"));
            ZonedDateTime lastDay = firstDay.with(TemporalAdjusters.lastDayOfMonth());
            lastDay = lastDay.withHour(23).withMinute(59).withSecond(59).withNano(999000000);

            Date lowerTimestamp = Date.from(firstDay.toInstant());
            Date upperTimestamp = Date.from(lastDay.toInstant());

            List<PurchaseOrder> filteredPurchaseOrders = purchaseOrderRepository.findByBuyerUUIDAndDate(filter.getCompanyUUID(), lowerTimestamp, upperTimestamp);
            System.out.println(filteredPurchaseOrders);
            System.out.println("cine te creeeeezi");
        } catch (Exception e) {
            throw new DateTimeException("Invalid date format");
        }

        return 0.0f;
    }
}
