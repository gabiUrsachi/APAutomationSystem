package org.example.unit.invoice.persistence.utils;

import org.example.persistence.utils.InvoiceStatus;
import org.example.persistence.utils.InvoiceStatusHistoryHelper;
import org.example.persistence.utils.data.InvoiceStatusHistoryObject;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RunWith(MockitoJUnitRunner.class)
public class InvoiceStatusHistoryHelperShould {

    @Test
    public void returnNullForEmptyStatusHistory() {
        InvoiceStatusHistoryObject statusHistoryObject = InvoiceStatusHistoryHelper.getMostRecentHistoryObject(List.of());

        Assertions.assertNull(statusHistoryObject);
    }

    @Test
    public void returnTheMostRecentHistoryObjectByDate() {
        // given
        LocalDateTime mostRecentDate = LocalDateTime.now();
        Map<LocalDateTime, InvoiceStatusHistoryObject> invoiceStatusHistoryObjectsMap = initStatusHistoryMap(mostRecentDate);

        // when
        InvoiceStatusHistoryObject mostRecentStatusHistoryObject = InvoiceStatusHistoryHelper.getMostRecentHistoryObject(new ArrayList<>(invoiceStatusHistoryObjectsMap.values()));

        // then
        Assertions.assertEquals(invoiceStatusHistoryObjectsMap.get(mostRecentDate), mostRecentStatusHistoryObject);
    }

    @Test
    public void successfullyInitStatusHistory(){
        // given
        InvoiceStatus invoiceStatus = createRandomInvoiceStatus();

        // when
        List<InvoiceStatusHistoryObject> invoiceStatusHistory = InvoiceStatusHistoryHelper.initStatusHistory(invoiceStatus);

        // then
        Assertions.assertEquals(1, invoiceStatusHistory.size());
        Assertions.assertEquals(invoiceStatus, invoiceStatusHistory.get(0).getStatus());
    }

    private Map<LocalDateTime, InvoiceStatusHistoryObject> initStatusHistoryMap(LocalDateTime mostRecentDate){
        return  Map.of(
                mostRecentDate.minusMonths(2), createRandomStatusHistoryObject(mostRecentDate.minusMonths(2)),
                mostRecentDate, createRandomStatusHistoryObject(mostRecentDate),
                mostRecentDate.minusDays(12), createRandomStatusHistoryObject(mostRecentDate.minusDays(12))
        );
    }

    private InvoiceStatusHistoryObject createRandomStatusHistoryObject(LocalDateTime date){
        return InvoiceStatusHistoryObject.builder()
                .date(date)
                .status(createRandomInvoiceStatus())
                .build();
    }

    private InvoiceStatus createRandomInvoiceStatus(){
        Random random = new Random();
        InvoiceStatus[] invoiceStatuses = InvoiceStatus.values();

        return invoiceStatuses[random.nextInt(invoiceStatuses.length)];
    }
}