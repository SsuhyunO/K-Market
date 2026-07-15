package org.example.k_market.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.k_market.dto.order.ShipmentDTO;
import org.example.k_market.dto.order.ShipmentItemDTO;
import org.example.k_market.dto.order.response.OrderLineResponse;
import org.example.k_market.dto.order.response.ShipmentListResponse;

import java.util.List;

@Mapper
public interface ShipmentDAO {
    int updateShipmentsStatusByOrderNo(@Param("orderNo") int orderNo,
                                       @Param("status") String status);

    int updateReadyShipmentsToShipping();

    int updateShippingShipmentsToDelivered();

    int updateReturnShippingShipmentsToReturned();

    int updateExchangeShippingShipmentsToWaiting();

    int updateOrderItemsStatusByShipmentStatus(@Param("shipmentStatus") String shipmentStatus,
                                               @Param("itemStatus") String itemStatus);

    List<Integer> selectOrderNosByShipmentStatus(@Param("shipmentStatus") String shipmentStatus);

    int updateShipmentStatusByOrderItemNo(@Param("orderItemNo") int orderItemNo,
                                          @Param("status") String status);

    void insertShipment(ShipmentDTO shipment);

    void insertShipmentItems(@Param("items") List<ShipmentItemDTO> items);

    List<ShipmentListResponse> selectShipments(@Param("searchType") String searchType,
                                               @Param("keyword") String keyword,
                                               @Param("sellerUid") String sellerUid,
                                               @Param("offset") int offset,
                                               @Param("limit") int limit);

    int selectShipmentCount(@Param("searchType") String searchType,
                            @Param("keyword") String keyword,
                            @Param("sellerUid") String sellerUid);

    ShipmentListResponse selectShipmentByNo(@Param("shipmentNo") int shipmentNo,
                                            @Param("sellerUid") String sellerUid);

    List<OrderLineResponse> selectShipmentLines(@Param("shipmentNo") int shipmentNo,
                                                @Param("sellerUid") String sellerUid);
}
