package org.example.presentation.controllers;

import org.example.business.services.InvoiceFilteringService;
import org.example.persistence.utils.data.InvoiceFilter;
import org.example.services.InvoiceValidationService;
import org.example.utils.data.Roles;
import org.example.business.models.*;

import org.example.services.AuthorisationService;
import org.example.services.InvoiceMapperService;
import org.example.services.InvoiceService;
import org.example.persistence.collections.Invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.example.presentation.view.OrderResponseDTO;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "http://localhost:4200/")
public class InvoiceController {
    private final InvoiceService invoiceService;
    private final InvoiceFilteringService invoiceFilteringService;
    private final InvoiceMapperService invoiceMapperService;
    private final AuthorisationService authorisationService;

    private final InvoiceValidationService invoiceValidatorService;


    @Autowired
    public InvoiceController(InvoiceService invoiceService, InvoiceFilteringService invoiceFilteringService, InvoiceMapperService invoiceMapperService, AuthorisationService authorisationService, InvoiceValidationService invoiceValidatorService) {
        this.invoiceService = invoiceService;
        this.invoiceFilteringService = invoiceFilteringService;
        this.invoiceMapperService = invoiceMapperService;
        this.authorisationService = authorisationService;
        this.invoiceValidatorService = invoiceValidatorService;
    }

    @PostMapping
    @SuppressWarnings("unchecked cast")
    public InvoiceDTO createInvoice(@RequestBody InvoiceDPO invoiceDPO, HttpServletRequest request) {

        Set<Roles> userRoles = new HashSet<>((List<Roles>) request.getAttribute("roles"));
        UUID companyUUID = (UUID) request.getAttribute("company");

        authorisationService.authorize(userRoles, Roles.SUPPLIER_I);
        invoiceValidatorService.verifyIdentifiersMatch(companyUUID, invoiceDPO.getBuyerId());

        Invoice invoiceEntity = invoiceMapperService.mapToEntity(invoiceDPO);
        Invoice responseInvoice = invoiceService.createInvoice(invoiceEntity);

        return invoiceMapperService.mapToDTO(responseInvoice);

    }

    @GetMapping
    @SuppressWarnings("unchecked cast")
    public List<InvoiceDDO> getInvoices(HttpServletRequest request) {

        Set<Roles> userRoles = new HashSet<>((List<Roles>) request.getAttribute("roles"));
        UUID companyUUID = (UUID) request.getAttribute("company");

        Set<Roles> validRoles = authorisationService.authorize(userRoles, Roles.BUYER_II, Roles.SUPPLIER_I, Roles.SUPPLIER_II);

        List<InvoiceFilter> queryFilters = invoiceFilteringService.createQueryFilters(validRoles, companyUUID);

        return invoiceMapperService.mapToDDO(invoiceService.getInvoices(queryFilters));

    }

    @PostMapping("/fromOR")
    @SuppressWarnings("unchecked cast")
    public InvoiceDTO createInvoiceFromPurchaseOrder(@RequestBody OrderResponseDTO orderResponseDTO, HttpServletRequest request) {
        Set<Roles> userRoles = new HashSet<>((List<Roles>) request.getAttribute("roles"));

        UUID companyUUID = (UUID) request.getAttribute("company");

        authorisationService.authorize(userRoles, Roles.SUPPLIER_II);
        invoiceValidatorService.verifyIdentifiersMatch(companyUUID, orderResponseDTO.getBuyer().getCompanyIdentifier());

        InvoiceDPO invoiceDPO = invoiceMapperService.mapToDPO(orderResponseDTO);
        return createInvoice(invoiceDPO, request);

    }

    @GetMapping("/{identifier}")
    @SuppressWarnings("unchecked cast")
    public InvoiceDTO getById(@PathVariable UUID identifier, HttpServletRequest request) {

        Set<Roles> userRoles = new HashSet<>((List<Roles>) request.getAttribute("roles"));

        UUID companyUUID = (UUID) request.getAttribute("company");

        Set<Roles> validRoles = authorisationService.authorize(userRoles, Roles.BUYER_II, Roles.SUPPLIER_I,Roles.SUPPLIER_II);

        List<InvoiceFilter> queryFilters = invoiceFilteringService.createQueryFilters(validRoles, companyUUID);
        Invoice individualInvoice = invoiceService.getInvoice(identifier,queryFilters);

        return invoiceMapperService.mapToDTO(individualInvoice);


    }

    @PutMapping("/{identifier}")
    @SuppressWarnings("unchecked cast")
    public InvoiceDTO updateInvoice(@PathVariable UUID identifier, @RequestBody InvoiceDTO invoiceDTO, HttpServletRequest request) {

        Set<Roles> userRoles = new HashSet<>((List<Roles>) request.getAttribute("roles"));
        UUID companyUUID = (UUID) request.getAttribute("company");

        authorisationService.authorize(userRoles, Roles.SUPPLIER_I, Roles.BUYER_II);
        invoiceValidatorService.verifyUpdatePermission(invoiceDTO.getInvoiceStatus(), companyUUID,invoiceDTO.getBuyer().getCompanyIdentifier(), invoiceDTO.getSeller().getCompanyIdentifier());

        Invoice invoice = invoiceMapperService.mapToEntity(invoiceDTO);
        invoiceService.updateInvoice(identifier, invoice);
        return invoiceMapperService.mapToDTO(invoice);

    }

    @DeleteMapping("/{identifier}")
    @SuppressWarnings("unchecked cast")
    public void deleteById(@PathVariable UUID identifier, HttpServletRequest request) {

        Set<Roles> userRoles = new HashSet<>((List<Roles>) request.getAttribute("roles"));
        UUID companyUUID = (UUID) request.getAttribute("company");

        authorisationService.authorize(userRoles, Roles.SUPPLIER_I);
        invoiceService.deleteInvoice(identifier);

    }


}