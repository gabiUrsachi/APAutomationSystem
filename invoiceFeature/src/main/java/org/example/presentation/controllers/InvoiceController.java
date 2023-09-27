package org.example.presentation.controllers;

import org.example.business.services.InvoiceFilteringService;
import org.example.persistence.utils.data.InvoiceFilter;
import org.example.presentation.controllers.utils.InvoiceActionsPermissions;
import org.example.presentation.controllers.utils.ResourceActionType;
import org.example.services.InvoiceValidationService;
import org.example.utils.AuthorizationMapper;
import org.example.utils.data.JwtClaims;
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
    public InvoiceDTO createInvoice(@RequestBody InvoiceDPO invoiceDPO, HttpServletRequest request) {

        JwtClaims jwtClaims = AuthorizationMapper.servletRequestToJWTClaims(request);
        Set<Roles> validRoles = InvoiceActionsPermissions.VALID_ROLES.get(ResourceActionType.CREATE);

        authorisationService.authorize(jwtClaims.getRoles(), validRoles.toArray(new Roles[0]));

        invoiceValidatorService.verifyIdentifiersMatch(jwtClaims.getCompanyUUID(), invoiceDPO.getSellerId());

        Invoice invoiceEntity = invoiceMapperService.mapToEntity(invoiceDPO);
        Invoice responseInvoice = invoiceService.createInvoice(invoiceEntity);

        return invoiceMapperService.mapToDTO(responseInvoice);

    }

    @GetMapping
    public List<InvoiceDDO> getInvoices(HttpServletRequest request) {

        JwtClaims jwtClaims = AuthorizationMapper.servletRequestToJWTClaims(request);

        Set<Roles> validRoles = InvoiceActionsPermissions.VALID_ROLES.get(ResourceActionType.GET);
        Set<Roles> matchingRoles = authorisationService.authorize(jwtClaims.getRoles(), validRoles.toArray(new Roles[0]));

        List<InvoiceFilter> queryFilters = invoiceFilteringService.createQueryFilters(matchingRoles, jwtClaims.getCompanyUUID());

        return invoiceMapperService.mapToDDO(invoiceService.getInvoices(queryFilters));

    }

    @PostMapping("/fromOR")
    public InvoiceDTO createInvoiceFromPurchaseOrder(@RequestBody OrderResponseDTO orderResponseDTO, HttpServletRequest request) {

        JwtClaims jwtClaims = AuthorizationMapper.servletRequestToJWTClaims(request);
        Set<Roles> validRoles = InvoiceActionsPermissions.VALID_ROLES.get(ResourceActionType.CREATE_FROM_OR);

        authorisationService.authorize(jwtClaims.getRoles(), validRoles.toArray(new Roles[0]));

        invoiceValidatorService.verifyIdentifiersMatch(jwtClaims.getCompanyUUID(), orderResponseDTO.getSeller().getCompanyIdentifier());

        InvoiceDPO invoiceDPO = invoiceMapperService.mapToDPO(orderResponseDTO);

        Invoice invoiceEntity = invoiceMapperService.mapToEntity(invoiceDPO);
        Invoice responseInvoice = invoiceService.createInvoice(invoiceEntity);

        return invoiceMapperService.mapToDTO(responseInvoice);

    }

    @GetMapping("/{identifier}")
    public InvoiceDTO getById(@PathVariable UUID identifier, HttpServletRequest request) {

        JwtClaims jwtClaims = AuthorizationMapper.servletRequestToJWTClaims(request);

        Set<Roles> validRoles = InvoiceActionsPermissions.VALID_ROLES.get(ResourceActionType.GET);
        Set<Roles> matchingRoles = authorisationService.authorize(jwtClaims.getRoles(), validRoles.toArray(new Roles[0]));

        List<InvoiceFilter> queryFilters = invoiceFilteringService.createQueryFilters(matchingRoles, jwtClaims.getCompanyUUID());
        Invoice individualInvoice = invoiceService.getInvoice(identifier, queryFilters);

        return invoiceMapperService.mapToDTO(individualInvoice);


    }

    @PutMapping("/{identifier}")
    public InvoiceDTO updateInvoice(@PathVariable UUID identifier, @RequestBody InvoiceDTO invoiceDTO, HttpServletRequest request) {

        JwtClaims jwtClaims = AuthorizationMapper.servletRequestToJWTClaims(request);

        Set<Roles> validRoles = InvoiceActionsPermissions.VALID_ROLES.get(ResourceActionType.UPDATE);
        Set<Roles> matchingRoles = authorisationService.authorize(jwtClaims.getRoles(), validRoles.toArray(new Roles[0]));

        invoiceValidatorService.verifyUpdatePermission(invoiceDTO.getInvoiceStatus(), jwtClaims.getCompanyUUID(), invoiceDTO.getBuyer().getCompanyIdentifier(), invoiceDTO.getSeller().getCompanyIdentifier());

        Invoice invoice = invoiceMapperService.mapToEntity(invoiceDTO);
        Invoice updatedInvoice = invoiceService.updateInvoice(identifier, invoice);
        return invoiceMapperService.mapToDTO(updatedInvoice);

    }

    @DeleteMapping("/{identifier}")
    public void deleteById(@PathVariable UUID identifier, HttpServletRequest request) {

        JwtClaims jwtClaims = AuthorizationMapper.servletRequestToJWTClaims(request);

        Set<Roles> validRoles = InvoiceActionsPermissions.VALID_ROLES.get(ResourceActionType.DELETE);

        authorisationService.authorize(jwtClaims.getRoles(), validRoles.toArray(new Roles[0]));
        invoiceService.deleteInvoice(identifier);

    }


}