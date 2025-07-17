package com.ecommerce.order.mapper;

import com.ecommerce.shared.events.OrderPlacedEvent;
import com.ecommerce.shared.model.Cart;
import com.ecommerce.shared.model.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class OrderMapper {

    protected abstract Order mapCartToOrder(Cart cart);


    protected abstract OrderPlacedEvent mapCartToOrder(Order order);
}
