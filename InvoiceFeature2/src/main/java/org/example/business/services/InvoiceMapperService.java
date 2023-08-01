package org.example.business.services;

import org.example.business.models.*;
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

    public Invoice mapToEntity(InvoiceDPO invoiceDPO) {

        return Invoice.builder()
                .identifier(invoiceDPO.getIdentifier())
                .buyerId(invoiceDPO.getBuyerId())
                .sellerId(invoiceDPO.getSellerId())
                .items(invoiceDPO.getItems())
                .build();

    }
    public Invoice mapToEntity(InvoiceDTO invoiceDTO) {

        return Invoice.builder()
                .identifier(invoiceDTO.getIdentifier())
                .buyerId(invoiceDTO.getBuyer().getCompanyIdentifier())
                .sellerId(invoiceDTO.getSeller().getCompanyIdentifier())
                .items(invoiceDTO.getItems())
                .build();

    }

    public InvoiceDTO mapToDTO(Invoice invoice) {

        CompanyDTO buyer = companyOpsService.getCompanyById(invoice.getBuyerId());
        CompanyDTO seller = companyOpsService.getCompanyById(invoice.getSellerId());
        return InvoiceDTO.builder()
                .identifier(invoice.getIdentifier())
                .buyer(buyer)
                .seller(seller)
                .items(invoice.getItems())
                .build();
    }

    public InvoiceDDO mapToDDO(Invoice invoice) {

        CompanyDTO buyer = companyOpsService.getCompanyById(invoice.getBuyerId());
        CompanyDTO seller = companyOpsService.getCompanyById(invoice.getSellerId());
        return InvoiceDDO.builder()
                .identifier(invoice.getIdentifier())
                .buyerName(buyer.getName())
                .sellerName(seller.getName())
                .build();
    }

    public InvoiceDPO mapToDPO(Invoice invoice) {

        return InvoiceDPO.builder()
                .identifier(invoice.getIdentifier())
                .buyerId(invoice.getBuyerId())
                .sellerId(invoice.getSellerId())
                .items(invoice.getItems())
                .build();
    }
    public List<InvoiceDDO> mapToDDO(List<Invoice> invoices){
        return invoices.stream()
                .map(this::mapToDDO)
                .collect(Collectors.toList());
    }

    public InvoiceDTO mapToDTO(OrderResponseDTO orderResponseDTO) {

        return InvoiceDTO.builder()
                .identifier(orderResponseDTO.getIdentifier())
                .buyer(orderResponseDTO.getBuyer())
                .seller(orderResponseDTO.getSeller())
                .items(orderResponseDTO.getItems())
                .build();
    }
}


