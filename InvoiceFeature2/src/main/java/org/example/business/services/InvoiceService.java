package org.example.business.services;

import org.example.business.models.OrderResponseDTO;
import org.example.business.models.InvoiceDTO;
import org.example.persistence.collections.Invoice;
import org.example.persistence.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

@Service
public class InvoiceService {
    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private CompanyOpsService companyOpsService;

    public InvoiceDTO createInvoice(@RequestBody InvoiceDTO invoiceDTO) {

        Invoice invoice = mapToEntity(invoiceDTO);
        Invoice responseInvoice = invoiceRepository.insert(invoice);

        return mapToDTO(responseInvoice);
    }
    public List<Invoice> getInvoices(){
        return invoiceRepository.findAll();
    }

    public InvoiceDTO createInvoiceFromPurchaseOrder(OrderResponseDTO orderResponseDTO) {

        return InvoiceDTO.builder()
                .buyer(orderResponseDTO.getBuyer())
                .seller(orderResponseDTO.getSeller())
                .items(orderResponseDTO.getItems())
                .build();
    }

    public Optional<Invoice> getInvoice(UUID identifier) {

        return Optional.ofNullable(invoiceRepository.findByIdentifier(identifier));

    }

    public void deleteInvoice(UUID identifier) {
       invoiceRepository.deleteByIdentifier(identifier);
    }

    private Invoice mapToEntity(InvoiceDTO invoiceDTO) {

        UUID invoiceIdentifier = UUID.randomUUID();
        return Invoice.builder()
                .identifier(invoiceIdentifier)
                .buyer(companyOpsService.mapToEntity(invoiceDTO.getBuyer()))
                .seller(companyOpsService.mapToEntity(invoiceDTO.getSeller()))
                .items(invoiceDTO.getItems())
                .build();

    }

    private InvoiceDTO mapToDTO(Invoice invoice) {

        return InvoiceDTO.builder()
                .buyer(companyOpsService.mapToDTO(invoice.getBuyer()))
                .seller(companyOpsService.mapToDTO(invoice.getSeller()))
                .items(invoice.getItems())
                .build();
    }
}
