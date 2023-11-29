package org.example.business.discountStrategies;

import org.example.business.discountStrategies.formulas.DiscountFormulaStrategy;
import org.example.persistence.collections.Invoice;
import org.example.persistence.repository.InvoiceRepository;
import org.example.persistence.utils.InvoiceHelper;

import java.util.List;
import java.util.UUID;

public class DiscountByExistingDiscountStrategy extends DiscountStrategy{
    private final Float MAX_LIMIT_OF_EXISTING_DISCOUNT = 10f;

    public DiscountByExistingDiscountStrategy(InvoiceRepository invoiceRepository, DiscountFormulaStrategy discountFormulaStrategy) {
        super(invoiceRepository, discountFormulaStrategy);
    }

    //TODO add comments to all new methods
    @Override
    public Float computeDiscount(Invoice invoice) {
        UUID buyerUUID = invoice.getBuyerId();
        UUID sellerUUID = invoice.getSellerId();

        List<Invoice> lastMonthPaidInvoices = this.invoiceRepository.findLastMonthPaidInvoicesByBuyerUUIDAndSellerUUID(buyerUUID, sellerUUID);
        Float totalDiscountedAmount = InvoiceHelper.computeTotalDiscountedAmount(lastMonthPaidInvoices);

        if(totalDiscountedAmount >= MAX_LIMIT_OF_EXISTING_DISCOUNT){
            Integer numberOfItems = InvoiceHelper.computeTotalNumberOfItems(lastMonthPaidInvoices);
            return this.discountFormulaStrategy.computeDiscountRate(Float.valueOf(numberOfItems));
        }

        return null;
    }
}
