package org.example.business.discountStrategies;

import org.example.business.discountStrategies.formulas.DiscountFormulaStrategy;
import org.example.persistence.repository.InvoiceCustomRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * This class handles the situation when discount is applied based on the
 * paid amount over the last 3 months
 */
@Component
public class DiscountByAmountStrategy extends DiscountStrategy {
    private final int MONTHS_NUMBER;

    public DiscountByAmountStrategy(InvoiceCustomRepository invoiceRepository, DiscountFormulaStrategy discountFormulaStrategy) {
        super(invoiceRepository, discountFormulaStrategy);
        MONTHS_NUMBER = 3;
    }

    @Override
    public Float computeDiscount(UUID buyerUUID) {
        Float paidAmountForLast3Months = this.invoiceRepository.getPaidAmountForLastNMonths(buyerUUID, MONTHS_NUMBER);

        return this.discountFormulaStrategy.computeDiscountRate(paidAmountForLast3Months);
    }
}
