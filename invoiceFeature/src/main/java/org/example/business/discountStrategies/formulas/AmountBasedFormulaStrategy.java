package org.example.business.discountStrategies.formulas;

import org.springframework.stereotype.Component;

/**
 * This discount formula uses some threshold values in order to establish the discount
 * rate according to the received base value(s)
 */
@Component
public class AmountBasedFormulaStrategy implements DiscountFormulaStrategy {
    @Override
    public Float computeDiscountRate(Float... baseValues) {
        if(baseValues.length > 0){
            Float amountValue = baseValues[0];

            if (amountValue >= 30000) {
                return 0.3f;
            }
            if (amountValue >= 20000) {
                return 0.2f;
            }
            if (amountValue >= 10000) {
                return 0.1f;
            }
        }

        return 0f;
    }
}
