package org.example.k_market.dto.admin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OperationCardDTO {
    private int paid;               // 결제완료
    private int ready;               // 배송준비
    private int cancelRequested;     // 취소요청
    private int exchangeRequested;   // 교환요청
    private int returnRequested;     // 반품요청
}
