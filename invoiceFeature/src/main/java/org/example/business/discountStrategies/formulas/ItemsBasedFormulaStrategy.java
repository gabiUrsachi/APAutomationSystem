package org.example.business.discountStrategies.formulas;

/**
 * This discount formula uses inverse proportionality relationship in order to establish
 * the discount rate according to the received base value(s)
 */
public class ItemsBasedFormulaStrategy implements DiscountFormulaStrategy {
    @Override
    public Float computeDiscountRate(Float... baseValues) {
        if (baseValues.length > 0) {
            int numberOfItems = baseValues[0].intValue();

            return 1f / (numberOfItems + 1);
        }

        return 0f;
    }
}
