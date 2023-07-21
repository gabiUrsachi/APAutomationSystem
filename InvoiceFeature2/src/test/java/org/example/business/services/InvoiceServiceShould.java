package org.example.business.services;

import org.example.business.errorhandling.customexceptions.OrderNotFoundException;
import org.example.business.models.InvoiceDTO;
import org.example.business.models.OrderRequestDTO;
import org.example.persistence.collections.Invoice;
import org.example.persistence.collections.PurchaseOrder;
import org.example.persistence.repository.InvoiceRepository;
import org.example.persistence.repository.PurchaseOrderRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class InvoiceServiceShould {
    @Mock MapperService mapperService;
    @Mock
    InvoiceRepository invoiceRepository;
    @Mock
    InvoiceDTO invoiceDTO;
    @Mock
    Invoice invoice;
    InvoiceService invoiceService;

    @Before
    public void initialize(){
        invoiceService = new InvoiceService(invoiceRepository,mapperService);
    }

    @Test
    public void storeInvoiceFromBody(){
        given(mapperService.mapToEntity(invoiceDTO)).willReturn(invoice);

        invoiceService.createInvoice(invoiceDTO);

        verify(invoiceRepository).insert(invoice);
    }

}