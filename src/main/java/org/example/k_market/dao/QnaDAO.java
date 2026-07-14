package org.example.k_market.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.k_market.dto.admin.QnaDTO;

import java.util.List;

@Mapper
public interface QnaDAO {

    /**
     * 문의글 전체 개수
     */
    int getTotalCount(
            @Param("category1") String category1,
            @Param("category2") String category2
    );

    /**
     * 문의글 목록
     */
    List<QnaDTO> getQnaList(
            @Param("offset") int offset,
            @Param("pageSize") int pageSize,
            @Param("category1") String category1,
            @Param("category2") String category2
    );

    /**
     * 문의글 한 건 조회
     */
    QnaDTO getQnaByNo(
            @Param("boardNo") int boardNo
    );


    /**
     * 사용자 문의글 등록
     */
    int insertQna(QnaDTO dto);

    
    /**
     * 관리자 답변 등록 및 수정
     */
    int updateAnswer(
            @Param("boardNo") int boardNo,
            @Param("answer") String answer
    );

    /**
     * 문의글 단일 및 선택 삭제
     */
    int deleteQnas(
            @Param("boardNoList") List<Integer> boardNoList
    );
}