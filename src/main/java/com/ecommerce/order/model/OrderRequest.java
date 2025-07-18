package com.ecommerce.order.model;

import com.ecommerce.shared.enums.DeliveryTypeEnum;
import com.ecommerce.shared.model.BillingInfo;
import com.ecommerce.shared.model.DeliveryInfo;
import lombok.Data;

@Data
public class OrderRequest {
    private DeliveryInfo deliveryInfo;
    private BillingInfo billingInfo;
    private DeliveryTypeEnum deliveryType;

    public DeliveryInfo getDeliveryInfo() {
        return deliveryInfo;
    }

    public BillingInfo getBillingInfo() {
        return billingInfo;
    }

    public DeliveryTypeEnum getDeliveryType() {
        return deliveryType;
    }
}