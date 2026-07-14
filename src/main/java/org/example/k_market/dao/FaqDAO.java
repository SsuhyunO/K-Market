package org.example.k_market.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.k_market.dto.admin.FaqDTO;

import java.util.List;

@Mapper
public interface FaqDAO {

    /**
     * FAQ 전체/1차/2차 유형별 개수
     */
    int getTotalCount(
            @Param("category1") String category1,
            @Param("category2") String category2
    );

    /**
     * FAQ 전체/1차/2차 유형별 목록
     */
    List<FaqDTO> getFaqList(
            @Param("offset") int offset,
            @Param("pageSize") int pageSize,
            @Param("category1") String category1,
            @Param("category2") String category2
    );

    /**
     * FAQ 상세 조회
     */
    FaqDTO getFaqByNo(
            @Param("boardNo") int boardNo
    );

    /**
     * FAQ 등록
     */
    int insertFaq(FaqDTO dto);

    /**
     * FAQ 수정
     */
    int updateFaq(FaqDTO dto);

    /**
     * FAQ 단일 삭제
     */
    int deleteFaq(
            @Param("boardNo") int boardNo
    );

    /**
     * FAQ 선택 삭제
     */
    int deleteFaqs(
            @Param("boardNoList")
            List<Integer> boardNoList
    );
}