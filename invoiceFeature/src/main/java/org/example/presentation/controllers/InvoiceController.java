package org.example.presentation.controllers;

import org.example.S3BucketOps;
import org.example.presentation.utils.InvoiceActionsPermissions;
import org.example.presentation.utils.InvoiceResourceActionType;
import org.example.presentation.view.InvoiceDDO;
import org.example.presentation.view.InvoiceDPO;
import org.example.presentation.view.InvoiceDTO;
import org.example.business.services.InvoiceFilteringService;
import org.example.persistence.collections.Invoice;
import org.example.persistence.utils.data.InvoiceFilter;
import org.example.presentation.view.OrderResponseDTO;
import org.example.services.AuthorisationService;
import org.example.presentation.utils.InvoiceMapperService;
import org.example.business.services.InvoiceService;
import org.example.business.services.InvoiceValidationService;
import org.example.utils.AuthorizationMapper;
import org.example.utils.data.JwtClaims;
import org.example.utils.data.Roles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {
    private static final Logger logger = LoggerFactory.getLogger(InvoiceController.class);
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
    public InvoiceDTO createInvoice(@RequestPart("invoiceDPO") InvoiceDPO invoiceDPO, @RequestPart("file") MultipartFile multipartFile, HttpServletRequest request) throws IOException {
        logger.info("[POST request] -> create invoice with buyer {} and seller {}.", invoiceDPO.getBuyerId(), invoiceDPO.getSellerId());

        JwtClaims jwtClaims = AuthorizationMapper.servletRequestToJWTClaims(request);
        Set<Roles> validRoles = InvoiceActionsPermissions.VALID_ROLES.get(InvoiceResourceActionType.CREATE);

        authorisationService.authorize(jwtClaims.getRoles(), validRoles.toArray(new Roles[0]));

        invoiceValidatorService.verifyIdentifiersMatch(jwtClaims.getCompanyUUID(), invoiceDPO.getSellerId());

        Invoice invoiceEntity = invoiceMapperService.mapToEntity(invoiceDPO);
        invoiceEntity.setUri(StringUtils.getFilenameExtension(multipartFile.getOriginalFilename()));
        Invoice responseInvoice = invoiceService.createInvoice(invoiceEntity);


        S3BucketOps.putS3Object(invoiceDPO.getSellerId().toString(), responseInvoice.getUri(), multipartFile.getInputStream());
        return invoiceMapperService.mapToDTO(responseInvoice);

    }

    @GetMapping
    public List<InvoiceDDO> getInvoices(HttpServletRequest request) {
        logger.info("[GET request] -> get all invoices");

        JwtClaims jwtClaims = AuthorizationMapper.servletRequestToJWTClaims(request);

        Set<Roles> validRoles = InvoiceActionsPermissions.VALID_ROLES.get(InvoiceResourceActionType.GET);
        Set<Roles> matchingRoles = authorisationService.authorize(jwtClaims.getRoles(), validRoles.toArray(new Roles[0]));

        List<InvoiceFilter> queryFilters = invoiceFilteringService.createQueryFilters(matchingRoles, jwtClaims.getCompanyUUID());

        List<InvoiceDDO> myList = invoiceMapperService.mapToDDO(invoiceService.getInvoices(queryFilters));
        return  myList;

    }

    @PostMapping("/fromOR")
    public InvoiceDTO createInvoiceFromPurchaseOrder(@RequestBody OrderResponseDTO orderResponseDTO, HttpServletRequest request) throws IOException {
        logger.info("[POST request] -> create invoice from purchase order identified by {}.", orderResponseDTO.getIdentifier());

        JwtClaims jwtClaims = AuthorizationMapper.servletRequestToJWTClaims(request);
        Set<Roles> validRoles = InvoiceActionsPermissions.VALID_ROLES.get(InvoiceResourceActionType.CREATE_FROM_OR);

        authorisationService.authorize(jwtClaims.getRoles(), validRoles.toArray(new Roles[0]));

        invoiceValidatorService.verifyIdentifiersMatch(jwtClaims.getCompanyUUID(), orderResponseDTO.getSeller().getCompanyIdentifier());

        InvoiceDPO invoiceDPO = invoiceMapperService.mapToDPO(orderResponseDTO);

        Invoice invoiceEntity = invoiceMapperService.mapToEntity(invoiceDPO);
        invoiceEntity.setUri(orderResponseDTO.getUri().split("\\.")[1]);
        Invoice responseInvoice = invoiceService.createInvoice(invoiceEntity);

        String bucketName = String.valueOf(responseInvoice.getSellerId());
        S3BucketOps.duplicateS3Object(bucketName, orderResponseDTO.getUri(), responseInvoice.getUri());
        return invoiceMapperService.mapToDTO(responseInvoice);

    }

    @GetMapping("/{identifier}")
    public InvoiceDTO getById(@PathVariable UUID identifier, HttpServletRequest request) {
        logger.info("[GET request] -> get invoice by UUID: {}.", identifier);

        JwtClaims jwtClaims = AuthorizationMapper.servletRequestToJWTClaims(request);

        Set<Roles> validRoles = InvoiceActionsPermissions.VALID_ROLES.get(InvoiceResourceActionType.GET);
        Set<Roles> matchingRoles = authorisationService.authorize(jwtClaims.getRoles(), validRoles.toArray(new Roles[0]));

        List<InvoiceFilter> queryFilters = invoiceFilteringService.createQueryFilters(matchingRoles, jwtClaims.getCompanyUUID());
        Invoice individualInvoice = invoiceService.getInvoice(identifier, queryFilters);

        return invoiceMapperService.mapToDTO(individualInvoice);


    }

    @PutMapping("/{identifier}")
    public InvoiceDTO updateInvoice(@PathVariable UUID identifier, @RequestBody InvoiceDTO invoiceDTO, HttpServletRequest request) {
        logger.info("[PUT request] -> update invoice identified by {}.", identifier);

        JwtClaims jwtClaims = AuthorizationMapper.servletRequestToJWTClaims(request);

        Set<Roles> validRoles = InvoiceActionsPermissions.VALID_ROLES.get(InvoiceResourceActionType.UPDATE);
        Set<Roles> matchingRoles = authorisationService.authorize(jwtClaims.getRoles(), validRoles.toArray(new Roles[0]));

        invoiceValidatorService.verifyUpdatePermission(invoiceDTO.getInvoiceStatus(), jwtClaims.getCompanyUUID(), invoiceDTO.getBuyer().getCompanyIdentifier(), invoiceDTO.getSeller().getCompanyIdentifier());

        Invoice invoice = invoiceMapperService.mapToEntity(invoiceDTO);
        Invoice updatedInvoice = invoiceService.updateInvoice(identifier, invoice);
        return invoiceMapperService.mapToDTO(updatedInvoice);

    }

    @DeleteMapping("/{identifier}")
    public void deleteById(@PathVariable UUID identifier, HttpServletRequest request) {
        logger.info("[DELETE request] -> remove invoice identified by {}.", identifier);

        JwtClaims jwtClaims = AuthorizationMapper.servletRequestToJWTClaims(request);

        Set<Roles> validRoles = InvoiceActionsPermissions.VALID_ROLES.get(InvoiceResourceActionType.DELETE);

        authorisationService.authorize(jwtClaims.getRoles(), validRoles.toArray(new Roles[0]));
        invoiceService.deleteInvoice(identifier);

    }


}