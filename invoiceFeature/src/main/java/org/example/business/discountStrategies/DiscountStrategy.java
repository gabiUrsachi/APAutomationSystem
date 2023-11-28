package org.example.business.discountStrategies;

import org.example.business.discountStrategies.formulas.DiscountFormulaStrategy;
import org.example.persistence.collections.Invoice;
import org.example.persistence.repository.InvoiceRepository;
import org.springframework.stereotype.Component;

/**
 * This class declares the behaviour which should be applied for different types of discount strategies
 */
@Component
public abstract class DiscountStrategy {
    protected InvoiceRepository invoiceRepository;
    protected DiscountFormulaStrategy discountFormulaStrategy;

    public DiscountStrategy(InvoiceRepository invoiceRepository, DiscountFormulaStrategy discountFormulaStrategy) {
        this.invoiceRepository = invoiceRepository;
        this.discountFormulaStrategy = discountFormulaStrategy;
    }

    /**
     * It computes a discount for a specific customer/buyer
     *
     * @param invoice The document on which the discount will be applied
     * @return The discount rate to be applied
     */
    public abstract Float computeDiscount(Invoice invoice);
}

