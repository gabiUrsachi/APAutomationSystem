package org.example.presentation.utils;

import org.example.business.services.CompanyService;
import org.example.persistence.collections.Company;
import org.example.persistence.collections.Invoice;
import org.example.persistence.utils.InvoiceHelper;
import org.example.presentation.view.InvoiceDDO;
import org.example.presentation.view.InvoiceDPO;
import org.example.presentation.view.InvoiceDTO;
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
                .statusHistory(InvoiceHelper.initStatusHistory(invoiceDTO.getInvoiceStatus()))
                .version(invoiceDTO.getVersion())
                .totalAmount(invoiceDTO.getTotalAmount())
                .uri(invoiceDTO.getUri())
                .build();

    }

    public InvoiceDTO mapToDTO(Invoice invoice) {

        Company buyer = companyService.getCompanyById(invoice.getBuyerId());
        Company seller = companyService.getCompanyById(invoice.getSellerId());
        Float reducedAmount = invoice.getDiscountRate() != null ? invoice.getTotalAmount() - invoice.getDiscountRate() * invoice.getTotalAmount() : null;

        return InvoiceDTO.builder()
                .identifier(invoice.getIdentifier())
                .buyer(companyMapperService.mapToDTO(buyer))
                .seller(companyMapperService.mapToDTO(seller))
                .items(invoice.getItems())
                .invoiceStatus(InvoiceHelper.getMostRecentHistoryObject(invoice.getStatusHistory()).getStatus()) //get the most recent status
                .version(invoice.getVersion())
                .totalAmount(invoice.getTotalAmount())
                .discountRate(invoice.getDiscountRate())
                .finalAmount(reducedAmount)
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
                .invoiceStatus(InvoiceHelper.getMostRecentHistoryObject(invoice.getStatusHistory()).getStatus()) //get the most recent status
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

    public List<InvoiceDDO> mapToDDO(List<Invoice> invoices) {
        return invoices.stream()
                .map(this::mapToDDO)
                .collect(Collectors.toList());
    }

    public InvoiceDPO mapToDPO(OrderResponseDTO orderResponseDTO) {

        return InvoiceDPO.builder()
                .buyerId(orderResponseDTO.getBuyer().getCompanyIdentifier())
                .sellerId(orderResponseDTO.getSeller().getCompanyIdentifier())
                .items(orderResponseDTO.getItems())
                .build();
    }
}


