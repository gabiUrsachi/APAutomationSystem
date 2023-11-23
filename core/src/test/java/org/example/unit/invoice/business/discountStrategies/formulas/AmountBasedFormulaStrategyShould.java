package org.example.unit.invoice.business.discountStrategies.formulas;

import org.example.business.discountStrategies.formulas.AmountBasedFormulaStrategy;
import org.example.business.discountStrategies.formulas.DiscountFormulaStrategy;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AmountBasedFormulaStrategyShould {
    private DiscountFormulaStrategy discountFormulaStrategy;

    private final Float LOW_BASE_VALUE = 10f;

    @Before
    public void initialize() {
        this.discountFormulaStrategy = new AmountBasedFormulaStrategy();
    }


    @Test
    public void return0IfNoDiscountIsApplied() {
        Float computedDiscountRate = this.discountFormulaStrategy.computeDiscountRate(LOW_BASE_VALUE);

        Assertions.assertEquals(0f, computedDiscountRate);
    }

    @Test
    public void return0IfNoBaseValuesAreGiven() {
        Float computedDiscountRate = this.discountFormulaStrategy.computeDiscountRate();

        Assertions.assertEquals(0f, computedDiscountRate);
    }

}