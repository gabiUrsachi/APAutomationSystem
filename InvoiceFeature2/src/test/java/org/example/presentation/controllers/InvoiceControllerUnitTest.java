package org.example.presentation.controllers;

import org.example.business.models.InvoiceDDO;
import org.example.business.models.InvoiceDPO;
import org.example.business.services.InvoiceMapperService;
import org.example.business.services.InvoiceService;
import org.example.persistence.collections.Invoice;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InvoiceControllerUnitTest {



    @Test
    public void invoiceControllerCallsService() {

        //given
        InvoiceService invoiceService = mock(InvoiceService.class);
        InvoiceMapperService invoiceMapperService = mock(InvoiceMapperService.class);
        InvoiceController invoiceController = new InvoiceController(invoiceService, invoiceMapperService);
        given(invoiceService.getInvoices()).willReturn(invoiceList);

        //when
        invoiceController.getInvoices();

        //then
        verify(invoiceMapperService).mapToDDO(invoiceList);

    }

//    @Test
//    public void invoiceControllerConvertsObjectProperly(){
//
//        //given
//        InvoiceService invoiceService = mock(InvoiceService.class);
//        InvoiceMapperService invoiceMapperService = mock(InvoiceMapperService.class);
//        InvoiceController invoiceController = new InvoiceController(invoiceService, invoiceMapperService);
//        invoiceList = new ArrayList<>();
//        Invoice invoice = new Invoice();
//        invoiceList.add(invoice);
//        //given(invoiceService.getInvoices()).willReturn(invoiceList);
//
//        //when
//
//        //then
//        verify(invoiceMapperService).mapToDDO(invoiceList);
//
//    }

//    @Test
//    public void invoiceControllerConvertsToDDO() {
//
//
//        InvoiceDPO invoiceDPO = mock(InvoiceDPO.class);
//
//        InvoiceService invoiceService = mock(InvoiceService.class);
//        InvoiceMapperService invoiceMapperService = mock(InvoiceMapperService.class);
//
//        InvoiceController invoiceController = new InvoiceController(invoiceService, invoiceMapperService);
//
//        given(invoiceMapperService.mapToEntity(invoiceDPO)).willReturn(invoice);
//
//        invoiceController.getInvoices();
//
//    }

}