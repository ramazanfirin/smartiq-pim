package com.smartiq.pim.service.dto;

import com.smartiq.pim.domain.Address;
import com.smartiq.pim.domain.enumeration.OrderStatus;

public class CreateOrderManagementDTO {

    private Long orderId;
    private OrderStatus status;
    private Address address;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
