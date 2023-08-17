package org.example.services;

import org.example.business.exceptions.InvoiceNotFoundException;
import org.example.business.models.InvoiceDTO;
import org.example.presentation.view.OrderResponseDTO;
import org.example.persistence.collections.Invoice;
import org.example.persistence.repository.InvoiceRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class InvoiceServiceShould {
    @Mock
    InvoiceMapperService mapperService;

    @Mock
    InvoiceRepository invoiceRepository;
    @Mock
    InvoiceDTO invoiceDTO;
    @Mock
    Invoice invoice;
    @Mock
    OrderResponseDTO orderResponseDTO;
    InvoiceService invoiceService;

    @Before
    public void initialize() {
        invoiceService = new InvoiceService(invoiceRepository);
    }

//    @Test
//    public void createInvoiceFromBody() {
//        given(mapperService.mapToEntity(invoiceDTO)).willReturn(invoice);
//
//        invoiceService.createInvoice(invoiceDTO);
//
//        verify(invoiceRepository).insert(invoice);
//    }
//
//
//    @Test
//    public void createInvoiceFromPO(){
//
//        given(invoiceService.createInvoiceDTOFromPurchaseOrder(orderResponseDTO)).willReturn(invoiceDTO);
//
//        given(mapperService.mapToEntity(invoiceDTO)).willReturn(invoice);
//
//        invoiceService.createInvoice(invoiceDTO);
//
//        verify(invoiceRepository).insert(invoice);
//    }
//

    @Test
    public void notReturnNonExistingInvoice() {
        UUID uuid = UUID.randomUUID();

        assertThrows(InvoiceNotFoundException.class, () ->invoiceService.getInvoice(uuid));

        given(invoiceRepository.findByIdentifier(uuid)).willReturn(Optional.empty());

    }

    @Test
    public void deleteInvoice() {

        given(mapperService.mapToEntity(invoiceDTO)).willReturn(invoice);

        invoiceService.deleteInvoice(invoice.getIdentifier());

        verify(invoiceRepository).deleteByIdentifier(invoice.getIdentifier());

    }
}