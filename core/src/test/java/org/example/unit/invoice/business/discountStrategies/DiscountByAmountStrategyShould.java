package org.example.unit.invoice.business.discountStrategies;

import org.example.business.discountStrategies.DiscountByAmountStrategy;
import org.example.business.discountStrategies.DiscountStrategy;
import org.example.business.discountStrategies.formulas.DiscountFormulaStrategy;
import org.example.persistence.collections.Invoice;
import org.example.persistence.repository.InvoiceRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Random;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DiscountByAmountStrategyShould {
    @Mock
    InvoiceRepository invoiceRepository;
    @Mock
    DiscountFormulaStrategy discountFormulaStrategy;
    @Mock
    Invoice invoice;
    DiscountStrategy discountStrategy;

    @Before
    public void initialize() {
        this.discountStrategy = new DiscountByAmountStrategy(invoiceRepository, discountFormulaStrategy);
    }

    @Test
    public void return0IfThereIsNoPaidAmountForAGivenBuyer() {
        // given
        UUID buyerUUID = generateUUID();
        UUID sellerUUID = generateUUID();

        when(invoice.getBuyerId()).thenReturn(buyerUUID);
        when(invoice.getSellerId()).thenReturn(sellerUUID);

        given(invoiceRepository.getPaidAmountForLastNMonths(eq(buyerUUID), eq(sellerUUID), anyInt())).willReturn(0f);

        // when
        Float computedDiscount = this.discountStrategy.computeDiscount(invoice);

        // then
        verify(invoiceRepository).getPaidAmountForLastNMonths(eq(buyerUUID), eq(sellerUUID), anyInt());
        Assertions.assertEquals(0f, computedDiscount);
    }

    @Test
    public void returnAValidValueIfPaidInvoicesExistForAGivenBuyer() {
        // given
        UUID buyerUUID = generateUUID();
        UUID sellerUUID = generateUUID();
        Float baseDiscountValue = new Random().nextFloat() * 1000000;

        when(invoice.getBuyerId()).thenReturn(buyerUUID);
        when(invoice.getSellerId()).thenReturn(sellerUUID);

        given(invoiceRepository.getPaidAmountForLastNMonths(eq(buyerUUID), eq(sellerUUID), anyInt())).willReturn(baseDiscountValue);

        // when
        Float computedDiscount = this.discountStrategy.computeDiscount(invoice);

        // then
        verify(invoiceRepository).getPaidAmountForLastNMonths(eq(buyerUUID), eq(sellerUUID), anyInt());
        Assertions.assertNotNull(computedDiscount);
    }

    private UUID generateUUID() {
        return UUID.randomUUID();
    }
}