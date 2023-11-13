package org.example;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class DatabaseOperationAspect {
    private static final Logger logger = LoggerFactory.getLogger("analytics");

    @Around("execution(* org.example.persistence.repository.InvoiceRepository.*(..)) " +
            "|| execution(* org.example.persistence.repository.PurchaseOrderRepository.*(..))")
    public Object logDBOperationExecTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long finishTime = System.currentTimeMillis();
        long executionTime = finishTime - startTime;

        String operation = joinPoint.getSignature().toShortString();
        logger.info("[Operation: {}], Execution time: {} ms", operation, executionTime);

        return result;
    }
}
