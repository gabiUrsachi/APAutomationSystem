package org.example.unit.invoice.business.discountStrategies;

import org.example.business.discountStrategies.DiscountByAmountStrategy;
import org.example.business.discountStrategies.DiscountStrategy;
import org.example.business.discountStrategies.formulas.DiscountFormulaStrategy;
import org.example.persistence.repository.InvoiceCustomRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Random;
import java.util.UUID;

import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class DiscountByAmountStrategyShould {

    @Mock
    InvoiceCustomRepository invoiceCustomRepository;

    @Mock
    DiscountFormulaStrategy discountFormulaStrategy;

    DiscountStrategy discountStrategy;

    @Before
    public void initialize() {
        this.discountStrategy = new DiscountByAmountStrategy(invoiceCustomRepository, discountFormulaStrategy);
    }

    @Test
    public void returnNullIfThereIsNoPaidAmountForAGivenBuyer() {
        UUID buyerUUID = generateUUID();
        UUID sellerUUID = generateUUID();
        int monthsNumber = 3;

        given(invoiceCustomRepository.getPaidAmountForLastNMonths(buyerUUID, sellerUUID, monthsNumber)).willReturn(null);

        Float computedDiscount = this.discountStrategy.computeDiscount(buyerUUID, sellerUUID);

        Assertions.assertNull(computedDiscount);
    }

    @Test
    public void returnAValidValueIfPaidInvoicesExistForAGivenBuyer() {
        UUID buyerUUID = generateUUID();
        UUID sellerUUID = generateUUID();
        int monthsNumber = 3;
        Float baseDiscountValue = new Random().nextFloat() * 1000000;

        given(invoiceCustomRepository.getPaidAmountForLastNMonths(buyerUUID, sellerUUID, monthsNumber)).willReturn(baseDiscountValue);

        Float computedDiscount = this.discountStrategy.computeDiscount(buyerUUID, sellerUUID);

        Assertions.assertNotNull(computedDiscount);
    }

    private UUID generateUUID(){
        return UUID.randomUUID();
    }
}