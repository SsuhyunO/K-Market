package org.example.k_market.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.k_market.dto.order.OrderDTO;
import org.example.k_market.dto.order.OrderItemDTO;

import java.util.List;

@Mapper
public interface OrderDAO {
    // insert 후 order.setOrderNo(...)가 자동으로 채워짐 (useGeneratedKeys)
    void insertOrder(OrderDTO order);

    void insertOrderItems(@Param("items") List<OrderItemDTO> items);
}
