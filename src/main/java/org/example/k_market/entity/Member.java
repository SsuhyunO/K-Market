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

    // ===== 추가된 부분: 탈퇴 여부 =====
    // 실제 삭제(hard delete) 대신 이 값만 바꾸는 soft delete 방식
    @Column(name = "status", length = 20, nullable = false)
    private String status; // "ACTIVE" (기본값) / "WITHDRAWN"

    @Column(name = "withdrawnAt")
    private LocalDateTime withdrawnAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.pointBalance == null) this.pointBalance = 0;
        if (this.memberLevel == null) this.memberLevel = 1;
        if (this.status == null) this.status = "ACTIVE"; // 추가된 부분
    }

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    // ===== 추가된 부분 =====
    public void withdraw() {
        this.status = "WITHDRAWN";
        this.withdrawnAt = LocalDateTime.now();
    }

    public boolean isWithdrawn() {
        return "WITHDRAWN".equals(this.status);
    }

    // ===== 추가된 부분: 마이페이지 정보수정(휴대폰/주소)용 =====
    // 이메일은 정책상 여기서 수정 불가 -> 파라미터에서 의도적으로 제외
    public void updateProfile(String phone, String zipCode, String addr1, String addr2) {
        if (phone != null) this.phone = phone;
        if (zipCode != null) this.zipCode = zipCode;
        if (addr1 != null) this.addr1 = addr1;
        if (addr2 != null) this.addr2 = addr2;
    }
}