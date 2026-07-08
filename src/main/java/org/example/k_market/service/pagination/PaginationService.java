package org.example.k_market.service.pagination;

import org.example.k_market.dto.response.PageResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaginationService {
    public <T> PageResponse<T> getPageInfo(int page, int size, int pageSize, PageQuery<T> pageQuery) {
        int total = pageQuery.count();
        int totalPage = total / size;
        int startElementNum = total - (page - 1) * size; // 게시글이 55개 있을때 기준 첫번째 페이지의 첫번째 글은 55번글..
        int lastElementNum = startElementNum + size;
        int startPageNum; // 현재 페이지 그룹의 시작 페이지 번호
        int lastPageNum; // 현재 페이지 그룹의 마지막 페이지 번호
        boolean hasNext; // 이전 페이지 그룹이 있는가?
        boolean hasPrev; // 다음 페이지 그룹이 있는가?

        return null;
    }
}
