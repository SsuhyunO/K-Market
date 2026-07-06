package org.example.k_market.service;

import lombok.RequiredArgsConstructor;
import org.example.k_market.repository.cart.CartRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
}
