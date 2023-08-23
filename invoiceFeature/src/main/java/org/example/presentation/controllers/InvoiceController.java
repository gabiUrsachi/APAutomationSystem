package org.example.presentation.controllers;

import org.example.utils.Roles;
import org.example.business.models.*;

import org.example.services.AuthorisationService;
import org.example.services.InvoiceMapperService;
import org.example.services.InvoiceService;
import org.example.persistence.collections.Invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.example.presentation.view.OrderResponseDTO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "http://localhost:4200/")
public class InvoiceController {
    private final InvoiceService invoiceService;

    private final InvoiceMapperService invoiceMapperService;

    private final AuthorisationService authorisationService;

    @Autowired
    public InvoiceController(InvoiceService invoiceService, InvoiceMapperService invoiceMapperService,AuthorisationService authorisationService) {
        this.invoiceService = invoiceService;
        this.invoiceMapperService = invoiceMapperService;
        this.authorisationService = authorisationService;
    }

    @PostMapping
    @SuppressWarnings("unchecked cast")
    public InvoiceDTO createInvoice(@RequestBody InvoiceDPO invoiceDPO, HttpServletRequest request) {

        Set<Roles> userRoles = (Set<Roles>) request.getAttribute("roles");
        authorisationService.authorize(userRoles, Roles.SUPPLIER_I);
        Invoice invoiceEntity = invoiceMapperService.mapToEntity(invoiceDPO);
        Invoice responseInvoice = invoiceService.createInvoice(invoiceEntity);
        return invoiceMapperService.mapToDTO(responseInvoice);

    }

    @GetMapping
    @SuppressWarnings("unchecked cast")
    public List<InvoiceDDO> getInvoices(HttpServletRequest request) {
        Set<Roles> userRoles = (Set<Roles>) request.getAttribute("roles");
        authorisationService.authorize(userRoles, Roles.BUYER_II,Roles.SUPPLIER_I,Roles.SUPPLIER_II);
        return invoiceMapperService.mapToDDO(invoiceService.getInvoices());

    }

    @PostMapping("/fromOR")
    @SuppressWarnings("unchecked cast")
    public InvoiceDTO createInvoiceFromPurchaseOrder(@RequestBody OrderResponseDTO orderResponseDTO,HttpServletRequest request) {
        Set<Roles> userRoles = (Set<Roles>) request.getAttribute("roles");
        authorisationService.authorize(userRoles, Roles.SUPPLIER_II);
        InvoiceDPO invoiceDPO = invoiceMapperService.mapToDPO(orderResponseDTO);
        return createInvoice(invoiceDPO,request);

    }

    @GetMapping("/{identifier}")
    @SuppressWarnings("unchecked cast")
    public InvoiceDTO getById(@PathVariable UUID identifier,HttpServletRequest request) {

        Set<Roles> userRoles = (Set<Roles>) request.getAttribute("roles");
        authorisationService.authorize(userRoles, Roles.SUPPLIER_I, Roles.BUYER_I);
        return invoiceMapperService.mapToDTO(invoiceService.getInvoice(identifier));

    }

    @PutMapping("/{identifier}")
    @SuppressWarnings("unchecked cast")
    public  InvoiceDTO updateInvoice(@PathVariable UUID identifier, @RequestBody InvoiceDTO invoiceDTO,HttpServletRequest request) {

        Set<Roles> userRoles = (Set<Roles>) request.getAttribute("roles");
        authorisationService.authorize(userRoles, Roles.SUPPLIER_I, Roles.BUYER_II);
        Invoice invoice = invoiceMapperService.mapToEntity(invoiceDTO);
        invoiceService.updateInvoice(identifier,invoice);
        return invoiceMapperService.mapToDTO(invoice);

    }
    @DeleteMapping("/{identifier}")
    @SuppressWarnings("unchecked cast")
    public void deleteById(@PathVariable UUID identifier,HttpServletRequest request) {

        Set<Roles> userRoles = (Set<Roles>) request.getAttribute("roles");
        authorisationService.authorize(userRoles, Roles.SUPPLIER_I);
        invoiceService.deleteInvoice(identifier);

    }


}