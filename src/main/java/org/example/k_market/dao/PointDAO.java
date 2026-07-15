package org.example.k_market.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.k_market.dto.PointDTO;

import java.util.List;

@Mapper
public interface PointDAO {
    void insertPoint(PointDTO point);
    int selectPointBalance(String memberUid);
    void updateMemberPointBalance(String memberUid);
    List<PointDTO> selectPointHistory(String memberUid);
    int selectPointCount(@Param("searchType") String searchType,
                         @Param("keyword") String keyword);

    List<PointDTO> selectPointList(@Param("searchType") String searchType,
                                   @Param("keyword") String keyword,
                                   @Param("offset") int offset,
                                   @Param("limit") int limit);
    void deletePoints(@Param("pointNos") List<Integer> pointNos);
    List<PointDTO> selectRecentEarnedPoints(@Param("memberUid") String memberUid, @Param("limit") int limit);
}
