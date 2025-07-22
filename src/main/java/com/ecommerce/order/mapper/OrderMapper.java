package com.ecommerce.order.mapper;

import com.ecommerce.order.model.OrderRequest;
import com.ecommerce.shared.enums.DeliveryTypeEnum;
import com.ecommerce.shared.enums.OrderStatusEnum;
import com.ecommerce.shared.events.OrderEvent;
import com.ecommerce.shared.model.Cart;
import com.ecommerce.shared.model.Order;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.util.UUID;

@Mapper(componentModel = "spring")
public abstract class OrderMapper {

    /**
     * This method takes Cart, OrderRequest, userId to get Order.
     *
     * @param cart    : Cart details
     * @param request : OrderRequest details
     * @param userId  : userId of user in String format
     * @return Order : Order
     */
    public Order getOrderFromCartAndOrderRequest(Cart cart, OrderRequest request, String userId) {
        Order order = new Order();
        order.setOrderId(UUID.randomUUID().toString().split("-")[0]);
        order.setUserId(userId);
        order.setItems(cart.getItems());
        order.setTotalAmount(cart.getTotalCartAmount() + (request.getDeliveryType().equals(DeliveryTypeEnum.EXPRESS) ? 25 : 0));
        order.setStatus(OrderStatusEnum.PLACED);
        order.setCreatedDateTime(LocalDateTime.now().toString());
        order.setDeliveryInfo(request.getDeliveryInfo());
        order.setBillingInfo(request.getBillingInfo());
        order.setDeliveryType(request.getDeliveryType());

        return order;
    }

    /**
     * This abstract method takes Order to generate mapping implementation and return OrderEvent.
     *
     * @param order : Order details
     * @return OrderEvent : OrderEvent
     */
    public abstract OrderEvent mapOrderToOrderEvent(Order order);
}
