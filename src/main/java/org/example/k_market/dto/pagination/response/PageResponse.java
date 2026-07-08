package org.example.k_market.dto.pagination.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponse<T> {
    private List<T> list;

    private int page; // 현재 페이지
    private int startNo;
    private int size;
    private int totalPage;
    private int totalElements;
    private int startPage;
    private int lastPage;
    private boolean hasNext; // 이전 페이지 그룹이 있는가?
    private boolean hasPrev; // 다음 페이지 그룹이 있는가?
}
