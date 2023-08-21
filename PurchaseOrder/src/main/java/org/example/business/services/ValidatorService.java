package org.example.business.services;

import org.example.customexceptions.ForbiddenUpdateException;
import org.example.customexceptions.IdentifiersMismatchException;
import org.example.persistence.utils.OrderStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * This service contains methods to perform validation checks related to identifiers and update permissions
 */
@Service
public class ValidatorService {

    /**
     * Verifies that a user's company UUID matches the requested company UUID.
     *
     * @param userCompanyUUID    the UUID of the company associated with a user
     * @param requestCompanyUUID the UUID of the company in the request
     * @throws IdentifiersMismatchException if the user's company UUID does not match the request company UUID
     */
    public void verifyIdentifiersMatch(UUID userCompanyUUID, UUID requestCompanyUUID) {
        if (!userCompanyUUID.equals(requestCompanyUUID)) {
            throw new IdentifiersMismatchException();
        }
    }

    /**
     * Verifies whether a user has permission to update an order based on the order status and user's company UUID.
     *
     * @param orderStatus       the status of the order being updated
     * @param userCompanyUUID   the UUID of the company associated with the user
     * @param buyerCompanyUUID  the UUID of the buyer company
     * @param sellerCompanyUUID the UUID of the seller company
     * @throws ForbiddenUpdateException if the user does not have permission to update the order
     */
    public void verifyUpdatePermission(OrderStatus orderStatus, UUID userCompanyUUID, UUID buyerCompanyUUID, UUID sellerCompanyUUID) {
        boolean buyerCondition = (orderStatus == OrderStatus.CREATED || orderStatus == OrderStatus.SAVED)
                &&
                userCompanyUUID.equals(buyerCompanyUUID);

        boolean sellerCondition = (orderStatus == OrderStatus.APPROVED || orderStatus == OrderStatus.REJECTED)
                &&
                userCompanyUUID.equals(sellerCompanyUUID);

        if (!(buyerCondition || sellerCondition)) {
            throw new ForbiddenUpdateException();
        }
    }

}
