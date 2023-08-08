package org.example.presentation.utils;

import org.example.business.services.CompanyMapperService;
import org.example.business.services.CompanyOpsService;
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
public class MapperService {
    @Autowired
    private CompanyMapperService companyMapperService;

    @Autowired
    private CompanyOpsService companyOpsService;

    /**
     * It creates an entity with the same properties as the received dto
     *
     * @param orderRequestDTO DTO to be converted
     * @return created entity
     */
    public PurchaseOrder mapToEntity(OrderRequestDTO orderRequestDTO) {
        return PurchaseOrder.builder()
                .buyer(orderRequestDTO.getBuyer())
                .seller(orderRequestDTO.getSeller())
                .items(orderRequestDTO.getItems())
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
                .buyer(companyMapperService.mapToDTO(companyOpsService.getCompanyById(purchaseOrder.getBuyer())))
                .seller(companyMapperService.mapToDTO(companyOpsService.getCompanyById(purchaseOrder.getSeller())))
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
