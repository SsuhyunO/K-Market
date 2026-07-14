package org.example.k_market.dto.order.response;

import lombok.*;
import org.example.k_market.dto.order.OrderDTO;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagementOrderListResponse {
    private int orderNo;
    private String memberUid;
    private String memberName;
    private int totalCount;
    private int orderTotal;
    private String payMethod;
    private String status;
    private String createdAt;

    public static ManagementOrderListResponse from(OrderDTO order) {
        return ManagementOrderListResponse.builder()
            .orderNo(order.getOrderNo())
            .memberUid(order.getMemberUid())
            .memberName(order.getMemberName())
            .totalCount(order.getTotalCount())
            .orderTotal(order.getOrderTotal())
            .payMethod(order.getPayMethod())
            .status(order.getStatus())
            .createdAt(order.getCreatedAt())
            .build();
    }
}
