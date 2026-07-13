package org.example.k_market.dto.order;

import jakarta.persistence.Id;
import lombok.*;
import org.example.k_market.entity.order.Order;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {
    private int orderNo;
    private String memberUid;
    private int orderPrice;
    private int orderDiscount;
    private Long couponIssueId;
    private int orderTotal;
    private int usedPoints;
    private int shippingFee;
    private String receiver;
    private String phone;
    private String zipCode;
    private String addr1;
    private String addr2;
    private String payMethod;
    private String status;
    private String createdAt;
    private String courierName;
    private String trackingNo;
    private String orderNote;

    public Order toEntity() {
        return Order.builder()
                .orderNo(orderNo)
                .memberUid(memberUid)
                .orderPrice(orderPrice)
                .orderDiscount(orderDiscount)
                .couponIssueId(couponIssueId)
                .orderTotal(orderTotal)
                .usedPoints(usedPoints)
                .shippingFee(shippingFee)
                .receiver(receiver)
                .phone(phone)
                .zipCode(zipCode)
                .addr1(addr1)
                .addr2(addr2)
                .payMethod(payMethod)
                .status(status)
                .courierName(courierName)
                .trackingNo(trackingNo)
                .orderNote(orderNote)
                .build();
    }
}
