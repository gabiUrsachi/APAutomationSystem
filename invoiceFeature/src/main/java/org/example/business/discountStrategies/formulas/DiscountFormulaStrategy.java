package org.example.business.discountStrategies.formulas;

import org.springframework.stereotype.Component;

/**
 * This class declares the general structure of different discount formulas
 */
@Component
public interface DiscountFormulaStrategy {
    Float computeDiscountRate(Float... baseValues);
}
