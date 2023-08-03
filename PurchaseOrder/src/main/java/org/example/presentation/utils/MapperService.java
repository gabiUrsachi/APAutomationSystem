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

@Service
public class MapperService {
    @Autowired
    private CompanyMapperService companyMapperService;

    @Autowired
    private CompanyOpsService companyOpsService;

    public PurchaseOrder mapToEntity(OrderRequestDTO orderRequestDTO){
        return PurchaseOrder.builder()
                .buyer(orderRequestDTO.getBuyer())
                .seller(orderRequestDTO.getSeller())
                .items(orderRequestDTO.getItems())
                .build();
    }

    public OrderResponseDTO mapToDTO(PurchaseOrder purchaseOrder){
        return OrderResponseDTO.builder()
                .identifier(purchaseOrder.getIdentifier())
                .buyer(companyMapperService.mapToDTO(companyOpsService.getCompanyById(purchaseOrder.getBuyer())))
                .seller(companyMapperService.mapToDTO(companyOpsService.getCompanyById(purchaseOrder.getSeller())))
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
