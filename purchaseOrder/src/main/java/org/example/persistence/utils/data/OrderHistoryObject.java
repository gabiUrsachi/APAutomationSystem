package org.example.persistence.utils.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class OrderHistoryObject {

    LocalDateTime date;

    private OrderStatus status;

}
