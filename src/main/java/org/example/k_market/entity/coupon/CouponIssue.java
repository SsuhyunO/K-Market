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
@Table(name = "coupon_issue")
public class CouponIssue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int issueNo;

    private int couponNo;
    private String memberUid;
    private int status;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
