package org.example.k_market.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.k_market.dto.order.OrderDTO;

import java.util.List;

@Mapper
public interface OrderDAO {
    // insert 후 order.setOrderNo(...)가 자동으로 채워짐 (useGeneratedKeys)
    void insertOrder(OrderDTO order);

    OrderDTO selectOrderByNo(int orderNo);

    int selectOrderCount(@Param("searchType") String searchType,
                         @Param("keyword") String keyword,
                         @Param("sellerUid") String sellerUid);

    List<OrderDTO> selectOrders(@Param("searchType") String searchType,
                                @Param("keyword") String keyword,
                                @Param("sellerUid") String sellerUid,
                                @Param("offset") int offset,
                                @Param("limit") int limit);

    int updateOrderStatus(@Param("orderNo") int orderNo,
                          @Param("status") String status);
}
