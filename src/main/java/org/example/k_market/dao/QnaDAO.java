package org.example.k_market.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.k_market.dto.admin.QnaDTO;

import java.util.List;

@Mapper
public interface QnaDAO {

    /* =========================================================
       관리자 문의 목록
       ========================================================= */

    /**
     * 문의글 전체 개수 조회
     */
    int getTotalCount(
            @Param("category1") String category1,
            @Param("category2") String category2
    );


    /**
     * 문의글 목록 조회
     */
    List<QnaDTO> getQnaList(
            @Param("offset") int offset,
            @Param("pageSize") int pageSize,
            @Param("category1") String category1,
            @Param("category2") String category2
    );


    /* =========================================================
       문의 상세
       ========================================================= */

    /**
     * 문의글 한 건 조회
     */
    QnaDTO getQnaByNo(
            @Param("boardNo") int boardNo
    );


    /* =========================================================
       마이페이지 문의 조회
       ========================================================= */

    /**
     * 로그인 회원의 문의글 전체 조회
     */
    List<QnaDTO> getQnaListByMemberUid(
            @Param("memberUid") String memberUid
    );


    /**
     * 로그인 회원의 최근 문의글 5개 조회
     */
    List<QnaDTO> getRecentQnaListByMemberUid(
            @Param("memberUid") String memberUid
    );


    /* =========================================================
       문의 등록
       ========================================================= */

    /**
     * 사용자 문의글 등록
     */
    int insertQna(QnaDTO dto);


    /* =========================================================
       관리자 답변
       ========================================================= */

    /**
     * 문의 답변 등록 및 수정
     */
    int updateAnswer(
            @Param("boardNo") int boardNo,
            @Param("answer") String answer
    );


    /**
     * 문의 답변만 삭제
     */
    int deleteAnswer(
            @Param("boardNo") int boardNo
    );


    /* =========================================================
       문의 삭제
       ========================================================= */

    /**
     * 문의글 단일 및 선택 삭제
     */
    int deleteQnas(
            @Param("boardNoList") List<Integer> boardNoList
    );
}