package org.example.k_market.util;

import lombok.Getter;

@Getter
public class PageInfo {
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int DEFAULT_PAGE_BLOCK_SIZE = 5;

    private int currentPage;
    private int totalPages;
    private int startPage;
    private int endPage;
    private int pageSize;

    // 기본값 사용하는 생성자
    public PageInfo(int currentPage, int totalCount) {
        this(currentPage, totalCount, DEFAULT_PAGE_SIZE, DEFAULT_PAGE_BLOCK_SIZE);
    }

    // 커스텀하고 싶을 때 쓰는 생성자
    public PageInfo(int currentPage, int totalCount, int pageSize, int pageBlockSize) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalPages = (int) Math.ceil((double) totalCount / pageSize);
        this.startPage = ((currentPage - 1) / pageBlockSize) * pageBlockSize + 1;
        this.endPage = Math.min(startPage + pageBlockSize - 1, totalPages);
    }

}
