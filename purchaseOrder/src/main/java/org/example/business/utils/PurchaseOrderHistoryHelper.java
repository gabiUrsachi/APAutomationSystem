package org.example.business.utils;

import org.example.persistence.utils.data.OrderHistoryObject;
import org.example.persistence.utils.data.OrderStatus;

import java.time.LocalDateTime;
import java.util.*;

public class PurchaseOrderHistoryHelper {


    public static OrderHistoryObject getLastestOrderHistoryObject(List<OrderHistoryObject> orderHistory) {

        return orderHistory.stream()
                .max(Comparator.comparing(OrderHistoryObject::getDate))
                .orElse(null);
    }


    /**
     * Initializes an OrderHistoryObject set containing the first parameter as its element
     *
     * @param orderStatus status to initialize the set with
     * @return Set of containing the order status
     */
    public static List<OrderHistoryObject> generateOrderHistoryList(OrderStatus orderStatus) {

        List<OrderHistoryObject> orderHistory = new ArrayList<>();
        OrderHistoryObject newOrderHistoryObject = new OrderHistoryObject(LocalDateTime.now(), orderStatus);
        orderHistory.add(newOrderHistoryObject);
        return orderHistory;
    }

}
