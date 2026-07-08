package org.example.k_market.service.pagination;

import java.util.List;

public interface PageQuery<T> {
    List<T> fetch(int offset, int size);
    int count();
}
