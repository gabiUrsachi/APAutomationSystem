package org.example.business.services;

import org.example.business.models.OrderRequestDTO;
import org.example.business.models.OrderResponseDTO;
import org.example.persistence.collections.PurchaseOrder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MapperService {
    private final CompanyOpsService companyOpsService;

    public MapperService(CompanyOpsService companyOpsService) {
        this.companyOpsService = companyOpsService;
    }

    public PurchaseOrder mapToEntity(OrderRequestDTO orderRequestDTO){
        return PurchaseOrder.builder()
                .buyer(companyOpsService.mapToEntity(orderRequestDTO.getBuyer()))
                .seller(companyOpsService.mapToEntity(orderRequestDTO.getSeller()))
                .items(orderRequestDTO.getItems())
                .build();
    }

    public OrderResponseDTO mapToDTO(PurchaseOrder purchaseOrder){
        return OrderResponseDTO.builder()
                .identifier(purchaseOrder.getIdentifier())
                .buyer(companyOpsService.mapToDTO(purchaseOrder.getBuyer()))
                .seller(companyOpsService.mapToDTO(purchaseOrder.getSeller()))
                .items(purchaseOrder.getItems())
                .orderStatus(purchaseOrder.getOrderStatus())
                .build();
    }

    public Set<OrderResponseDTO> mapToDTO(Set<PurchaseOrder> purchaseOrders){
        return purchaseOrders.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toSet());
    }
}
