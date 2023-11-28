package org.example.unit.invoice.business.services;

import org.example.business.discountStrategies.DiscountByAmountStrategy;
import org.example.business.discountStrategies.DiscountByExistingDiscountStrategy;
import org.example.business.discountStrategies.DiscountStrategy;
import org.example.business.services.InvoiceDiscountService;
import org.example.persistence.collections.Invoice;
import org.example.persistence.repository.InvoiceRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.LinkedList;
import java.util.Random;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InvoiceDiscountServiceShould {
    @Spy
    @InjectMocks
    InvoiceDiscountService invoiceDiscountService;

    Invoice invoice;

    @Before
    public void initialize() {
        this.invoice = Invoice.builder().build();
    }

    @Test
    public void returnUnmodifiedNullInvoiceDiscountIfNoStrategyMatches() {
        // given
        DiscountStrategy discountStrategy1 = Mockito.mock(DiscountByAmountStrategy.class);
        DiscountStrategy discountStrategy2 = Mockito.mock(DiscountByExistingDiscountStrategy.class);

        LinkedList<DiscountStrategy> discountStrategies = new LinkedList<>();
        discountStrategies.add(discountStrategy1);
        discountStrategies.add(discountStrategy2);

        when(invoiceDiscountService.buildChainOfDiscountStrategies()).thenReturn(discountStrategies);
        when(discountStrategy1.computeDiscount(invoice)).thenReturn(null);
        when(discountStrategy2.computeDiscount(invoice)).thenReturn(null);

        // when
        Invoice updatedInvoice = invoiceDiscountService.applyDiscount(invoice);

        // then
        verify(invoiceDiscountService).buildChainOfDiscountStrategies();

        Assertions.assertEquals(invoice.getDiscountRate(), updatedInvoice.getDiscountRate());
        Assertions.assertNull(updatedInvoice.getDiscountRate());
    }

    @Test
    public void returnInvoiceWithDiscountOfFirstMatchingStrategyIfAny() {
        // given
        Float discountValue = new Random().nextFloat();

        DiscountStrategy discountStrategy1 = Mockito.mock(DiscountByAmountStrategy.class);
        DiscountStrategy discountStrategy2 = Mockito.mock(DiscountByExistingDiscountStrategy.class);

        LinkedList<DiscountStrategy> discountStrategies = new LinkedList<>();
        discountStrategies.add(discountStrategy1);
        discountStrategies.add(discountStrategy2);

        when(invoiceDiscountService.buildChainOfDiscountStrategies()).thenReturn(discountStrategies);
        when(discountStrategy1.computeDiscount(invoice)).thenReturn(null);
        when(discountStrategy2.computeDiscount(invoice)).thenReturn(discountValue);

        // when
        Invoice updatedInvoice = invoiceDiscountService.applyDiscount(invoice);

        // then
        verify(invoiceDiscountService).buildChainOfDiscountStrategies();

        Assertions.assertNull(invoice.getDiscountRate());
        Assertions.assertEquals(discountValue, updatedInvoice.getDiscountRate());
    }


}