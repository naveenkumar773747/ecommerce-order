package com.ecommerce.order.mapper;

import com.ecommerce.shared.events.OrderEvent;
import com.ecommerce.shared.model.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class OrderMapper {

    /**
     * This abstract method takes Order to generate mapping implementation and return OrderEvent.
     *
     * @param order : Order details
     * @return OrderEvent : OrderEvent
     */
    public abstract OrderEvent mapOrderToEvent(Order order);
}
