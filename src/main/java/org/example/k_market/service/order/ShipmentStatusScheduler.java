package org.example.k_market.service.order;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShipmentStatusScheduler {

    private final OrderService orderService;

    @Scheduled(fixedDelay = 10_000)
    public void advanceShipmentStatuses() {
        orderService.advanceShipmentStatuses();
    }
}
