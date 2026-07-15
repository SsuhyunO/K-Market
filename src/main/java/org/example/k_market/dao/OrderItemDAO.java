package org.example.k_market.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.k_market.dto.order.OrderItemDTO;
import org.example.k_market.dto.order.response.MyOrderItemResponse;
import org.example.k_market.dto.order.response.OrderLineResponse;

import java.util.List;

@Mapper
public interface OrderItemDAO {
    void insertOrderItems(@Param("items") List<OrderItemDTO> items);

    List<OrderItemDTO> selectOrderItemsByOrderNo(int orderNo);

    List<OrderLineResponse> selectOrderLinesByOrderNo(@Param("orderNo") int orderNo,
                                                       @Param("sellerUid") String sellerUid);

    List<OrderLineResponse> selectShippableOrderLinesByOrderNo(@Param("orderNo") int orderNo,
                                                               @Param("sellerUid") String sellerUid);

    List<OrderLineResponse> selectOrderLinesByItemNos(@Param("orderItemNos") List<Integer> orderItemNos,
                                                      @Param("sellerUid") String sellerUid);

    int updateOrderItemsStatus(@Param("orderItemNos") List<Integer> orderItemNos,
                               @Param("itemStatus") String itemStatus);

    int updateOrderItemStatus(@Param("orderItemNo") int orderItemNo,
                              @Param("itemStatus") String itemStatus);

    int updateOrderItemsStatusByOrderNo(@Param("orderNo") int orderNo,
                                        @Param("itemStatus") String itemStatus);

    List<MyOrderItemResponse> selectMyOrderItems(@Param("memberUid") String memberUid,
                                                 @Param("startDate") String startDate,
                                                 @Param("endDate") String endDate,
                                                 @Param("offset") int offset,
                                                 @Param("limit") int limit);

    MyOrderItemResponse selectMyOrderItemByOrderItemNo(@Param("memberUid") String memberUid,
                                                       @Param("orderItemNo") int orderItemNo);

    int selectMyOrderItemCount(@Param("memberUid") String memberUid,
                               @Param("startDate") String startDate,
                               @Param("endDate") String endDate);
}
