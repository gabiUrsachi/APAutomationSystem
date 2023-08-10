package org.example.business.services;

import org.example.business.models.*;
import org.example.persistence.collections.Company;
import org.example.persistence.collections.Invoice;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceMapperService {
    private final CompanyOpsService companyOpsService;
    private final CompanyMapperService companyMapperService;

    public InvoiceMapperService(CompanyOpsService companyOpsService, CompanyMapperService companyMapperService) {
        this.companyOpsService = companyOpsService;
        this.companyMapperService = companyMapperService;
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
                .invoiceStatus(invoiceDTO.getInvoiceStatus())
                .build();

    }

    public InvoiceDTO mapToDTO(Invoice invoice) {

        Company buyer = companyOpsService.getCompanyById(invoice.getBuyerId());
        Company seller = companyOpsService.getCompanyById(invoice.getSellerId());
        return InvoiceDTO.builder()
                .identifier(invoice.getIdentifier())
                .buyer(companyMapperService.mapToDTO(buyer))
                .seller(companyMapperService.mapToDTO(seller))
                .items(invoice.getItems())
                .invoiceStatus(invoice.getInvoiceStatus())
                .build();
    }

    public InvoiceDDO mapToDDO(Invoice invoice) {

        Company buyer = companyOpsService.getCompanyById(invoice.getBuyerId());
        Company seller = companyOpsService.getCompanyById(invoice.getSellerId());
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


