package org.example.k_market.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seller")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Seller {

    @Id
    @Column(name = "uid", length = 20)
    private String uid; // member 테이블의 uid와 동일한 값 (PK이자 FK)

    @Column(name = "companyName", length = 100, nullable = false)
    private String companyName;

    @Column(name = "bizRegNo", length = 12, nullable = false)
    private String bizRegNo;

    @Column(name = "onlineSalesNo", length = 30)
    private String onlineSalesNo;

    @Column(name = "tel", length = 20)
    private String tel;

    @Column(name = "fax", length = 20)
    private String fax;

    @Column(name = "status", length = 20, nullable = false)
    private String status; // PENDING(준비) / ACTIVE(운영중) / SUSPENDED(중단)

    @PrePersist
    protected void onCreate() {
        if (this.status == null) {
            this.status = "PENDING";
        }
    }

    // 상태 즉시변경용 (승인/중단/재개)
    public void changeStatus(String status) {
        this.status = status;
    }
}