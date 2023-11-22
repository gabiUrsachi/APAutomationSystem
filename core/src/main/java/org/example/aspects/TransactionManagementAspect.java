package org.example.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.customexceptions.ResourceReferenceException;
import org.example.persistence.collections.Invoice;
import org.example.persistence.collections.PurchaseOrder;
import org.example.persistence.collections.User;
import org.example.persistence.repository.InvoiceRepository;
import org.example.persistence.repository.PurchaseOrderRepository;
import org.example.persistence.repository.UserRepository;
import org.example.utils.ErrorMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@Aspect
public class TransactionManagementAspect {

    @Autowired
    InvoiceRepository invoiceRepository;

    @Autowired
    PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    UserRepository userRepository;

    @Around("execution(* org.example.persistence.repository.CompanyRepository.deleteById(..))")
    public void deleteCompany(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        UUID companyIdentifier = (UUID) args[0];

        checkUserReference(companyIdentifier);
        checkInvoiceReference(companyIdentifier);
        checkPurchaseOrderReference(companyIdentifier);

        joinPoint.proceed();
    }

    private void checkUserReference(UUID companyIdentifier){
        Optional<User> optionalUser = userRepository.findFirstByCompanyIdentifier(companyIdentifier);
        boolean isUserPresent = optionalUser.isPresent();

        if (isUserPresent) {
            throw new ResourceReferenceException(ErrorMessages.RESOURCE_REFERENCE);
        }
    }

    private void checkInvoiceReference(UUID companyIdentifier){
        Optional<Invoice> optionalInvoice = invoiceRepository.findFirstByBuyerIdOrSellerId(companyIdentifier, companyIdentifier);
        boolean isInvoicePresent = optionalInvoice.isPresent();

        if (isInvoicePresent) {
            throw new ResourceReferenceException(ErrorMessages.RESOURCE_REFERENCE);
        }
    }

    private void checkPurchaseOrderReference(UUID companyIdentifier){
        Optional<PurchaseOrder> optionalOrder = purchaseOrderRepository.findFirstByBuyerOrSeller(companyIdentifier, companyIdentifier);
        boolean isOrderPresent = optionalOrder.isPresent();

        if (isOrderPresent) {
            throw new ResourceReferenceException(ErrorMessages.RESOURCE_REFERENCE);
        }
    }
}
