package org.example.k_market.entity.coupon;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "coupon")
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int couponNo;

    private String couponType;
    private String sellerUid;
    private String benefit;
    private int issuedCnt;
    private int usedCnt;
    private String status;
    private String notice;
    private String name;
    private String expireDate;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private String startDate;
}
