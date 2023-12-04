package org.example.unit.invoice.business.discountStrategies.formulas;

import org.example.business.discountStrategies.formulas.DiscountFormulaStrategy;
import org.example.business.discountStrategies.formulas.ItemsBasedFormulaStrategy;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Random;

@RunWith(MockitoJUnitRunner.class)
public class ItemsBasedFormulaStrategyShould {
    private DiscountFormulaStrategy discountFormulaStrategy;
    Random random;

    @Before
    public void initialize() {
        this.discountFormulaStrategy = new ItemsBasedFormulaStrategy();
        random = new Random();
    }

    @Test
    public void returnDiscountRateWithInverseProportionalityRelationship(){
        // given
        int lowerBaseValue = random.nextInt(100);
        int higherBaseValue = lowerBaseValue + random.nextInt(100) + 1;

        // when
        Float discountRateForLowerBaseValue = discountFormulaStrategy.computeDiscountRate(Float.valueOf(lowerBaseValue));
        Float discountRateForHigherBaseValue = discountFormulaStrategy.computeDiscountRate(Float.valueOf(higherBaseValue));

        // then
        Assertions.assertTrue(higherBaseValue > lowerBaseValue);
        Assertions.assertTrue(discountRateForHigherBaseValue < discountRateForLowerBaseValue);
    }

    @Test
    public void returnZeroIfNoBaseValuesAreGiven() {
        float computedDiscountRate = this.discountFormulaStrategy.computeDiscountRate();

        Assertions.assertEquals(0f, computedDiscountRate);
    }
}


