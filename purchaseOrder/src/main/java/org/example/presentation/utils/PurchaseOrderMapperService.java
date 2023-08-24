package org.example.presentation.utils;

import org.example.business.services.CompanyService;
import org.example.presentation.view.OrderRequestDTO;
import org.example.presentation.view.OrderResponseDTO;
import org.example.persistence.collections.PurchaseOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This service is used for entity <-> dto conversions
 */
@Service
public class PurchaseOrderMapperService {
    @Autowired
    private CompanyMapperService companyMapperService;

    @Autowired
    private CompanyService companyService;

    /**
     * It creates an entity with the same properties as the received dto
     *
     * @param orderRequestDTO DTO to be converted
     * @return created entity
     */
    public PurchaseOrder mapToEntity(OrderRequestDTO orderRequestDTO) {
        return PurchaseOrder.builder()
                .identifier(orderRequestDTO.getIdentifier())
                .buyer(orderRequestDTO.getBuyer())
                .seller(orderRequestDTO.getSeller())
                .items(orderRequestDTO.getItems())
                .orderStatus(orderRequestDTO.getOrderStatus())
                .version(orderRequestDTO.getVersion())
                .build();
    }

    /**
     * It creates a Data Transfer Object with the same properties as the received entity
     *
     * @param purchaseOrder entity to be converted
     * @return created DTO
     */
    public OrderResponseDTO mapToDTO(PurchaseOrder purchaseOrder) {
        return OrderResponseDTO.builder()
                .identifier(purchaseOrder.getIdentifier())
                .buyer(companyMapperService.mapToDTO(companyService.getCompanyById(purchaseOrder.getBuyer())))
                .seller(companyMapperService.mapToDTO(companyService.getCompanyById(purchaseOrder.getSeller())))
                .items(purchaseOrder.getItems())
                .orderStatus(purchaseOrder.getOrderStatus())
                .version(purchaseOrder.getVersion())
                .build();
    }

    /**
     * It creates a list of Data Transfer Object with the same properties as the received entities
     *
     * @param purchaseOrders entities to be converted
     * @return created DTOs
     */
    public List<OrderResponseDTO> mapToDTO(List<PurchaseOrder> purchaseOrders) {
        return purchaseOrders.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
}