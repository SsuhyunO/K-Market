package org.example.k_market.entity.order;

import jakarta.persistence.*;
import lombok.*;
import org.example.k_market.dto.order.OrderDTO;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderNo;

    private String memberUid;
    private int orderPrice;
    private int orderDiscount;
    private int orderTotal;
    private int usedPoints;
    private String receiver;
    private String phone;
    private String zipCode;
    private String addr1;
    private String addr2;
    private String payMethod;
    private String status;
    private Long couponIssueId;

    @CreationTimestamp
    private LocalDateTime createdAt;
    private String orderNote;

    public OrderDTO toDTO() {
        return OrderDTO.builder()
                .orderNo(orderNo)
                .memberUid(memberUid)
                .orderPrice(orderPrice)
                .orderDiscount(orderDiscount)
                .orderTotal(orderTotal)
                .usedPoints(usedPoints)
                .receiver(receiver)
                .phone(phone)
                .zipCode(zipCode)
                .addr1(addr1)
                .addr2(addr2)
                .payMethod(payMethod)
                .status(status)
                .createdAt(createdAt.toString())
                .orderNote(orderNote)
                .build();
    }
}
