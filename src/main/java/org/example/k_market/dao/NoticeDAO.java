package org.example.k_market.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.k_market.dto.admin.NoticeDTO;

import java.util.List;

@Mapper
public interface NoticeDAO {

    /**
     * 공지사항 전체 또는 유형별 개수
     */
    int getTotalCount(
            @Param("type") String type
    );

    /**
     * 공지사항 전체 또는 유형별 목록
     */
    List<NoticeDTO> getNoticeList(
            @Param("offset") int offset,
            @Param("pageSize") int pageSize,
            @Param("type") String type
    );

    /**
     * 공지사항 상세
     */
    NoticeDTO getNoticeByNo(
            @Param("boardNo") int boardNo
    );

    /**
     * 공지사항 등록
     */
    int insertNotice(NoticeDTO dto);

    /**
     * 공지사항 수정
     */
    int updateNotice(NoticeDTO dto);

    /**
     * 공지사항 단일 삭제
     */
    int deleteNotice(
            @Param("boardNo") int boardNo
    );

    /**
     * 공지사항 선택 삭제
     */
    int deleteNotices(
            @Param("boardNoList")
            List<Integer> boardNoList
    );
}