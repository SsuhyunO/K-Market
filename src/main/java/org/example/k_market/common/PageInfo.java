package org.example.k_market.dto.common;

import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class PageInfo {

    private final int currentPage;   // 1부터 시작 (화면 표시용)
    private final int totalPages;
    private final int startPage;
    private final int endPage;

    private static final int PAGE_BLOCK_SIZE = 10; // 하단에 보여줄 페이지 번호 개수

    public PageInfo(Page<?> page) {
        this.currentPage = page.getNumber() + 1; // Spring Data Page는 0부터 시작하므로 +1
        this.totalPages = Math.max(page.getTotalPages(), 1);

        this.startPage = ((currentPage - 1) / PAGE_BLOCK_SIZE) * PAGE_BLOCK_SIZE + 1;
        this.endPage = Math.min(startPage + PAGE_BLOCK_SIZE - 1, totalPages);
    }
}