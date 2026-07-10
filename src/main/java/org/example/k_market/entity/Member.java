package org.example.k_market.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

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

    @Column(name = "password", length = 255, nullable = true)
    private String password; // 구글 로그인 유저는 null

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

    // ===== status 값 정리 =====
    // "ACTIVE"    : 정상 회원 (일반가입 완료, 또는 구글가입 후 추가정보 입력완료)
    // "WITHDRAWN" : 탈퇴 회원
    // "PENDING"   : 구글 최초가입 후 추가정보(생년월일/전화번호 등) 입력 전 상태
    @Column(name = "status", length = 20, nullable = false)
    private String status;

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

    // ===== 구글 로그인용 추가 필드 =====
    @Column(name = "provider", length = 20, nullable = false)
    private String provider; // "LOCAL" (기본값) / "GOOGLE"

    @Column(name = "provider_id", length = 100)
    private String providerId; // 구글이 주는 고유 sub 값

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.pointBalance == null) this.pointBalance = 0;
        if (this.memberLevel == null) this.memberLevel = 1;
        if (this.status == null) this.status = "ACTIVE";
        if (this.provider == null) this.provider = "LOCAL";
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

    // ===== 추가된 부분: 구글 최초가입 후 추가정보 입력 대기중인지 여부 =====
    public boolean isProfilePending() {
        return "PENDING".equals(this.status);
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

    // ===== 구글 로그인 유저 생성용 정적 팩토리 메서드 =====
    // uid는 PK이자 String이라, 구글 로그인 유저는 임의로 생성해서 채워넣습니다.
    // "google_" + UUID 앞 12자리 형태로 생성 (예: google_3f9a1b2c4d5e)
    // status를 PENDING으로 생성 -> 추가정보(생년월일/성별/전화번호/주소) 입력 전까지는 미완성 상태
    public static Member createGoogleMember(String email, String name, String providerId) {
        String generatedUid = "google_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);

        return Member.builder()
                .uid(generatedUid)
                .password(null)
                .name(name != null ? name : "구글사용자")
                .email(email)
                .memberType("MEMBER")
                .status("PENDING")
                .provider("GOOGLE")
                .providerId(providerId)
                .build();
    }

    // ===== 구글 로그인 정보 연동 (이미 LOCAL로 가입된 이메일과 매칭될 때) =====
    public void linkGoogleProvider(String providerId) {
        this.provider = "GOOGLE";
        this.providerId = providerId;
    }

    // ===== 추가된 부분: 구글 최초가입자의 추가정보 입력 완료 처리 =====
    // 완료 시 status를 ACTIVE로 전환 -> 이후부터 정상회원과 동일하게 취급됨
    public void completeGoogleProfile(String name, String birthDate, String gender, String phone,
                                      String zipCode, String addr1, String addr2) {
        if (name != null && !name.isBlank()) this.name = name;
        this.birthDate = birthDate;
        this.gender = gender;
        this.phone = phone;
        this.zipCode = zipCode;
        this.addr1 = addr1;
        this.addr2 = addr2;
        this.status = "ACTIVE";
    }
}