package org.example.k_market.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Member {

    @Id
    @Column(name = "uid", length = 20)
    private String uid; // 로그인 아이디

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "birthDate", length = 10)
    private String birthDate;

    @Column(name = "gender", length = 1)
    private String gender;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "memberType", length = 10, nullable = false)
    private String memberType;

    @Column(name = "memberLevel")
    private Integer memberLevel;

    @Column(name = "pointBalance")
    private Integer pointBalance;

    @Column(name = "zipCode", length = 10)
    private String zipCode;

    @Column(name = "addr1", length = 255)
    private String addr1;

    @Column(name = "addr2", length = 255)
    private String addr2;

    @Column(name = "regIp", length = 20)
    private String regIp;

    @Column(name = "createdAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.pointBalance == null) this.pointBalance = 0;
        if (this.memberLevel == null) this.memberLevel = 1;
    }

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}