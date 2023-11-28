package org.example.business.discountStrategies;

import org.example.business.discountStrategies.formulas.DiscountFormulaStrategy;
import org.example.persistence.collections.Invoice;
import org.example.persistence.repository.InvoiceRepository;

import java.util.UUID;

public class DiscountByExistingDiscountStrategy extends DiscountStrategy{
    private final Float MAX_LIMIT;

    public DiscountByExistingDiscountStrategy(InvoiceRepository invoiceRepository, DiscountFormulaStrategy discountFormulaStrategy) {
        super(invoiceRepository, discountFormulaStrategy);
        MAX_LIMIT = 10f;
    }

    @Override
    public Float computeDiscount(Invoice invoice) {
        UUID buyerUUID = invoice.getBuyerId();
        UUID sellerUUID = invoice.getSellerId();

        //TODO calcul discount cumulat pt invoice-urile platite in ultima luna
        //Float appliedDiscountForLastMonth

        //TODO calcul nr total de items al invoice-urilor platite in ultima luna
        //Integer numberOfItems = ...

        return null;

    }
}
