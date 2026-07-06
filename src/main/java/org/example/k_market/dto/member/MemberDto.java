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

    // 회원 정보 응답 (비밀번호 제외!)
    @Getter
    @Builder
    public static class Response {
        private String uid;
        private String name;
        private String email;
        private String phone;
        private String memberType;
        private Integer memberLevel;
        private Integer pointBalance;
        private String zipCode;
        private String addr1;
        private String addr2;

        public static Response from(Member member) {
            return Response.builder()
                    .uid(member.getUid())
                    .name(member.getName())
                    .email(member.getEmail())
                    .phone(member.getPhone())
                    .memberType(member.getMemberType())
                    .memberLevel(member.getMemberLevel())
                    .pointBalance(member.getPointBalance())
                    .zipCode(member.getZipCode())
                    .addr1(member.getAddr1())
                    .addr2(member.getAddr2())
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
}