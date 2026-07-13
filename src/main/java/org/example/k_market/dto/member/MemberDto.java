package org.example.k_market.dto.member;

import org.example.k_market.entity.Member;
import jakarta.validation.constraints.*;
import lombok.*;

public class MemberDto {

    // 회원가입 요청
    @Getter
    @NoArgsConstructor
    public static class SignUpRequest {

        @NotBlank(message = "아이디는 필수입니다.")
        @Size(min = 4, max = 20, message = "아이디는 4~20자여야 합니다.")
        private String uid;

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 8, max = 20, message = "비밀번호는 8~20자여야 합니다.")
        private String password;

        @NotBlank
        private String name;

        private String birthDate;
        private String gender;

        @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다.")
        private String phone;

        @Email(message = "이메일 형식이 올바르지 않습니다.")
        private String email;

        private String zipCode;
        private String addr1;
        private String addr2;

        public Member toEntity(String encodedPassword, String memberType, String regIp) {
            return Member.builder()
                    .uid(uid)
                    .password(encodedPassword)
                    .name(name)
                    .birthDate(birthDate)
                    .gender(gender)
                    .phone(phone)
                    .email(email)
                    .zipCode(zipCode)
                    .addr1(addr1)
                    .addr2(addr2)
                    .memberType(memberType)
                    .regIp(regIp)
                    .build();
        }
    }

    // 로그인 요청
    @Getter
    @NoArgsConstructor
    public static class LoginRequest {

        @NotBlank(message = "아이디를 입력하세요.")
        private String uid;

        @NotBlank(message = "비밀번호를 입력하세요.")
        private String password;

        // ===== 추가된 부분: 자동로그인 체크박스 값 =====
        private boolean autoLogin;
    }

    // 아이디 중복확인 요청
    @Getter
    @NoArgsConstructor
    public static class CheckUidRequest {

        @NotBlank(message = "아이디를 입력하세요.")
        private String uid;
    }

    // 아이디 찾기 요청 (이름 + 이메일로 찾는다고 가정)
    @Getter
    @NoArgsConstructor
    public static class FindUidRequest {

        @NotBlank
        private String name;

        @NotBlank
        @Email
        private String email;
    }

    // 비밀번호 찾기(재설정) 요청
    @Getter
    @NoArgsConstructor
    public static class FindPasswordRequest {

        @NotBlank
        private String uid;

        @NotBlank
        @Email
        private String email;
    }

    // 새 비밀번호 설정 요청
    @Getter
    @NoArgsConstructor
    public static class ResetPasswordRequest {

        @NotBlank
        private String uid;

        @NotBlank
        @Size(min = 8, max = 20)
        private String newPassword;
    }

    // 이메일 인증번호 발송 요청
    @Getter
    @NoArgsConstructor
    public static class EmailAuthRequest {

        @NotBlank
        @Email
        private String email;
    }

    // 이메일 인증번호 확인 요청
    @Getter
    @NoArgsConstructor
    public static class EmailAuthVerifyRequest {

        @NotBlank
        @Email
        private String email;

        @NotBlank(message = "인증번호를 입력하세요.")
        private String authCode;
    }

    // 아이디 찾기 결과 (화면 표시용)
    @Getter
    @Builder
    public static class FindUidResult {
        private String name;
        private String uid;
        private String email;
        private String regDate;

        public static FindUidResult from(Member member) {
            return FindUidResult.builder()
                    .name(member.getName())
                    .uid(member.getUid())
                    .email(member.getEmail())
                    .regDate(member.getCreatedAt() != null
                            ? member.getCreatedAt().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                            : "")
                    .build();
        }
    }

    // 회원 정보 응답 (비밀번호 제외!)
    @Getter
    @Builder
    public static class Response {
        private String uid;
        private String name;
        private String birthDate;
        private String email;
        private String phone;
        private String memberType;
        private Integer memberLevel;
        private Integer pointBalance;
        private String zipCode;
        private String addr1;
        private String addr2;
        private String provider;

        public static Response from(Member member) {
            return Response.builder()
                    .uid(member.getUid())
                    .name(member.getName())
                    .birthDate(member.getBirthDate())
                    .email(member.getEmail())
                    .phone(member.getPhone())
                    .memberType(member.getMemberType())
                    .memberLevel(member.getMemberLevel())
                    .pointBalance(member.getPointBalance())
                    .zipCode(member.getZipCode())
                    .addr1(member.getAddr1())
                    .addr2(member.getAddr2())
                    .provider(member.getProvider())
                    .build();
        }
    }

    // 회원정보 수정 요청
    @Getter
    @NoArgsConstructor
    public static class UpdateRequest {
        private String name;
        private String phone;
        private String email;
        private String zipCode;
        private String addr1;
        private String addr2;
    }

    // ===== 관리자 회원목록 화면용 응답 =====
    @Getter
    @Builder
    public static class AdminListItem {
        private int no;                  // 화면용 순번 (DB PK 아님)
        private String uid;
        private String name;
        private String gender;
        private String gradeLabel;       // "VVIP" 등 변환된 값
        private Integer memberLevel;     // 원본 숫자값 (드롭다운 변경 시 필요)
        private Integer pointBalance;
        private String email;
        private String phone;
        private String zipCode;
        private String address;          // addr1
        private String detailAddress;    // addr2
        private String createdAt;
        private String lastLoginAt;
        private String note;
        private String status;
        private String statusLabel;      // "정상"/"탈퇴" 변환된 값

        public static AdminListItem from(Member member, int no) {
            return AdminListItem.builder()
                    .no(no)
                    .uid(member.getUid())
                    .name(member.getName())
                    .gender(member.getGender())
                    .gradeLabel(toGradeLabel(member.getMemberLevel()))
                    .memberLevel(member.getMemberLevel())
                    .pointBalance(member.getPointBalance())
                    .email(member.getEmail())
                    .phone(member.getPhone())
                    .zipCode(member.getZipCode())
                    .address(member.getAddr1())
                    .detailAddress(member.getAddr2())
                    .createdAt(format(member.getCreatedAt()))
                    .lastLoginAt(format(member.getLastLoginAt()))
                    .note(member.getNote())
                    .status(member.getStatus())
                    .statusLabel("ACTIVE".equals(member.getStatus()) ? "정상" : "탈퇴")
                    .build();
        }

        private static String format(java.time.LocalDateTime dt) {
            return dt != null
                    ? dt.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    : "";
        }

        // 등급 매핑 기준 (추후 팀 협의 시 숫자만 조정)
        // 1=FAMILY, 2=SILVER, 3=GOLD, 4=VIP, 5=VVIP
        private static String toGradeLabel(Integer level) {
            if (level == null) return "FAMILY";
            return switch (level) {
                case 5 -> "VVIP";
                case 4 -> "VIP";
                case 3 -> "GOLD";
                case 2 -> "SILVER";
                default -> "FAMILY";
            };
        }
    }

    // ===== 관리자 회원정보 수정 요청 =====
    @Getter
    @NoArgsConstructor
    public static class AdminUpdateRequest {
        private String uid;            // 수정 대상 식별용 (필수)
        private String name;
        private String gender;
        private String email;
        private String phone;
        private String zipCode;
        private String address;
        private String detailAddress;
        private String note;
    }

    // ===== 관리자 등급 즉시변경 요청 =====
    @Getter
    @NoArgsConstructor
    public static class AdminGradeUpdateRequest {
        private String uid;
        private Integer memberLevel;
    }
}