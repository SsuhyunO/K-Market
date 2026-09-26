package org.example.k_market.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderCreateRequestDTO {
    @NotBlank(message = "받는 분을 입력해주세요.")
    private String receiver;

    @NotBlank(message = "휴대폰 번호를 입력해주세요.")
    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "휴대폰 번호 형식이 올바르지 않습니다.")
    private String phone;

    @NotBlank(message = "우편번호를 입력해주세요.")
    private String zipCode;

    @NotBlank(message = "주소를 입력해주세요.")
    private String addr1;
    private String addr2;
    private String orderNote;

    @NotBlank(message = "결제수단을 선택해주세요.")
    @Pattern(regexp = "^(CARD|CHECK_CARD|BANK|VBANK|PHONE|KAKAO)$", message = "지원하지 않는 결제수단입니다.")
    private String payMethod;

    @Min(value = 1, message = "쿠폰 번호가 올바르지 않습니다.")
    private Integer couponIssueId;

    @Min(value = 1, message = "쿠폰 적용 상품이 올바르지 않습니다.")
    private Integer targetVariantId; // PRODUCT 타입 쿠폰일 때 적용 대상 상품 (nullable)

    @Min(value = 0, message = "사용 포인트는 0 이상이어야 합니다.")
    private Integer usedPoints;

    @Valid
    @NotEmpty(message = "주문할 상품이 없습니다.")
    private List<OrderItemRequestDTO> items;
}
