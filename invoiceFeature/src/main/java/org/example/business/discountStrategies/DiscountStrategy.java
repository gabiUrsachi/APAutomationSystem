package org.example.business.discountStrategies;

import org.example.business.discountStrategies.formulas.DiscountFormulaStrategy;
import org.example.persistence.repository.InvoiceCustomRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * This class declares the behaviour which should be applied for different types of discount strategies
 */
@Component
public abstract class DiscountStrategy {
    protected InvoiceCustomRepository invoiceRepository;
    protected DiscountFormulaStrategy discountFormulaStrategy;

    public DiscountStrategy(InvoiceCustomRepository invoiceRepository, DiscountFormulaStrategy discountFormulaStrategy) {
        this.invoiceRepository = invoiceRepository;
        this.discountFormulaStrategy = discountFormulaStrategy;
    }

    /**
     * It computes a discount for a specific customer/buyer
     *
     * @param buyerUUID The buyer company which will receive the discount
     * @param sellerUUID The seller company which will applies the discount
     * @return The discount rate to be applied
     */
    public abstract Float computeDiscount(UUID buyerUUID, UUID sellerUUID);
}

