package org.example.unit.invoice.persistence.utils;

import org.example.SQSOps;
import org.example.business.services.PurchaseOrderService;
import org.example.business.utils.PurchaseOrderHistoryHelper;
import org.example.business.utils.PurchaseOrderStatusPrecedence;
import org.example.customexceptions.InvalidResourceUpdateException;
import org.example.customexceptions.ResourceNotFoundException;
import org.example.persistence.collections.PurchaseOrder;
import org.example.persistence.repository.PurchaseOrderRepository;
import org.example.persistence.utils.data.OrderHistoryObject;
import org.example.persistence.utils.data.OrderStatus;
import org.example.persistence.utils.data.PurchaseOrderFilter;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.*;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
class InvoiceHelperShould {

    @Test
    void createFiltersBasedAggregators() {
    }

    @Test
    void createPaidAmountOverNMonthsAggregators() {
    }

    @Test
    void createPagingAggregators() {
    }
}