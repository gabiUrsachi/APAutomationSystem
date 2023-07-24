package org.example.business.services;

import org.example.business.models.InvoiceDTO;
import org.example.business.models.OrderResponseDTO;
import org.example.persistence.collections.Invoice;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MapperService {
    private final CompanyOpsService companyOpsService;

    public MapperService(CompanyOpsService companyOpsService) {
        this.companyOpsService = companyOpsService;
    }

    public Invoice mapToEntity(InvoiceDTO invoiceDTO) {

        UUID invoiceIdentifier = UUID.randomUUID();
        return Invoice.builder()
                .identifier(invoiceIdentifier)
                .buyer(companyOpsService.mapToEntity(invoiceDTO.getBuyer()))
                .seller(companyOpsService.mapToEntity(invoiceDTO.getSeller()))
                .items(invoiceDTO.getItems())
                .build();

    }

    public InvoiceDTO mapToDTO(Invoice invoice) {

        return InvoiceDTO.builder()
                .buyer(companyOpsService.mapToDTO(invoice.getBuyer()))
                .seller(companyOpsService.mapToDTO(invoice.getSeller()))
                .items(invoice.getItems())
                .build();
    }

    public InvoiceDTO mapToDTO(OrderResponseDTO orderResponseDTO) {

        return InvoiceDTO.builder()
                .buyer(orderResponseDTO.getBuyer())
                .seller(orderResponseDTO.getSeller())
                .items(orderResponseDTO.getItems())
                .build();
    }
}


