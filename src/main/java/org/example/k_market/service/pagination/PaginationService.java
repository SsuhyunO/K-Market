package org.example.k_market.service.pagination;

import org.example.k_market.dto.pagination.request.PageRequest;
import org.example.k_market.dto.pagination.response.PageResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaginationService {
    public <T> PageResponse<T> getPageInfo(PageRequest request, int size, int blockSize, PageQuery<T> pageQuery) {
        int total = pageQuery.count();
        int rowSize = size > 0 ? size : 10;
        int totalPage = (int)Math.ceil((double)total / rowSize);
        int currentPage = Math.max(request.getPage(), 1);
        if (totalPage > 0) {
            currentPage = Math.min(currentPage, totalPage);
        }
        int offset = (currentPage - 1) * rowSize;
        int startElementNum = total - (currentPage - 1) * rowSize; // 게시글이 55개 있을때 기준 첫번째 페이지의 첫번째 글은 55번글..
        int startPageNum = (currentPage - 1) / blockSize * blockSize + 1; // 현재 페이지 그룹의 시작 페이지 번호
        int lastPageNum = Math.min(startPageNum + blockSize - 1, totalPage); // 현재 페이지 그룹의 마지막 페이지 번호
        boolean hasNext = lastPageNum < totalPage; // 이전 페이지 그룹이 있는가?
        boolean hasPrev = startPageNum > 1; // 다음 페이지 그룹이 있는가?
        List<T> content = pageQuery.fetch(offset, rowSize);

        return PageResponse.<T>builder()
            .list(content)
            .startNo(startElementNum)
            .page(currentPage)
            .size(rowSize)
            .totalPage(totalPage)
            .totalElements(total)
            .startPage(startPageNum)
            .lastPage(lastPageNum)
            .hasNext(hasNext)
            .hasPrev(hasPrev)
            .build();
    }
}
