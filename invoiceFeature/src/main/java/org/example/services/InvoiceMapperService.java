package org.example.services;

import org.example.business.models.*;
import org.example.business.services.CompanyService;
import org.example.persistence.collections.Company;
import org.example.persistence.collections.Invoice;
import org.example.persistence.collections.Item;
import org.example.presentation.utils.CompanyMapperService;
import org.example.presentation.view.OrderResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceMapperService {
    private final CompanyService companyService;
    private final CompanyMapperService companyMapperService;

    public InvoiceMapperService(CompanyService companyService, CompanyMapperService companyMapperService) {
        this.companyService = companyService;
        this.companyMapperService = companyMapperService;
    }

    public Invoice mapToEntity(InvoiceDPO invoiceDPO) {

        return Invoice.builder()
                .identifier(invoiceDPO.getIdentifier())
                .buyerId(invoiceDPO.getBuyerId())
                .sellerId(invoiceDPO.getSellerId())
                .items(invoiceDPO.getItems())
                .totalAmount((float) invoiceDPO.getItems().stream()
                        .mapToDouble(item -> item.getPrice() * item.getQuantity())
                        .reduce(0, Double::sum))
                .build();

    }
    public Invoice mapToEntity(InvoiceDTO invoiceDTO) {

        return Invoice.builder()
                .identifier(invoiceDTO.getIdentifier())
                .buyerId(invoiceDTO.getBuyer().getCompanyIdentifier())
                .sellerId(invoiceDTO.getSeller().getCompanyIdentifier())
                .items(invoiceDTO.getItems())
                .invoiceStatus(invoiceDTO.getInvoiceStatus())
                .version(invoiceDTO.getVersion())
                .totalAmount(invoiceDTO.getTotalAmount())
                .uri(invoiceDTO.getUri())
                .build();

    }

    public InvoiceDTO mapToDTO(Invoice invoice) {

        Company buyer = companyService.getCompanyById(invoice.getBuyerId());
        Company seller = companyService.getCompanyById(invoice.getSellerId());
        return InvoiceDTO.builder()
                .identifier(invoice.getIdentifier())
                .buyer(companyMapperService.mapToDTO(buyer))
                .seller(companyMapperService.mapToDTO(seller))
                .items(invoice.getItems())
                .invoiceStatus(invoice.getInvoiceStatus())
                .version(invoice.getVersion())
                .totalAmount(invoice.getTotalAmount())
                .uri(invoice.getUri())
                .build();
    }

    public InvoiceDDO mapToDDO(Invoice invoice) {

        Company buyer = companyService.getCompanyById(invoice.getBuyerId());
        Company seller = companyService.getCompanyById(invoice.getSellerId());
        return InvoiceDDO.builder()
                .identifier(invoice.getIdentifier())
                .buyerName(buyer.getName())
                .sellerName(seller.getName())
                .invoiceStatus(invoice.getInvoiceStatus())
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

    public InvoiceDPO mapToDPO(OrderResponseDTO orderResponseDTO) {

        int orderStatusValue = orderResponseDTO.getOrderStatus().ordinal();
        return InvoiceDPO.builder()
                .buyerId(orderResponseDTO.getBuyer().getCompanyIdentifier())
                .sellerId(orderResponseDTO.getSeller().getCompanyIdentifier())
                .items(orderResponseDTO.getItems())
                .build();
    }
}


