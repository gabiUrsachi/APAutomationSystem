package org.example.business.services;

import org.example.business.models.OrderRequestDTO;
import org.example.business.models.OrderResponseDTO;
import org.example.persistence.collections.PurchaseOrder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MapperService {
    private final CompanyMapperService companyMapperService;

    public MapperService(CompanyMapperService companyMapperService) {
        this.companyMapperService = companyMapperService;
    }

    public PurchaseOrder mapToEntity(OrderRequestDTO orderRequestDTO){
        return PurchaseOrder.builder()
                .buyer(companyMapperService.mapToEntity(orderRequestDTO.getBuyer()))
                .seller(companyMapperService.mapToEntity(orderRequestDTO.getSeller()))
                .items(orderRequestDTO.getItems())
                .build();
    }

    public OrderResponseDTO mapToDTO(PurchaseOrder purchaseOrder){
        return OrderResponseDTO.builder()
                .identifier(purchaseOrder.getIdentifier())
                .buyer(companyMapperService.mapToDTO(purchaseOrder.getBuyer()))
                .seller(companyMapperService.mapToDTO(purchaseOrder.getSeller()))
                .items(purchaseOrder.getItems())
                .orderStatus(purchaseOrder.getOrderStatus())
                .build();
    }

    public List<OrderResponseDTO> mapToDTO(List<PurchaseOrder> purchaseOrders){
        return purchaseOrders.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
}
