package com.ecommerce.order.mapper;

import com.ecommerce.shared.events.PaymentEvent;
import com.ecommerce.shared.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class PaymentMapper {

    /**
     * This abstract method takes Order to generate mapping implementation and return OrderEvent.
     *
     * @param order : Order details
     * @return PaymentEvent : PaymentEvent
     */
    @Mapping(target = "paymentTypeEnum", source = "billingInfo.paymentMethod")
    public abstract PaymentEvent mapOrderToPaymentEvent(Order order);
}
