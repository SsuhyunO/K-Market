package org.example.k_market.dto.seller;

import org.example.k_market.entity.Member;
import org.example.k_market.entity.Seller;
import org.example.k_market.util.PageInfo;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

public class ShopDto {

    // 상점등록 요청 (member + seller 동시 생성)
    @Getter
    @NoArgsConstructor
    public static class RegisterRequest {

        @NotBlank(message = "아이디는 필수입니다.")
        private String userId;

        @NotBlank(message = "비밀번호는 필수입니다.")
        private String password;

        @NotBlank(message = "상호명은 필수입니다.")
        private String businessName;

        @NotBlank(message = "대표자명은 필수입니다.")
        private String ceo;

        @NotBlank(message = "사업자등록번호는 필수입니다.")
        private String businessRegistrationNumber;

        private String mailOrderBusinessNumber;
        private String phone;
        private String fax;
        private String zipCode;
        private String defaultAddress;
        private String detailAddress;

        public Member toMemberEntity(String encodedPassword, String regIp) {
            return Member.builder()
                    .uid(userId)
                    .password(encodedPassword)
                    .name(ceo)
                    .phone(phone)
                    .zipCode(zipCode)
                    .addr1(defaultAddress)
                    .addr2(detailAddress)
                    .memberType("SELLER")
                    .regIp(regIp)
                    .build();
        }

        public Seller toSellerEntity() {
            return Seller.builder()
                    .uid(userId)
                    .companyName(businessName)
                    .bizRegNo(businessRegistrationNumber)
                    .onlineSalesNo(mailOrderBusinessNumber)
                    .tel(phone)
                    .fax(fax)
                    .status("PENDING")
                    .build();
        }
    }

    // 상태변경 요청 (승인/중단/재개)
    @Getter
    @NoArgsConstructor
    public static class StatusRequest {
        @NotBlank(message = "상태값은 필수입니다.")
        private String status; // ACTIVE / SUSPENDED
    }

    // 상점목록 화면 표시용 개별 행
    @Getter
    @Builder
    public static class ListItem {
        private int no;
        private String uid;
        private String companyName;
        private String ceo;
        private String bizRegNo;
        private String onlineSalesNo;
        private String tel;
        private String status;
        private String statusLabel;

        public static ListItem from(int no, Seller seller, String ceoName) {
            return ListItem.builder()
                    .no(no)
                    .uid(seller.getUid())
                    .companyName(seller.getCompanyName())
                    .ceo(ceoName)
                    .bizRegNo(seller.getBizRegNo())
                    .onlineSalesNo(seller.getOnlineSalesNo())
                    .tel(seller.getTel())
                    .status(seller.getStatus())
                    .statusLabel(toStatusLabel(seller.getStatus()))
                    .build();
        }

        private static String toStatusLabel(String status) {
            return switch (status) {
                case "ACTIVE" -> "운영중";
                case "SUSPENDED" -> "운영중지";
                default -> "운영준비";
            };
        }
    }

    // 상점목록 + 페이지 정보 (컨트롤러 반환용)
    @Getter
    @AllArgsConstructor
    public static class ListResult {
        private List<ListItem> items;
        private PageInfo pageInfo;
    }
}