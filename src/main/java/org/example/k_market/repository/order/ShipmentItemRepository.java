package org.example.k_market.repository.order;

import org.example.k_market.entity.order.ShipmentItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentItemRepository extends JpaRepository<ShipmentItem, Integer> {
}
