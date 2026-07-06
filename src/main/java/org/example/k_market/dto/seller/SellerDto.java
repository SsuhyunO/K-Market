package org.example.k_market.dto.seller;

import org.example.k_market.entity.Member;
import org.example.k_market.entity.Seller;
import jakarta.validation.constraints.*;
import lombok.*;

public class SellerDto {

    // 판매자 회원가입 요청 (member + seller 동시 생성)
    @Getter
    @NoArgsConstructor
    public static class RegisterRequest {

        // --- 회원(Member) 정보 ---
        @NotBlank(message = "아이디는 필수입니다.")
        @Size(min = 4, max = 20)
        private String uid;

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 8, max = 20)
        private String password;

        @NotBlank(message = "대표자명은 필수입니다.")
        private String name;

        @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다.")
        private String phone;

        @NotBlank
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        private String email;

        private String zipCode;
        private String addr1;
        private String addr2;

        // --- 판매자(Seller) 정보 ---
        @NotBlank(message = "상호명은 필수입니다.")
        private String companyName;

        @NotBlank(message = "사업자등록번호는 필수입니다.")
        private String bizRegNo;

        private String onlineSalesNo;
        private String tel;
        private String fax;

        public Member toMemberEntity(String encodedPassword, String regIp) {
            return Member.builder()
                    .uid(uid)
                    .password(encodedPassword)
                    .name(name)
                    .phone(phone)
                    .email(email)
                    .zipCode(zipCode)
                    .addr1(addr1)
                    .addr2(addr2)
                    .memberType("SELLER")
                    .regIp(regIp)
                    .build();
        }

        public Seller toSellerEntity() {
            return Seller.builder()
                    .uid(uid)
                    .companyName(companyName)
                    .bizRegNo(bizRegNo)
                    .onlineSalesNo(onlineSalesNo)
                    .tel(tel)
                    .fax(fax)
                    .build();
        }
    }

    // 판매자 정보 응답
    @Getter
    @Builder
    public static class Response {
        private String uid;
        private String companyName;
        private String bizRegNo;
        private String onlineSalesNo;
        private String tel;
        private String fax;

        public static Response from(Seller seller) {
            return Response.builder()
                    .uid(seller.getUid())
                    .companyName(seller.getCompanyName())
                    .bizRegNo(seller.getBizRegNo())
                    .onlineSalesNo(seller.getOnlineSalesNo())
                    .tel(seller.getTel())
                    .fax(seller.getFax())
                    .build();
        }
    }
}