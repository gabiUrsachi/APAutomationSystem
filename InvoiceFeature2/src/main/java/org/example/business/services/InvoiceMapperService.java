package org.example.business.services;

import org.example.business.models.CompanyDTO;
import org.example.business.models.InvoiceDTO;
import org.example.business.models.OrderResponseDTO;
import org.example.persistence.collections.Invoice;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InvoiceMapperService {
    private final CompanyOpsService companyOpsService;

    public InvoiceMapperService(CompanyOpsService companyOpsService) {
        this.companyOpsService = companyOpsService;
    }

    public Invoice mapToEntity(InvoiceDTO invoiceDTO) {

        UUID invoiceIdentifier = UUID.randomUUID();
        return Invoice.builder()
                .identifier(invoiceIdentifier)
                .buyerId(invoiceDTO.getBuyer().getCompanyIdentifier())
                .sellerId(invoiceDTO.getSeller().getCompanyIdentifier())
                .items(invoiceDTO.getItems())
                .build();

    }

    public InvoiceDTO mapToDTO(Invoice invoice) {

        CompanyDTO buyer = companyOpsService.getCompanyById(invoice.getBuyerId());
        CompanyDTO seller = companyOpsService.getCompanyById(invoice.getSellerId());
        return InvoiceDTO.builder()
                .buyer(buyer)
                .seller(seller)
                .items(invoice.getItems())
                .build();
    }
    public List<InvoiceDTO> mapToDTO(List<Invoice> invoices){
        return invoices.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public InvoiceDTO mapToDTO(OrderResponseDTO orderResponseDTO) {

        return InvoiceDTO.builder()
                .buyer(orderResponseDTO.getBuyer())
                .seller(orderResponseDTO.getSeller())
                .items(orderResponseDTO.getItems())
                .build();
    }
}


