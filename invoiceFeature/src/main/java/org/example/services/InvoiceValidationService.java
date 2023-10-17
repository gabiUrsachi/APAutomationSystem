package org.example.services;

import org.example.customexceptions.ForbiddenActionException;
import org.example.persistence.utils.InvoiceStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * This service contains methods to perform validation checks related to identifiers and update permissions
 */
@Service
public class InvoiceValidationService {

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

    public void verifyUpdatePermission(InvoiceStatus invoiceStatus, UUID userCompanyUUID, UUID buyerCompanyUUID, UUID sellerCompanyUUID) {

        boolean buyerCondition = (invoiceStatus == InvoiceStatus.CREATED || invoiceStatus == InvoiceStatus.PAID)
                &&
                userCompanyUUID.equals(buyerCompanyUUID);

        boolean sellerCondition = (invoiceStatus == InvoiceStatus.SENT )
                &&
                userCompanyUUID.equals(sellerCompanyUUID);

        if (!(buyerCondition || sellerCondition)) {
            throw new ForbiddenActionException();
        }
    }

}
