package org.example.k_market.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.k_market.dto.admin.NoticeDTO;

import java.util.List;

@Mapper
public interface NoticeDAO {

    int getTotalCount();

    List<NoticeDTO> getNoticeList(
            @Param("offset") int offset,
            @Param("pageSize") int pageSize
    );

    NoticeDTO getNoticeByNo(
            @Param("boardNo") int boardNo
    );

    int insertNotice(NoticeDTO dto);

    int updateNotice(NoticeDTO dto);

    int deleteNotice(
            @Param("boardNo") int boardNo
    );

    int deleteNotices(
            @Param("boardNoList") List<Integer> boardNoList
    );
}