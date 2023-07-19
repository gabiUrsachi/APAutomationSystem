package org.example.business.services;

import org.example.business.models.CompanyDTO;
import org.example.business.models.OrderRequestDTO;
import org.example.business.models.OrderResponseDTO;
import org.example.persistence.collections.Company;
import org.example.persistence.collections.PurchaseOrder;
import org.example.persistence.repository.PurchaseOrderRepository;
import org.example.persistence.utils.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PurchaseOrderOpsService {
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private CompanyOpsService companyOpsService;

    public OrderResponseDTO createPurchaseOrder(OrderRequestDTO orderRequestDTO){
        // map dto to entity
        PurchaseOrder purchaseOrder = mapToEntity(orderRequestDTO);

        UUID purchaseOrderIdentifier = UUID.randomUUID();
        purchaseOrder.setIdentifier(purchaseOrderIdentifier);
        purchaseOrder.setOrderStatus(OrderStatus.CREATED);

        // persist
        PurchaseOrder savedPurchaseOrder = purchaseOrderRepository.insert(purchaseOrder);

        return mapToDTO(savedPurchaseOrder);
    }

    public Set<OrderResponseDTO> getPurchaseOrdersByBuyer(CompanyDTO buyer){
        Company company = companyOpsService.mapToEntity(buyer);

        Set<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findAllByBuyer(company);

        return mapToDTO(purchaseOrders);
    }

    public void updatePurchaseOrder( UUID identifier, OrderRequestDTO orderRequestDTO){
        Optional<PurchaseOrder> oldPurchaseOrder = purchaseOrderRepository.findById(identifier);

        // map dto to entity
        PurchaseOrder purchaseOrder = mapToEntity(orderRequestDTO);
        purchaseOrder.setIdentifier(identifier);
        purchaseOrder.setOrderStatus(oldPurchaseOrder.get().getOrderStatus());

        // persist
        PurchaseOrder updatedPurchaseOrder = purchaseOrderRepository.save(purchaseOrder);
        int x =10;
    }

    private PurchaseOrder mapToEntity(OrderRequestDTO orderRequestDTO){

        return PurchaseOrder.builder()
                .buyer(companyOpsService.mapToEntity(orderRequestDTO.getBuyer()))
                .seller(companyOpsService.mapToEntity(orderRequestDTO.getSeller()))
                .items(orderRequestDTO.getItems())
                .build();
    }

    private OrderResponseDTO mapToDTO(PurchaseOrder purchaseOrder){
        return OrderResponseDTO.builder()
                .identifier(purchaseOrder.getIdentifier())
                .buyer(companyOpsService.mapToDTO(purchaseOrder.getBuyer()))
                .seller(companyOpsService.mapToDTO(purchaseOrder.getSeller()))
                .items(purchaseOrder.getItems())
                .orderStatus(purchaseOrder.getOrderStatus())
                .build();
    }

    private Set<OrderResponseDTO> mapToDTO(Set<PurchaseOrder> purchaseOrders){
        return purchaseOrders.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toSet());
    }

}
