package org.example.k_market.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.k_market.dto.order.OrderDTO;
import org.example.k_market.dto.order.OrderItemDTO;
import org.example.k_market.dto.order.ShipmentDTO;
import org.example.k_market.dto.order.ShipmentItemDTO;
import org.example.k_market.dto.order.response.MyOrderItemResponse;
import org.example.k_market.dto.order.response.OrderLineResponse;
import org.example.k_market.dto.order.response.ShipmentListResponse;

import java.util.List;

@Mapper
public interface OrderDAO {
    // insert 후 order.setOrderNo(...)가 자동으로 채워짐 (useGeneratedKeys)
    void insertOrder(OrderDTO order);

    void insertOrderItems(@Param("items") List<OrderItemDTO> items);

    OrderDTO selectOrderByNo(int orderNo);
    List<OrderItemDTO> selectOrderItemsByOrderNo(int orderNo);

    int selectOrderCount(@Param("searchType") String searchType,
                         @Param("keyword") String keyword);

    List<OrderDTO> selectOrders(@Param("searchType") String searchType,
                                @Param("keyword") String keyword,
                                @Param("offset") int offset,
                                @Param("limit") int limit);

    List<OrderLineResponse> selectOrderLinesByOrderNo(int orderNo);

    List<OrderLineResponse> selectShippableOrderLinesByOrderNo(int orderNo);

    List<OrderLineResponse> selectOrderLinesByItemNos(@Param("orderItemNos") List<Integer> orderItemNos);

    int updateOrderItemsStatus(@Param("orderItemNos") List<Integer> orderItemNos,
                               @Param("itemStatus") String itemStatus);

    int updateOrderItemStatus(@Param("orderItemNo") int orderItemNo,
                              @Param("itemStatus") String itemStatus);

    int updateOrderStatus(@Param("orderNo") int orderNo,
                          @Param("status") String status);

    void insertShipment(ShipmentDTO shipment);

    void insertShipmentItems(@Param("items") List<ShipmentItemDTO> items);

    List<ShipmentListResponse> selectShipments(@Param("searchType") String searchType,
                                               @Param("keyword") String keyword,
                                               @Param("offset") int offset,
                                               @Param("limit") int limit);

    int selectShipmentCount(@Param("searchType") String searchType,
                            @Param("keyword") String keyword);

    ShipmentListResponse selectShipmentByNo(int shipmentNo);

    List<OrderLineResponse> selectShipmentLines(int shipmentNo);

    List<MyOrderItemResponse> selectMyOrderItems(@Param("memberUid") String memberUid,
                                                 @Param("startDate") String startDate,
                                                 @Param("endDate") String endDate,
                                                 @Param("offset") int offset,
                                                 @Param("limit") int limit);

    int selectMyOrderItemCount(@Param("memberUid") String memberUid,
                               @Param("startDate") String startDate,
                               @Param("endDate") String endDate);
}
