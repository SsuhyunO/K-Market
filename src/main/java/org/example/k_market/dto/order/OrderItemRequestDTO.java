package org.example.k_market.dto.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemRequestDTO {
    @NotNull(message = "상품 옵션을 선택해주세요.")
    @Min(value = 1, message = "상품 옵션 번호가 올바르지 않습니다.")
    private Integer prodVariantId;

    @NotNull(message = "상품 수량을 입력해주세요.")
    @Min(value = 1, message = "상품 수량은 1개 이상이어야 합니다.")
    private Integer count;
}
