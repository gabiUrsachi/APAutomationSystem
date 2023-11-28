package org.example.unit.invoice.business.discountStrategies;

import org.example.business.discountStrategies.DiscountByAmountStrategy;
import org.example.business.discountStrategies.DiscountStrategy;
import org.example.business.discountStrategies.formulas.DiscountFormulaStrategy;
import org.example.persistence.collections.Invoice;
import org.example.persistence.repository.InvoiceCustomRepository;
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
    InvoiceCustomRepository invoiceCustomRepository;
    @Mock
    DiscountFormulaStrategy discountFormulaStrategy;
    @Mock
    Invoice invoice;
    DiscountStrategy discountStrategy;

    @Before
    public void initialize() {
        this.discountStrategy = new DiscountByAmountStrategy(invoiceCustomRepository, discountFormulaStrategy);
    }

    @Test
    public void returnNullIfThereIsNoPaidAmountForAGivenBuyer() {
        // given
        UUID buyerUUID = generateUUID();
        UUID sellerUUID = generateUUID();

        when(invoice.getBuyerId()).thenReturn(buyerUUID);
        when(invoice.getSellerId()).thenReturn(sellerUUID);

        given(invoiceCustomRepository.getPaidAmountForLastNMonths(eq(buyerUUID), eq(sellerUUID), anyInt())).willReturn(null);

        // when
        Float computedDiscount = this.discountStrategy.computeDiscount(invoice);

        // then
        verify(invoiceCustomRepository).getPaidAmountForLastNMonths(eq(buyerUUID), eq(sellerUUID), anyInt());
        Assertions.assertNull(computedDiscount);
    }

    @Test
    public void returnAValidValueIfPaidInvoicesExistForAGivenBuyer() {
        // given
        UUID buyerUUID = generateUUID();
        UUID sellerUUID = generateUUID();
        Float baseDiscountValue = new Random().nextFloat() * 1000000;

        when(invoice.getBuyerId()).thenReturn(buyerUUID);
        when(invoice.getSellerId()).thenReturn(sellerUUID);

        given(invoiceCustomRepository.getPaidAmountForLastNMonths(eq(buyerUUID), eq(sellerUUID), anyInt())).willReturn(baseDiscountValue);

        // when
        Float computedDiscount = this.discountStrategy.computeDiscount(invoice);

        // then
        verify(invoiceCustomRepository).getPaidAmountForLastNMonths(eq(buyerUUID), eq(sellerUUID), anyInt());
        Assertions.assertNotNull(computedDiscount);
    }

    private UUID generateUUID() {
        return UUID.randomUUID();
    }
}