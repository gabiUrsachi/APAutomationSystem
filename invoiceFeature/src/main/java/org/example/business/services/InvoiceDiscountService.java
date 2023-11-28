package org.example.business.services;

import org.example.business.discountStrategies.DiscountByAmountStrategy;
import org.example.business.discountStrategies.DiscountByExistingDiscountStrategy;
import org.example.business.discountStrategies.DiscountStrategy;
import org.example.business.discountStrategies.formulas.AmountBasedFormulaStrategy;
import org.example.business.discountStrategies.formulas.ItemsBasedFormulaStrategy;
import org.example.persistence.collections.Invoice;
import org.example.persistence.repository.InvoiceRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

@Service
public class InvoiceDiscountService {
    private InvoiceRepository invoiceRepository;

    public InvoiceDiscountService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    /**
     * This method is responsible for updating an invoice by adding a new field
     * related to the discount rate which should be applied to the total amount
     *
     * @param invoice document to be updated
     * @return updated document
     */
    public Invoice applyDiscount(Invoice invoice) {
        Invoice updatedInvoice = copyInvoice(invoice);

        LinkedList<DiscountStrategy> discountStrategies = buildChainOfDiscountStrategies();

        for(DiscountStrategy discountStrategy: discountStrategies){
            Float discountRate = discountStrategy.computeDiscount(invoice);

            if(discountRate != null){
                updatedInvoice.setDiscountRate(discountRate);
                return updatedInvoice;
            }
        }

        return updatedInvoice;
    }

    public LinkedList<DiscountStrategy> buildChainOfDiscountStrategies(){
        LinkedList<DiscountStrategy> discountStrategies = new LinkedList<>();

        discountStrategies.addLast(new DiscountByExistingDiscountStrategy(this.invoiceRepository, new ItemsBasedFormulaStrategy()));
        discountStrategies.addLast(new DiscountByAmountStrategy(this.invoiceRepository, new AmountBasedFormulaStrategy()));

        return discountStrategies;
    }

    private Invoice copyInvoice(Invoice invoice){

        return Invoice.builder()
                .identifier(invoice.getIdentifier())
                .buyerId(invoice.getBuyerId())
                .sellerId(invoice.getSellerId())
                .items(invoice.getItems())
                .statusHistory(invoice.getStatusHistory())
                .totalAmount(invoice.getTotalAmount())
                .version(invoice.getVersion())
                .uri(invoice.getUri())
                .build();
    }


}
