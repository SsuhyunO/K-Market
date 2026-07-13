package org.example.k_market.common.product;

import java.util.Set;

public final class ProductCommonNoticeKeys {
    private static final Set<String> KEYS = Set.of(
        "productName",
        "modelName",
        "title",
        "manufacturer",
        "producer",
        "brand",
        "origin"
    );

    private ProductCommonNoticeKeys() {
    }

    public static boolean contains(String key) {
        return KEYS.contains(key);
    }

    public static Set<String> all() {
        return KEYS;
    }
}
