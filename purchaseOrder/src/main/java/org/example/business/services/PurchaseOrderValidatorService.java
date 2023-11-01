package org.example.business.services;

import org.example.customexceptions.ForbiddenActionException;
import org.example.persistence.utils.data.OrderHistoryObject;
import org.example.persistence.utils.data.OrderStatus;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.example.business.utils.PurchaseOrderHistoryHelper.getLastestOrderHistoryObject;

/**
 * This service contains methods to perform validation checks related to identifiers and update permissions
 */
@Service
public class PurchaseOrderValidatorService {

    /**
     * Verifies that a user's company UUID matches the requested company UUID.
     *
     * @param userCompanyUUID    the UUID of the company associated with a user
     * @param requestCompanyUUID the UUID of the company in the request
     * @throws ForbiddenActionException if the user's company UUID does not match the request company UUID
     */
    public void verifyIdentifiersMatch(UUID userCompanyUUID, UUID requestCompanyUUID) {
        if (!userCompanyUUID.equals(requestCompanyUUID)) {
            throw new ForbiddenActionException();
        }
    }

    /**
     * Verifies whether a user has permission to update an order based on the order status and user's company UUID.
     *
     * @param orderHistory      the set of the order history objects being updated
     * @param userCompanyUUID   the UUID of the company associated with the user
     * @param buyerCompanyUUID  the UUID of the buyer company
     * @param sellerCompanyUUID the UUID of the seller company
     * @throws ForbiddenActionException if the user does not have permission to update the order
     */
    public void verifyUpdatePermission(List<OrderHistoryObject> orderHistory, UUID userCompanyUUID, UUID buyerCompanyUUID, UUID sellerCompanyUUID) {

        OrderHistoryObject lastestOrderHistoryObject = getLastestOrderHistoryObject(orderHistory);
        boolean buyerCondition = (lastestOrderHistoryObject.getStatus() == OrderStatus.CREATED || lastestOrderHistoryObject.getStatus() == OrderStatus.SAVED)
                &&
                userCompanyUUID.equals(buyerCompanyUUID);

        boolean sellerCondition = (lastestOrderHistoryObject.getStatus() == OrderStatus.APPROVED || lastestOrderHistoryObject.getStatus() == OrderStatus.REJECTED)
                &&
                userCompanyUUID.equals(sellerCompanyUUID);

        if (!(buyerCondition || sellerCondition)) {
            throw new ForbiddenActionException();
        }
    }

}
