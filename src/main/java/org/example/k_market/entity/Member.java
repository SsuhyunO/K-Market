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

    @Column(name = "status", length = 20, nullable = false)
    private String status; // "ACTIVE" (기본값) / "WITHDRAWN"

    @Column(name = "withdrawnAt")
    private LocalDateTime withdrawnAt;

    @Column(name = "autoLoginToken", length = 100)
    private String autoLoginToken;

    @Column(name = "autoLoginExpireAt")
    private LocalDateTime autoLoginExpireAt;

    // ===== 추가된 부분: 최근 로그인 시각 =====
    @Column(name = "lastLoginAt")
    private LocalDateTime lastLoginAt;

    // ===== 추가된 부분: 관리자 메모 =====
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.pointBalance == null) this.pointBalance = 0;
        if (this.memberLevel == null) this.memberLevel = 1;
        if (this.status == null) this.status = "ACTIVE";
    }

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void withdraw() {
        this.status = "WITHDRAWN";
        this.withdrawnAt = LocalDateTime.now();
    }

    public boolean isWithdrawn() {
        return "WITHDRAWN".equals(this.status);
    }

    public void issueAutoLoginToken(String token, LocalDateTime expireAt) {
        this.autoLoginToken = token;
        this.autoLoginExpireAt = expireAt;
    }

    public void clearAutoLoginToken() {
        this.autoLoginToken = null;
        this.autoLoginExpireAt = null;
    }

    public void updateProfile(String phone, String zipCode, String addr1, String addr2) {
        if (phone != null) this.phone = phone;
        if (zipCode != null) this.zipCode = zipCode;
        if (addr1 != null) this.addr1 = addr1;
        if (addr2 != null) this.addr2 = addr2;
    }

    // ===== 추가된 부분: 로그인 시각 갱신 =====
    public void updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
    }

    // ===== 추가된 부분: 관리자 화면 수정용 =====
    public void adminUpdate(String name, String gender, String email, String phone,
                            String zipCode, String addr1, String addr2, String note) {
        if (name != null) this.name = name;
        if (gender != null) this.gender = gender;
        if (email != null) this.email = email;
        if (phone != null) this.phone = phone;
        if (zipCode != null) this.zipCode = zipCode;
        if (addr1 != null) this.addr1 = addr1;
        if (addr2 != null) this.addr2 = addr2;
        if (note != null) this.note = note;
    }

    // ===== 추가된 부분: 등급 즉시변경용 =====
    public void changeMemberLevel(Integer memberLevel) {
        this.memberLevel = memberLevel;
    }
}