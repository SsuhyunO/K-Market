package org.example.k_market.service.shop;

import org.example.k_market.dto.shop.SalesSearchRequest;
import org.example.k_market.dto.shop.SalesStatusResult;

public interface SalesService {
    SalesStatusResult getSalesStatus(SalesSearchRequest request);
}