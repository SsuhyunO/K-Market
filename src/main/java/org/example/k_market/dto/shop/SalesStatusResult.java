package org.example.k_market.dto.shop;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.k_market.util.PageInfo;

import java.util.List;

@Getter
@AllArgsConstructor
public class SalesStatusResult {
    private List<SalesStatusDTO> items;
    private PageInfo pageInfo;
}