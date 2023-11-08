package org.example.business.discountStrategies.formulas;

import org.springframework.stereotype.Component;

@Component
public interface DiscountFormulaStrategy {
    Float computeDiscountRate(Float... baseValues);
}
