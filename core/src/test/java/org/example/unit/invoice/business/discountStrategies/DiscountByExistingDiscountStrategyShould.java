package org.example.unit.invoice.business.discountStrategies;

import org.example.business.discountStrategies.DiscountByExistingDiscountStrategy;
import org.example.business.discountStrategies.DiscountStrategy;
import org.example.business.discountStrategies.formulas.DiscountFormulaStrategy;
import org.example.persistence.collections.Invoice;
import org.example.persistence.repository.InvoiceRepository;
import org.example.persistence.utils.InvoiceHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DiscountByExistingDiscountStrategyShould {
    @Mock
    InvoiceRepository invoiceRepository;
    @Mock
    DiscountFormulaStrategy discountFormulaStrategy;
    @Mock
    Invoice invoice;
    DiscountStrategy discountStrategy;
    Random random;

    static MockedStatic<InvoiceHelper> invoiceHelperMockedStatic;

    @Before
    public void initialize() {
        this.discountStrategy = new DiscountByExistingDiscountStrategy(invoiceRepository, discountFormulaStrategy);
        random = new Random();
        invoiceHelperMockedStatic = Mockito.mockStatic(InvoiceHelper.class);
    }

    @After
    public void clean(){
        invoiceHelperMockedStatic.close();
    }

    @Test
    public void returnNullIfStrategyCannotBeAppliedDueToExistingDiscountLowerThanLimit() {
        // given
        List<Invoice> invoices = new ArrayList<>();

        UUID buyerUUID = generateUUID();
        UUID sellerUUID = generateUUID();

        when(invoice.getBuyerId()).thenReturn(buyerUUID);
        when(invoice.getSellerId()).thenReturn(sellerUUID);

        invoiceHelperMockedStatic.when(() -> InvoiceHelper.computeTotalDiscountedAmount(invoices)).thenReturn(Float.NEGATIVE_INFINITY);

        // when
        Float computedDiscount = this.discountStrategy.computeDiscount(invoice);

        // then
        invoiceHelperMockedStatic.verify(() -> InvoiceHelper.computeTotalDiscountedAmount(invoices));
        Assertions.assertNull(computedDiscount);
    }

    @Test
    public void returnComputedDiscountRateIfStrategyIsApplied() {
        // given
        List<Invoice> invoices = new ArrayList<>();
        int totalNumberOfItems = random.nextInt(10);
        Float expectedDiscountRate = random.nextFloat();

        UUID buyerUUID = generateUUID();
        UUID sellerUUID = generateUUID();

        when(invoice.getBuyerId()).thenReturn(buyerUUID);
        when(invoice.getSellerId()).thenReturn(sellerUUID);

        invoiceHelperMockedStatic.when(() -> InvoiceHelper.computeTotalDiscountedAmount(invoices)).thenReturn(Float.POSITIVE_INFINITY);
        invoiceHelperMockedStatic.when(() -> InvoiceHelper.computeTotalNumberOfItems(invoices)).thenReturn(totalNumberOfItems);
        when(discountFormulaStrategy.computeDiscountRate(Float.valueOf(totalNumberOfItems))).thenReturn(expectedDiscountRate);

        // when
        Float computedDiscount = this.discountStrategy.computeDiscount(invoice);

        // then
        invoiceHelperMockedStatic.verify(() -> InvoiceHelper.computeTotalDiscountedAmount(invoices));
        invoiceHelperMockedStatic.verify(() -> InvoiceHelper.computeTotalNumberOfItems(invoices));
        verify(discountFormulaStrategy).computeDiscountRate(Float.valueOf(totalNumberOfItems));

        Assertions.assertNotNull(computedDiscount);
        Assertions.assertEquals(expectedDiscountRate, computedDiscount);
    }

    private UUID generateUUID() {
        return UUID.randomUUID();
    }


}
