package org.example.business.discountStrategies;

import org.example.business.discountStrategies.formulas.DiscountFormulaStrategy;
import org.example.persistence.collections.Invoice;
import org.example.persistence.repository.InvoiceRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * This class handles the situation when discount is applied based on the
 * paid amount over the last 3 months
 */
@Component
public class DiscountByAmountStrategy extends DiscountStrategy {
    private final int MONTHS_NUMBER = 3;

    public DiscountByAmountStrategy(InvoiceRepository invoiceRepository, DiscountFormulaStrategy discountFormulaStrategy) {
        super(invoiceRepository, discountFormulaStrategy);
    }

    @Override
    public Float computeDiscount(Invoice invoice) {
        UUID buyerUUID = invoice.getBuyerId();
        UUID sellerUUID = invoice.getSellerId();

        Float paidAmountForLast3Months = this.invoiceRepository.getPaidAmountForLastNMonths(buyerUUID, sellerUUID, MONTHS_NUMBER);

        return this.discountFormulaStrategy.computeDiscountRate(paidAmountForLast3Months);
    }
}
