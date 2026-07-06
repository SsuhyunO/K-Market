package org.example.k_market.service;

import lombok.RequiredArgsConstructor;
import org.example.k_market.repository.order.OrderRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository repository;
}
