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
@Table(name = "`order`")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderNo;

    @Column(length = 20, nullable = false)
    private String memberUid;
    private int orderPrice;
    private int orderDiscount;
    private int orderTotal;
    private int usedPoints;

    @Column(length = 50, nullable = false)
    private String receiver;

    @Column(length = 20, nullable = false)
    private String phone;

    @Column(length = 10, nullable = false)
    private String zipCode;

    @Column(nullable = false)
    private String addr1;
    private String addr2;

    @Column(length = 20, nullable = false)
    private String payMethod;

    @Column(length = 30, nullable = false)
    private String status;
    private Integer couponIssueId;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String orderNote;

    public OrderDTO toDTO() {
        return OrderDTO.builder()
                .orderNo(orderNo)
                .memberUid(memberUid)
                .orderPrice(orderPrice)
                .orderDiscount(orderDiscount)
                .couponIssueId(couponIssueId)
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
