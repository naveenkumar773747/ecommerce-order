package com.ecommerce.order.mapper;

import com.ecommerce.shared.events.OrderPlacedEvent;
import com.ecommerce.shared.model.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class OrderMapper {

//    public abstract Order mapCartToOrder(Cart cart);

    /**
     * This abstract method takes Order to generate mapping implementation and return OrderPlacedEvent.
     *
     * @param order : Order details
     * @return OrderPlacedEvent : OrderPlacedEvent
     */
    public abstract OrderPlacedEvent mapOrderToEvent(Order order);
}
