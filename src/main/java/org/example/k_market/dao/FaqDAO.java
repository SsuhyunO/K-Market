package org.example.k_market.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.k_market.dto.admin.FaqDTO;

import java.util.List;

@Mapper
public interface FaqDAO {

    int getTotalCount(
            @Param("category1") String category1
    );

    List<FaqDTO> getFaqList(
            @Param("offset") int offset,
            @Param("pageSize") int pageSize,
            @Param("category1") String category1
    );

    FaqDTO getFaqByNo(
            @Param("boardNo") int boardNo
    );

    int insertFaq(FaqDTO dto);

    int updateFaq(FaqDTO dto);

    int deleteFaqs(
            @Param("boardNoList") List<Integer> boardNoList
    );

    List<FaqDTO> getFaqList(int offset, int pageSize);
}